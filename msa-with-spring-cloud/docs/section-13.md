# 장애 처리와 Microservice 분산 추적

각각의 서비스에서 문제가 생겼을시에 어떻게 처리해야하는지 어떠한 서비스가 문제가 생겼고 해당 서비스의 시작점, 반환 값에 대한 흐름 추적도 필요

### CircuitBreaker 와 Resilience4j의 사용 


![image](https://user-images.githubusercontent.com/83503188/196357783-ade60bfa-ed49-48a4-9808-9a326902e6ec.png)

![image](https://user-images.githubusercontent.com/83503188/196358010-983db0e3-4982-40c3-b388-3250730c0fa3.png)
-
- 다음과 같이 user-service에 사용자의 정보를 요청했으나 timeout 오류가 발생한 경우
- 로그를 확인하면 order-service에서 문제가 발생했음을 알 수 있다. 

![image](https://user-images.githubusercontent.com/83503188/196358279-9b59bcc6-4aba-45db-a0e3-1ed11b97c040.png)

- 위와 같이 user-service에 요청을 보내고 user-service에서 다른 microservice를 부르는 과정에서 오류가 발생하는 경우
- user-service에서 발생하는 오류가 아님에도 불구하고 user-service의 응답결과로 500에러를 만나게된다.
- 이를 해결하기 위해서 order-service, catalog-service로 문제가 발생하는 경우에 더 이상 요청을 전달하지 않아야 한다. 
- feign client 측에서는 임시로 문제가 발생했을경우에 에러를 대신할 수 있는 default값 또는 우회할 수 있는 값을 보여주는게 user-service에 준비가 되있어야한다.

![image](https://user-images.githubusercontent.com/83503188/196358814-c1105404-3702-4b82-8f34-b113574bd360.png)

- 따라서 order-service 또는 catalog-service에서 문제가 발생하더라도 user-service에 문제가 없었다면 정상적인 200 반환을 해야한다. 

![image](https://user-images.githubusercontent.com/83503188/196358981-ff43465f-75ab-445a-8fe9-ccce4b079fd9.png)

### CircuitBreaker

- 문제가 생긴 서비스나 함수를 더 이상 사용하지 않도록 막아주고 문제가 생긴 서비스를 재사용할 수 있는 상태로 복구가 된다고하면 이전처럼 정상적인 흐름으로 변경하는 장치
- 장애가 발생하는 서비스에 반복적인 호출이 되지 못하게 차단
- 특정 서비스가 정상적으로 동작하지 않을 경우 다른 기능으로 대체 수행 -> 장애 회피

![image](https://user-images.githubusercontent.com/83503188/196359433-24c2714c-f350-4e66-b2ef-66749684f227.png)

![image](https://user-images.githubusercontent.com/83503188/196359697-e0faeef7-36c2-43b9-9f9b-18702db52d48.png)

- open: 특별한 이유에 의해서 정상적인 서비스가 불가능한 경우에는 일정한 수치 이상에 도달했을때 (30초안에 10번의 호출 시 절반 이상 실패, ...) CircuitBreaker가 open 상태가 되어서 최종적인 마이크로서비스에 전달하지 않는 상태
- closed: Circuit Breaker가 닫혔다고 하는 것은 정상적으로 다른 마이크로서비스를 사용할 수 있는 상태

spring cloud 2020버전 이전에는 CircuitBreak를 사용하기 위해서 Spring Cloud Netflix Hystrix를 사용했다.

![image](https://user-images.githubusercontent.com/83503188/196360670-8585d28d-4f88-46bf-95ef-12c649bcb474.png)


![image](https://user-images.githubusercontent.com/83503188/196361041-2ff821b4-61cb-4afd-bd5a-90b16a9ecd0f.png)
- 2019년도 이후에는 hystrix가 더 이상 개발되지 않고 대체할 수 있는 라이브러리인 Resilience4j를 사용
- Resilience4j는 circuitbreaker, ratelimiter, bulkhead, retry, timelimiter, cache를 제공한다.

각각의 aplication에 spring-cloud-circuitbreaker-resilience4j 추가

**Resilience4JConfiguration**

![image](https://user-images.githubusercontent.com/83503188/196362658-aff6ddfe-13a1-4032-9eee-e24e922f153c.png)


- CircuitBreakerFactory를 기본값으로 사용하는것이 아닌 임의로 커스터마이징 하기위해서는 `Customizer<Resilience4JCircuitBreakerFactory>`
- `failureRateRhreshold(4)`: CircuitBreaker를 열지 결정하는 실패 확률 -> default=50 -> 현재는 100번 중 4번
- `waitDurationInOpenState(Duration.ofMillis(1000))`: CircuitBreaker를 open한 상태를 유지하는 지속 기간을 의미, 이 기간 이후에 half-open 상태 -> default: 60초
- `slidingWindowType(...)`: CircuitBreaker가 닫힐 때(정상적인 작업이 수행가능한 상태) 통화 결과를 기록하는 데 사용되는 슬라이딩 창의 유형을 구성, 카운트 기반 또는 시간 기반 -> default: 횟수 기반
- `slidingWindowSize(2)`: CircuitBreaker가 닫힐 때 호출 결과를 기록하는 데 사용되는 슬라이딩 상의 크기를 구성 -> default: 100

![image](https://user-images.githubusercontent.com/83503188/196363882-f342a3fe-fe52-4e33-ae9d-b4ab202eea92.png)


- TimeLimiter 추가
- `timeoutDuration(...)`: 서플라이어(Order-Serive)가 어느정도 문제가 생겼을 경우 오류로 간주할지 정하는 설정, Time Limiter는 future supplier의 time limit을 정하는 API -> default: 1초

### Users Microservice에 CircuitBreaker 적용

Order Service를 기동하지 않은 상태에서 user 정보 get

![image](https://user-images.githubusercontent.com/83503188/196375741-09631af6-5954-40f8-a12e-5be460a38488.png)





**UserServiceImpl**

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Environment env;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        // 매퍼가 매칭시킬 수 있는 환경 설정 정보 지정
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        ...

        /* ErrorDecoder */
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitbreaker.run(() ->
                        orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>()
        );

        userDto.setOrders(orderList);

        return userDto;
    }
...
}
```
- throwable을 통해 요청에 실패했을 경우 반환할 값 명시

![image](https://user-images.githubusercontent.com/83503188/196378676-61653a52-2d0c-492b-9b3c-3237723c1ebd.png)
- Order Service가 기동중이지 않고도 User 정보는 반환되고 있다.



**CircuitBreaker를 커스텀 - Resilience4jConfig**

```java
@Configuration
public class Resilience4jConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(2) // 2번의 카운트가 마지막에 저장된다.
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                .build();
        
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()

        );

    }
}

```

Order Service 기동 - 주문 1개 생성 후 user 정보 get

![image](https://user-images.githubusercontent.com/83503188/196383616-876c13b4-7a1b-48e5-8765-9c5802c5f861.png)

Order Service 중지

![image](https://user-images.githubusercontent.com/83503188/196383851-bdef5b21-4efb-47ef-ac3d-9d6a8ac96c1e.png)

### 분산 추적의 개요 Zipkin 서버 설치

마이크로서비스가 독립적으로 자체적인 서비스가 작동하는 것이 아닌 연쇄적으로 여러개의 서비스가 실행되는 과정에서 해당하는 요청 정보가 어떻게 실행되고 어느단계를 거쳐서 어느 마이크로서비스로 이동되는지 추적할 수 있는 Zipkin에 대해서 알아본다.

#### Zipkin

- https://zipkin.io/
- Twitter에서 사용하는 분산 환경의 Timing 데이터 수집, 추적 시스템 (오픈소스)
- Google Drapper에서 발전하였으며, 분산환경에서의 시스템 병목 현상 파악
- Collector, Query Service, Database WebUI로 구성
- Span
    - 하나의 요청에 사용되는 작업의 단위
    - 64 bit unique ID
- Trace
    - 트리 구조로 이뤄진 span 셋
    - 하나의 요청에 대한 같은 Trace ID 발급


![image](https://user-images.githubusercontent.com/83503188/197159608-d59a7502-75a4-411b-a8c7-c8efcb291707.png)
- 모든 Microservice는 Zipkin에 데이터를 전달한다.

#### Spring Cloud Sleuth

Zipkin과 연동하여 로그파일 데이터, 스트리밍 데이터값을 Zipkin에 전달시켜주는 역할

- 스프링 부트 애플리케이션을 Zipkin과 연동
- 요청 값에 따른 Trace ID, Span ID 부여
- Trace와 Span Ids를 로그에 추가 가능
    - servlet filter
    - rest template
    - scheduled actions
    - message channels
    - feign client


![image](https://user-images.githubusercontent.com/83503188/197162114-273b797b-d074-44d3-9f0b-aaf0aa393c1b.png)

사용자의 요청이 시작되고 끝날 때까지 같은 Trace ID가 사용되고 그 사이에서 마이크로서비스 간의 Transaction 이 발생한다면 세부적인 Transaction을 위해 Span ID가 발급된다.

### Spring Cloud Sleuth + Zipkin을 이용한 Microservice의 분산

Users Microservice 수정

라이브러리 추가
- spring-cloud-starter-sleuth
- spring-cloud-starter-zipkin

**application.yml**

zipkin 서버 위치 지정

```yml
spring:
  application:
    name: user-service
  zipkin:
    base-url: http://localhost:9411 # zipkin server 위치
    enabled: true # 작동 가능하도록
  sleuth:
    sampler: 
      probability: 1.0 # 발생된 로그를 어느정도의 빈도수를 가지고 zipkin에 전달할지 -> 현재 1.0은 전부 전달 == 100퍼센트
...
```




**UserServiceImpl**

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    ...
    @Override
    public UserDto getUserByUserId(String userId) {
        ...

        /* ErrorDecoder */
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        log.info("Before call orders microservice");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitbreaker.run(() ->
                        orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>()
        );
        log.info("After called orders microservice");

        userDto.setOrders(orderList);

        return userDto;
    }
...
}
```

Orders Microservice 수정

라이브러리 추가
- spring-cloud-starter-sleuth
- spring-cloud-starter-zipkin

```yml
spring:
  application:
    name: order-service
  zipkin:
    base-url: http://127.0.0.1:9411
    enabled: true
  sleuth:
    sampler:
      probability: 1.0
```

**OrderController**

```java
@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    ...

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add orders data");
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
        OrderDto createdOrder = orderService.createOrder(orderDto);
        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class);

        /* Send an order to the Kafka */
//        kafkaProducer.send("example-order-topic", orderDto);

        /* kafka */
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());
//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);

//        kafkaProducer.send("example-catalog-topic", orderDto); // order와 catalog를 연동하기 위한 kafka producer
//        orderProducer.send("order", orderDto); // 사용자의 주문 정보를 kafka topic에 전달시키는 용도

        log.info("After added orders data");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");

        List<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        orderList.forEach(v ->
                result.add(mapper.map(v, ResponseOrder.class))
        );
        log.info("After retrieved orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
```

**Order Create**

```text
2022-10-21 19:44:32.680  INFO [order-service,bf3e9de37aa6d463,bf3e9de37aa6d463] 2836 --- [o-auto-1-exec-1] d.y.o.controller.OrderController         : Before add orders data
2022-10-21 19:44:32.855  INFO [order-service,bf3e9de37aa6d463,bf3e9de37aa6d463] 2836 --- [o-auto-1-exec-1] d.y.o.controller.OrderController         : After added orders data
2022-10-21 19:44:45.756  INFO [order-service,f54fa2d21903e8ff,f54fa2d21903e8ff] 2836 --- [o-auto-1-exec-2] d.y.o.controller.OrderController         : Before add orders data
2022-10-21 19:44:45.762  INFO [order-service,f54fa2d21903e8ff,f54fa2d21903e8ff] 2836 --- [o-auto-1-exec-2] d.y.o.controller.OrderController         : After added orders data
```
- [order-service,bf3e9de37aa6d463,bf3e9de37aa6d463]: [서비스명, trace ID, span ID]

Zipkin 확인

![image](https://user-images.githubusercontent.com/83503188/197178700-86d9918e-8968-4af7-9ef3-793f3a428e6d.png)

**User 정보 Get - User Service**


```text
2022-10-21 19:48:32.992  INFO [user-service,29d72753d32799ff,29d72753d32799ff] 18408 --- [o-auto-1-exec-3] d.y.userservice.service.UserServiceImpl  : Before call orders microservice
2022-10-21 19:48:33.043 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] ---> GET http://ORDER-SERVICE/order-service/0a6ca6c9-4f13-498d-b288-462880674fd9/orders HTTP/1.1
2022-10-21 19:48:33.044 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] ---> END HTTP (0-byte body)
2022-10-21 19:48:33.385 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] <--- HTTP/1.1 200 (341ms)
2022-10-21 19:48:33.385 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] connection: keep-alive
2022-10-21 19:48:33.385 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] content-type: application/json
2022-10-21 19:48:33.385 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] date: Fri, 21 Oct 2022 10:48:33 GMT
2022-10-21 19:48:33.385 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] keep-alive: timeout=60
2022-10-21 19:48:33.386 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] transfer-encoding: chunked
2022-10-21 19:48:33.386 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] 
2022-10-21 19:48:33.386 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] [{"productId":"CATALOG-001","qty":10,"unitPrice":1500,"totalPrice":15000,"createdAt":"2022-10-21T10:44:32.825+00:00","orderId":"8ddc3bf8-7cc3-4bab-a9eb-bebb4436123f"},{"productId":"CATALOG-002","qty":15,"unitPrice":1500,"totalPrice":22500,"createdAt":"2022-10-21T10:44:45.758+00:00","orderId":"748bf795-d095-46a0-adc1-6c226d99a7a5"}]
2022-10-21 19:48:33.386 DEBUG [user-service,29d72753d32799ff,1bca40a3cc6058e6] 18408 --- [pool-4-thread-1] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] <--- END HTTP (333-byte body)
2022-10-21 19:48:33.400  INFO [user-service,29d72753d32799ff,29d72753d32799ff] 18408 --- [o-auto-1-exec-3] d.y.userservice.service.UserServiceImpl  : After called orders microservice
```
**User 정보 Get - Order Service**

```text
2022-10-21 19:48:33.207  INFO [order-service,29d72753d32799ff,f0b2459b5563fa8a] 2836 --- [o-auto-1-exec-4] d.y.o.controller.OrderController         : Before retrieve orders data
2022-10-21 19:48:33.374  INFO [order-service,29d72753d32799ff,f0b2459b5563fa8a] 2836 --- [o-auto-1-exec-4] d.y.o.controller.OrderController         : After retrieved orders data
```
- feign 클라이언트에서 order service를 호출하면서 생성한 trace ID와 order service에서 확인할 수 있는 trace ID가 동일하다. -> 같은 요청임을 확인

Zipkin 확인

![image](https://user-images.githubusercontent.com/83503188/197179183-3cbf9299-4f99-4237-b350-35f79b938d26.png)

Zipkin Dependency 확인

![image](https://user-images.githubusercontent.com/83503188/197179995-8c6f04a8-9968-4cff-9689-6e1338c04a78.png)


**강제 오류 발생 - OrderController**



```java
@RestController
@RequestMapping("/order-service")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    ...

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception {
        log.info("Before retrieve orders data");

        List<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        orderList.forEach(v ->
                result.add(mapper.map(v, ResponseOrder.class))
        );

        try {
            Thread.sleep(1000);
            throw new Exception("장애 발생");
        }catch (InterruptedException e) {
            log.warn(e.getMessage());
        }

        log.info("After retrieved orders data");

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
```

1건 주문 후 확인

![image](https://user-images.githubusercontent.com/83503188/197180649-39b808e0-eaa5-4e52-8297-8ac8b2c1ce38.png)

- 주문이 되었지만 예외를 발생시켰기 때문에 주문이 확인되지 않는다.


```text
2022-10-21 19:58:05.262  INFO [user-service,a696c8a33515dc05,a696c8a33515dc05] 18408 --- [o-auto-1-exec-5] d.y.userservice.service.UserServiceImpl  : Before call orders microservice
2022-10-21 19:58:05.264 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] ---> GET http://ORDER-SERVICE/order-service/0a6ca6c9-4f13-498d-b288-462880674fd9/orders HTTP/1.1
2022-10-21 19:58:05.264 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] ---> END HTTP (0-byte body)
2022-10-21 19:58:06.494 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] <--- HTTP/1.1 500 (1231ms)
2022-10-21 19:58:06.495 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] connection: close
2022-10-21 19:58:06.495 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] content-type: application/json
2022-10-21 19:58:06.495 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] date: Fri, 21 Oct 2022 10:58:06 GMT
2022-10-21 19:58:06.495 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] transfer-encoding: chunked
2022-10-21 19:58:06.495 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] 
2022-10-21 19:58:06.496 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] {"timestamp":"2022-10-21T10:58:06.479+00:00","status":500,"error":"Internal Server Error","trace":"java.lang.Exception: 장애 발생\r\n\tat dev.yoon.orderservice.controller.OrderController.getOrder(OrderController.java:83)\r\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\r\n\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\r\n\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\r\n\tat java.base/java.lang.reflect.Method.invoke(Method.java:566)\r\n\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205)\r\n\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:150)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)\r\n\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808)\r\n\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\r\n\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1071)\r\n\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:964)\r\n\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\r\n\tat org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:898)\r\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:655)\r\n\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\r\n\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:764)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.springframework.cloud.sleuth.instrument.web.servlet.TracingFilter.doFilter(TracingFilter.java:68)\r\n\tat org.springframework.cloud.sleuth.autoconfig.instrument.web.TraceWebServletConfiguration$LazyTracingFilter.doFilter(TraceWebServletConfiguration.java:131)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\r\n\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)\r\n\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)\r\n\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197)\r\n\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)\r\n\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)\r\n\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135)\r\n\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)\r\n\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)\r\n\tat org.springframework.cloud.sleuth.instrument.web.tomcat.TraceValve.invoke(TraceValve.java:103)\r\n\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:360)\r\n\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399)\r\n\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)\r\n\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:890)\r\n\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1789)\r\n\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\r\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)\r\n\tat org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)\r\n\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\r\n\tat java.base/java.lang.Thread.run(Thread.java:829)\r\n","message":"장애 발생","path":"/order-service/0a6ca6c9-4f13-498d-b288-462880674fd9/orders"}
2022-10-21 19:58:06.497 DEBUG [user-service,a696c8a33515dc05,15ea90e9a52e816c] 18408 --- [pool-4-thread-2] d.y.u.client.OrderServiceClient          : [OrderServiceClient#getOrders] <--- END HTTP (5823-byte body)
2022-10-21 19:58:06.520  INFO [user-service,a696c8a33515dc05,a696c8a33515dc05] 18408 --- [o-auto-1-exec-5] d.y.userservice.service.UserServiceImpl  : After called orders microservice
```

Zipkin 확인

![image](https://user-images.githubusercontent.com/83503188/197180882-8d728940-b998-4390-953a-2de94124d19c.png)

Zipkin Dependency 확인

![image](https://user-images.githubusercontent.com/83503188/197180980-8907d971-caa5-4d47-987a-11751722a07c.png)
- Error가 추가되었다.

> 각각의 마이크로서비스가 현재 가지고있는 메모리 상태, 호출된 정확한 횟수, ... 파악하기 위해서는 추가적으로 모니터링 기능을 넣으면 된다. 


