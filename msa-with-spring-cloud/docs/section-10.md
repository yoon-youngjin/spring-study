# Microservice간 통신

### Communication Types

동기 방식, 비동기 방식으로 communication 가능

- Synchronous HTTP communication
- Asynchronous communication over AMQP

![image](https://user-images.githubusercontent.com/83503188/194596514-3d2b046e-7e2b-4e71-9ba1-d626fba9c386.png)

- 사용자 정보를 반환할 때 주문 정보도 포함하여 전달해주고 싶은 상황
- Eureka Discovery Service에 Order service 2개가 등록되었다고 가정
- 클라이언트의 요청에 의해 User Service가 작동하여 Order Service로부터 데이터를 가져와야 하는 상황이다. 
- User serivce는 Eureka Server를 통해 Order Service의 정보를 받아서 직접 호출

### Rest Template 사용

**Users Service -> Order Service**

**RestTemplate 빈 등록**

```java
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

  ...
    
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}

```

**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Environment env;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    ...

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
        ResponseEntity<List<ResponseOrder>> orderListResponse =
                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ResponseOrder>>() {
                        });

        List<ResponseOrder> orderList = orderListResponse.getBody();
        userDto.setOrders(orderList);

        return userDto;
    }

...

  
}

```
- `new ParameterizedTypeReference<List<ResponseOrder>>()`: 반환 받을 타입에 대한 정보

![image](https://user-images.githubusercontent.com/83503188/194603336-6ce0fce6-52c4-4887-b04e-74c0dbbdf2d6.png)

- order-service의 `getOrder()`의 반환 타입과 동일하게 


**remote repository: user-service.yml**

```yml
order_service:
  url: http://127.0.0.1:8000/order-service/%s/orders
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/194602536-874aa5f0-1dcd-4796-99b1-fc694120caff.png)


**user-service.yml IP 내용 변경**

![image](https://user-images.githubusercontent.com/83503188/194604214-9e749625-a384-4d97-9f1c-2954012be239.png)

```java
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

  ...

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}

```

- RestTemplate 빈에 `@LoadBalanced` 어노테이션을 부착함으로써 주소체계를 사용하지않고 마이크로 서비스 이름으로 대체할 수 있다.

**remote repository: user-service.yml**

```yml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: sa
    password: '{cipher}AQAGAHR558KD3Y6dyjTaKvLwsMa5G4FdTSttuvxS6YN5qc5MYJB/LV41k18K+QgKOkKp6GfnfrqkUqqzPmUoV6M5Ms11JnpRQ5arsmY5oo+WOXNmUT+PAKWZ051atYngI6gNURNTYw2eHWzTqgywzgTDLj1NT8X94q3Ir5KuWeJMLCIzw442zUpPcDvfpl4YSE2n1KZ2vqncslNDfqPxeZGMJDKceIRspMRtnBbrtNA6hvqsX2XFYPQlf7HpBZ4XorMcLe+Ki+GJKcWJYE/4YPkgpBuNlwK9Y4WQQPqbAI7ghfAqmADyJSkFQ+nG+S0ho0lmmgJcTw7EwSkL/vd3Br6k3u0kCBa9Mylrp9tyRBxwDQMvR3bRDHAKDiils0pu8wQ='
  
# token:
#   expiration_time: 86400000 # 만료 기간 -> 하루
#   secret: '{cipher}AQBr7zVkg9ilfUqJYsHQag/30IeJ0V7rfEnYFdviZP4OD2giykMG4o8cXH0JyFuCEqJCnp4V3Nm1/KH4GwAKddRH010ggTo3ltvBtcTRCs1hKaWlcn0NFB28/Ri7n83QRZou2V2FUsOoM52meynjZ6I9i6grDfzMVQXm8TtXe47xqmeLp0vT1vxZYVfnHWOeFn+0Iix+W70dtsSm+1A9FvQsaqVqkfoB0ECue0JriG9LbLXjYvFg/eo08clL5SAIqY46uLATBc2teN30v1nv5nZ+u8DAgVFkiIhN2nalaNprji9nsP4JHsHaJ+RpgsFY7BZThkXKxlRQNfkzf8TbONiIBvCx90YgQWn/LL5lDQ6x5qCAeiBPlyo5j/09bLHAGtE=' # 토큰 키
# gateway:
#   ip: 127.0.0.1

order_service:
  url: http://ORDER-SERVICE/order-service/%s/orders

```

### Feign Client 사용 

- FeignClient -> HTTP Client
  - REST Call을 추상화 한 Spring Cloud Netflix 라이브러리
- 사용방법
  - 호출하려는 HTTP Endpoint에 대한 Interface를 생성
  - `@FeignClient` 선언
- Load balaned 지원

라이브러리 추가
- spring-cloud-starter-openfeign

```java
@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication { }
```
- `@EnableFeignClients`: User Service 자체가 FeignClient를 사용할 수 있다는 선언

**OrderServiceClient**

```java
@FeignClient(name = "ORDER-SERVICE")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);

}

```

- `@FeignClient(name = "ORDER-SERVICE")`: name은 호출하고자하는 마이크로서비스 이름 -> 유레카에 등록된 마이크로서비스 중에서 이름이 "ORDER-SERVICE"인 마이크로서비스 검색



**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Environment env;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrderServiceClient orderServiceClient;

    ...

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<>() {
//                        });

//        List<ResponseOrder> orderList = orderListResponse.getBody();

        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        userDto.setOrders(orderList);

        return userDto;
    }
...
}
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/194692512-33318590-df7d-4e6e-8ed7-8559abe41753.png)

