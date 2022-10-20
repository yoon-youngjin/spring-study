# 장애 처리와 Microservice 분산 추적

각각의 서비스에서 문제가 생겼을시에 어떻게 처리해야하는지 어떠한 서비스가 문제가 생겼고 해당 서비스의 시작점, 반환값에 대한 흐름 추적도 필요

CircuitBreaker와 Resilience4J의 사용

![image](https://user-images.githubusercontent.com/83503188/196357783-ade60bfa-ed49-48a4-9808-9a326902e6ec.png)

다음과 같이 user-service에 사용자의 정보를 요청했으나 timeout 오류가 발생한 경우

![image](https://user-images.githubusercontent.com/83503188/196358010-983db0e3-4982-40c3-b388-3250730c0fa3.png)

로그를 확인하면 order service에서 문제가 발생했음을 알 수 있다.

![image](https://user-images.githubusercontent.com/83503188/196358279-9b59bcc6-4aba-45db-a0e3-1ed11b97c040.png)

위와 같이 user-service에 요청을 보내고 user-service에서 다른 microservice를 부르는 과정에서 오류가 발생하는 경우

user-service에서 발생하는 오류가 아님에도 불구하고 user-service의 응답결과로 500에러를 만나게된다.

이를 해결하기 위해서 order-service, catalog-service로 문제가 발생하는 경우에 더 이상 요청을 전달하지 않아야 한다.

feign client 측에서는 임시로 문제가 발생했을경우에 에러를 대신할 수 있는 default값 또는 우회할 수 있는 값을 보여주는게 user-service에 준비가 되있어야한다.

![image](https://user-images.githubusercontent.com/83503188/196358814-c1105404-3702-4b82-8f34-b113574bd360.png)

따라서 order-service 또는 catalog-service에서 문제가 발생하더라도 user-service에 문제가 없었다면 정상적인 200 반환을 해야한다.

![image](https://user-images.githubusercontent.com/83503188/196358981-ff43465f-75ab-445a-8fe9-ccce4b079fd9.png)

CircuitBreaker

- 문제가 생긴 서비스나 함수를 더 이상 사용하지 않도록 막아주고 문제가 생긴 서비스를 재사용할 수 있는 상태로 복구가 된다고하면 이전처럼 정상적인 흐름으로 변경하는 장치
- 장애가 발생하는 서비스에 반복저인 호출이 되지 못하게 차단
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

- CircuitBreakerFactory를 기본값으로 사용하는것이 아닌 임의로 커스터마이징 하기위해서는 Customizer<Resilience4JCircuitBreakerFactory>
- `failureRateRhreshold(4)`: CircuitBreaker를 열지 결정하는 실패 확률 -> default=50 -> 현재는 100번 중 4번
- waitDurationInOpenState(Duration.ofMillis(1000)): CircuitBreaker를 open한 상태를 유지하는 지속 기간을 의미, 이 기간 이후에 half-open 상태 -> default: 60초
- slidingWindowType(...): CircuitBreaker가 닫힐 때(정상적인 작업이 수행가능한 상태) 통화 결과를 기록하는 데 사용되는 슬라이딩 창의 유형을 구성, 카운트 기반 또는 시간 기반 -> default: 횟수 기반
- slidingWindowSize(2): CircuitBreaker가 닫힐 때 호출 결과를 기록하는 데 사용되는 슬라이딩 상의 크기를 구성 -> default: 100

![image](https://user-images.githubusercontent.com/83503188/196363882-f342a3fe-fe52-4e33-ae9d-b4ab202eea92.png)

- TimeLimiter 추가
- timeoutDuration(...): 서플라이어(Order-Serive)가 어느정도 문제가 생겼을 경우 오류로 간주할지 정하는 설정, TimeLimiter는 future supplier의 time limit을 정하는 API -> default: 1초

Users Microservice에 CircuitBreaker 적용

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

**CircuitBreaker를 커스텀**

**Resilience4jConfig**

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

Order Service 기동

주문 1개 생성 후 user 정보 get

![image](https://user-images.githubusercontent.com/83503188/196383616-876c13b4-7a1b-48e5-8765-9c5802c5f861.png)

Order Service 중지

![image](https://user-images.githubusercontent.com/83503188/196383851-bdef5b21-4efb-47ef-ac3d-9d6a8ac96c1e.png)