### Feign Client 예외 처리

#### Feign Client에서 로그 사용

FeignClient 사용 시 발생한 로그 추적

**application.yml**

```yml
logging:
  level:
    dev.yoon.userservice.client: DEBUG
```

**Logger 빈 추가**


```java
@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

    ...

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

**결과**

![image](https://user-images.githubusercontent.com/83503188/194694065-95d48094-d375-4f06-a796-936eadafaa46.png)

#### FeignException

- 잘못된 Endpoint? / 데이터 존재X ?
- 클라이언트가 user service로 사용자 정보를 요청하면 user service에서는 order service로 feign을 통해 정보를 가져오는데
- 만약 order service의 url이 잘못된 경우에는 user service에 존재하는 사용자 정보는 반환하고 주문 정보는 반환하지 않도록 예외처리를 추가

**UserServiceImpl**

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    ...

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        /* Using a resttemplate */
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<>() {
//                        });

//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using a feignclient */
        List<ResponseOrder> orderList = null;
        try {
            orderList = orderServiceClient.getOrders(userId);
        } catch (FeignException ex) {
            log.error(ex.getMessage());
        }

        userDto.setOrders(orderList);

        return userDto;
    }
...
}
```

![image](https://user-images.githubusercontent.com/83503188/194694567-73c019c0-e39a-4829-8688-b137f002c54a.png)

![image](https://user-images.githubusercontent.com/83503188/194694577-21247b03-6635-4b4f-8d96-831ae0dbe00c.png)

### ErrorDecoder를 이용한 예외 처리

#### ErrorDecoder 구현 

```java
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        switch(response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("getOrders")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }

        return null;
    }

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
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        /* Using a resttemplate */
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse =
//                restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<>() {
//                        });

//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using a feignclient */
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrders(userId);
//        } catch (FeignException ex) {
//            log.error(ex.getMessage());
//        }

        /* ErrorDecoder */
        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);
        userDto.setOrders(orderList);

        return userDto;
    }
...

}
```

![image](https://user-images.githubusercontent.com/83503188/194695178-52e89a2d-1a13-40c0-9763-d96d18839caa.png)
- 기존의 500에러가 아닌 404에러로 반환되는 모습

### 데이터 동기화 문제

하나의 마이크로서비스를 하나 이상의 인스턴스로 기동했을 때

- Order Service 2개 기동
  - Users의 요청 분산 처리
  - Orders 데이터도 분산 저장 -> 동기화 문제 발생

![image](https://user-images.githubusercontent.com/83503188/194695634-024d5100-742d-48d9-b94b-6c54a72b7e43.png)

![image](https://user-images.githubusercontent.com/83503188/194695676-df29a69a-0c0d-40e1-a7cc-0d705015b134.png)

![image](https://user-images.githubusercontent.com/83503188/194695478-d177d3ab-f9dc-4658-9204-3d292ddd2ae0.png)
- 현재 각각의 Order Service 인스턴스는 독립적이 H2 메모리 DB를 가진다. 

#### 해결방법 - 하나의 Database 사용

![image](https://user-images.githubusercontent.com/83503188/194695698-b8640e1b-9430-472b-8bd8-8e893df740a6.png)

#### 해결방법 - Database 간의 동기화

![image](https://user-images.githubusercontent.com/83503188/194695825-c1c53ba7-dea6-4cb3-ac86-864779337705.png)
- 각각의 인스턴스는 자체 DB를 가지고 Kafka, RabbitMQ와 같은 Message Queuing Server를 이용해서 데이터를 동기화한다.

### 해결방법 - Kafka Connector + DB

![image](https://user-images.githubusercontent.com/83503188/194696043-2ff9340f-1699-42be-b11d-fa5bca5e1284.png)

- 해결방법 1과 해결방법 2를 둘 다 사용하는 방법
- DB 커넥션에 드는 비용을 줄일 수 있다.
- 각각의 인스턴스가 DB로 접근하는 것이 아닌 Message Queueing Server에 보내고 Message Queueing Server에서 DB로 전달하고 각각의 인스턴스는 DB로 접근하여 데이터를 가져온다면 하나의 DB이므로 동시성 문제를 해결할 수 잇다.
- Message Queueing Server는 데이터 처리에 특화된 시스템이므로 순차적으로 메시지를 사용하고자하는 곳에 바로 전달하는 능력이 된다.
- 아무리 많은 데이터가 들어온다하더라도 1초에 수만건 데이터를 처리할 수 있도록 구성된 기술 -> 동시성 문제 해결가능



