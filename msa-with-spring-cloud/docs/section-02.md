# API Gateway Service

### API Gateway 란? 

사용자나 외부 시스템으로부터 요청을 단일화하여 처리할 수 있도록 하는 서비스

라우팅 설정에 따라서 각각의 엔드포인트로 클라이언트 대신해서 요청하고 응답 받으면 다시 클라이언트에게 전달해주는 프록시 역할

시스템의 내부구조는 숨기고 외부의 요청에 대해서 적절한 형태로 가공하여 응답할 수 있다는 장점을 가진다.

![image](https://user-images.githubusercontent.com/83503188/192714506-ebeb8cc3-9a70-4443-a7e9-414e2ddda832.png)

이와 같이 클라이언트에서 Microservice 의 주소를 통해 직접 요청을 보내는 그림

새로운 Microservice 가 추가된거나 기존 Microservice 에 변경이 생긴다면 클라이언트의 코드가 다시 수정 배포되어야 한다. - 단점

위와 같은 문제를 해결하기 위해 단일 진입점이 필요하다. 

![image](https://user-images.githubusercontent.com/83503188/192714892-1a9054ad-03cb-437a-8b66-aa29ff90b601.png)

그래서 백엔드 계층 중간에 Gateway 를 두고, 각각의 Microservice 로의 요청을 전부 처리

#### 장점 

- 인증 및 권한 부여에 대한 단일 작업
- 서비스 검색 통합
- 응답 캐싱
- 정책, 회로 차단기 및 Qos 다시 시도
- 속도 제한
- 부하 분산
- 로깅, 추적, 상관 관계
- 헤더, 쿼리 문자열 및 청구 변환
- IP 허용 목록에 추가 

#### Netflix Ribbon - Load Balancer

Spring Cloud 에서의 MSA 간 통신
1. RestTemplate
2. Feign Client

Load Balancer 를 어디에 구축할지에 대한 고민, 초창기 Spring Cloud 에서는 이러한 Load Balancer 를 해주는 서비스를 위해서 Ribbon 이라는 서비스를 제공

Ribbon: Client side Load Balancer
- 비동기 방식에서 호환 문제가 있어 최근에는 잘 사용하지 않는다.
- 서비스 이름으로 호출
- Health Check

![image](https://user-images.githubusercontent.com/83503188/192716745-d66e1d15-c754-48f2-bf93-90f9966eec6c.png)

### Netflix Zuul 

Gateway 역할을 해주는 제품

![image](https://user-images.githubusercontent.com/83503188/192717349-592336d2-0e09-4584-9406-dd1ac17a6fc9.png)


**first-service - FirstServiceController**

```java
@RestController
@RequestMapping("")
@Slf4j
public class FirstServiceController {
    
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First Service.";
    }
}
```

**first-service - application.yml**

```yml
server:
  port: 8081


spring:
  application:
    name: my-first-service

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

}
```


**second-service - SecondServiceController**

```java
@RestController
@RequestMapping("/")
@Slf4j
public class SecondServiceController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Second Service.";
    }
}
```

**second-service - application.yml**

```yml
server:
  port: 8082

spring:
  application:
    name: my-second-service

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

**zuul-service**

```java
@SpringBootApplication
@EnableZuulProxy
public class ZuulServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServiceApplication.class, args);
    }

}

```

```yml
server:
  port: 8000

spring:
  application:
    name: my-zuul-service

zuul:
  routes:
    first-service:
      path: /first-service/**
      url: http://localhost:8081
    second-service:
      path: /second-service/**
      url: http://localhost:8082
```

- `zuul.routes`: 라우팅 설정
  - first-service: 임의의 이름
  - `path`: 사용자의 요청
  - `url`: 사용자 요청에 대해서 이동할 url

![image](https://user-images.githubusercontent.com/83503188/192724670-4b1bf888-4ae7-4c42-a06c-ddad58d92dbd.png)

![image](https://user-images.githubusercontent.com/83503188/192724730-6ef2cfdc-8a35-44b5-8a48-f65e4c861421.png)

### Netflix Zuul - Filter 적용

**ZuulLoggingFilter**

```java
@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    @Override
    public Object run() throws ZuulException {
        log.info("**************** printing logs: ");

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("**************** " + request.getRequestURI());

        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
}
```

- `filterType()`: 사전 필터인지 사후 필터인지에 대한 정의, return "pre" 이므로 사전 필터,  return "post" 이면 사후 필터
- `filterOrder()`: 여러 개의 필터가 존재하는 경우 순서를 의미
- `shouldFilter()`: 현재 필터는 원하는 옵션에 따라서 필터로 사용할 수도 하지 않을 수도 있는데, return true 이므로 필터로 사용하겠다는 것
- `run()`: 실제 동작 정의
  - `RequestContext.getCurrentContext();`: 필터에는 Request 객체가 존재하지 않으므로 RequestContext 로부터 Request 정보를 가져와야 한다.
  - HttpServletRequest 를 통해 사용자의 요청 정보를 출력

![image](https://user-images.githubusercontent.com/83503188/192726940-b032f13e-1a77-4032-a31c-9a8476d65015.png)


### Spring Cloud Gateway 란?

앞에서 사용한 Netflix Zuul 대신 사용할 Gateway / Routing Service

Zuul 1.0 Service 의 기본적인 동기방식을 비동기 방식으로 처리

Zuul 의 스프링의 호환성 문제를 처리

**apigateway-service**

```yml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
```

- `cloud.gateway.routes`: 리스트 형태로 라우트 객체를 등록
- `id`: 해당 라우터의 고유값
- `url`: 포워딩될 주소
- `predicates`: 조건절
  - Path: 사용자가 입력한 path 정보가 first-service 로 시작하는 경우 

![image](https://user-images.githubusercontent.com/83503188/192730152-856990bb-c6e7-4362-9d81-e8cbded6bee6.png)

기존의 Tomcat 서버가 아닌 Netty 내장 서버가 작동된 것을 볼 수 있다. -> 비동기 방식

위 설정에서 문제점은 http://localhost:8000/first-service/welcome 이와 같이 요청이 들어오면 리다이렉트를 http://localhost:8081/first-service/welcome 로 해주기 때문에 first-service 에서 맵핑이 되지 않는다.

따라서 기존의 first, second service 의 맵핑 정보를 바꿔주면 된다. -> 이후에 필터를 통해서 사용자의 요청 정보를 변환할 수 있다. 


```java
@RestController
@RequestMapping("first-service")
@Slf4j
public class FirstServiceController { }

@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondServiceController { }
```

![image](https://user-images.githubusercontent.com/83503188/192731928-6f8405d1-5697-416f-a046-92e3e7e6c4e1.png)

### Spring Cloud Gateway - Filter

Client 가 Spring Cloud Gateway 에 요청을 전달하면 gateway 에서 First? Second?를 판단하고 서비스에 요청을 보낸다.

Gateway 의 작업을 조금 더 확대한다면 Predicate 에 의해 요청의 조건을 분기하고 사전 필터, 사후 필터를 구성(Java Code or Property(yml)를 통해)할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/192733994-14744334-53ec-47be-bfe2-f53a7c9ee174.png)

#### 먼저 필터를 자바 코드를 통해 구성

**FilterConfig.java**

```java
@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                                .addResponseHeader("first-response", "first-response-header"))
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}
```

앞에서 yml의 cloud.gateway.routes을 통해 설정한 작업을 자바 코드로 처리하는 방법이며  해당 path로 요청이 들어오면 헤더를 추가하여 uri로 포워딩 시켜준다.

**FirstServiceController, SecondServiceController**

```java
@RestController
@RequestMapping("first-service")
@Slf4j
public class FirstServiceController {
    ...

    @GetMapping("/message")
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Hello World in First Service.";
    }

}
```

```java
@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondServiceController {
    ...
    @GetMapping("/message")
    public String message(@RequestHeader("second-request") String header) {
        log.info(header);
        return "Hello World in Second Service.";
    }
}
```

spring cloud gateway 의 filter 에서 추가해준 request header 확인


![image](https://user-images.githubusercontent.com/83503188/192737022-ba21b95a-67db-4c79-b656-55e994535959.png)

![image](https://user-images.githubusercontent.com/83503188/192737219-cf477659-c831-4b9f-8da2-5b284210057c.png)

#### 필터를 설정 파일(yml) 통해 구성

```yml

...

spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=first-request, first-request-header2
            - AddResponseHeader=first-response, first-response-header2
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second-request, second-request-header2
            - AddResponseHeader=second-response, second-response-header2
```

![image](https://user-images.githubusercontent.com/83503188/192738704-e8943698-25cb-4a22-83b6-a44eab9e8916.png)

![image](https://user-images.githubusercontent.com/83503188/192739057-f7f1406c-53e9-4b08-9f6c-a577b28c1232.png)

### Spring Cloud Gateway - Custom Filter 적용 


```java
@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter: request id -> {}", request.getId());

            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());
            }));
        };
    }


    public static class Config {
        // Put the configuration properties
    }

}

```

- `apply()`: 수행하고자 하는 내용
  - chain 형태로 작동
  - 예를 들어 pre filter 에서 사용자 로그인 시 받은 JWT 를 검증할 수 있다.
  - 첫번째 매개변수인 exchange 를 통해, ServerHttpRequest, ServerHttpResponse 를 가져올 수 있다. 
    - Netty 라는 비동기 내장 서버이므로 ServletRequest, ServletResponse 가 아닌 ServerHttpRequest, ServerHttpResponse 를 사용해야 한다.
  - then()은 종료되기 전에 수행할 내용



```yml
...

spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#            - AddRequestHeader=second-request, second-request-header2
#            - AddResponseHeader=second-response, second-response-header2
            - CustomFilter
```


**FirstServiceController, SecondServiceController**

```java
@RestController
@RequestMapping("first-service")
@Slf4j
public class FirstServiceController {
    

 ...

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        log.info("Server port={}", request.getServerPort());

        log.info("spring.cloud.client.hostname={}", env.getProperty("spring.cloud.client.hostname"));
        log.info("spring.cloud.client.ip-address={}", env.getProperty("spring.cloud.client.ip-address"));

        return String.format("Hi, there. This is a message from First Service on PORT %s"
                , env.getProperty("local.server.port"));
    }

}
```

```java
@RestController
@RequestMapping("/second-service")
@Slf4j
public class SecondServiceController {
    

 ...

    @GetMapping("/check")
    public String check() {
        return "Hi, there. This is a message from Second Service.";
    }
}
```

![image](https://user-images.githubusercontent.com/83503188/192742817-824133ff-4828-44c3-a902-a2370b201a4d.png)

![image](https://user-images.githubusercontent.com/83503188/192742974-83d9ffe6-24aa-47ca-a26a-7f65fa9adccd.png)

### Spring Cloud Gateway - Global Filter 

앞에서 실습한 Custom Filter 와 만드는 방법과 동일하다. 단, 어떤 라우트 정보가 실행된다고 하더라도 공통적으로 실행되는 공통필터


```java
@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Global Filter Start: request id -> {}", request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        }));
    }

    @Data
    public static class Config {

        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }
}
```

Config.class 의 변수의 초기화는 `application.yml` 파일에서 처리

```yml

... 
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#            - AddRequestHeader=second-request, second-request-header2
#            - AddResponseHeader=second-response, second-response-header2
            - CustomFilter
      default-filters: 
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway GlobalFilter
            preLogger: true
            postLogger: true
```

- GlobalFilter는 모든 필터 중 가장 먼저 시작되고 가장 마지막 종료된다.

![image](https://user-images.githubusercontent.com/83503188/192745565-b67818e7-812d-4aaf-bc58-e76459269547.png)


뒤에서 환경 설정 정보(application.yml)을 다루는 내용을 배우는데 현재는 yml 이 프로젝트에 내장되어 있는데, 내장되어 있으므로 변경 시 값을 바꾼 뒤 다시 빌드, 배포, 패키징하는 과정을 해야한다.

yml 이 프로젝트 외부에 존재한다면 수행 중인 Microservice 는 갱신되지 않을 수 있다. 따라서 설정과 실행을 분리하는게 좋다.


### Spring Cloud Gateway - Custom Filter (Logging)

```java
@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Logging Filter Start: request uri -> {}", request.getURI());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging Filter End: response code -> {}", response.getStatusCode());
                }
            }));
        };
    }


    @Data
    public static class Config {

        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }

}
```

```yml
...
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway GlobalFilter
            preLogger: true
            postLogger: true
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#            - AddRequestHeader=second-request, second-request-header2
#            - AddResponseHeader=second-response, second-response-header2
            - name: CustomFilter
            - name: LoggingFilter
              args:
                baseMessage: Hi, there.
                preLogger: true
                postLogger: true
```

![image](https://user-images.githubusercontent.com/83503188/192747562-59986064-a554-4819-bd9c-9ce173c3f3be.png)

![image](https://user-images.githubusercontent.com/83503188/192747970-2735ce49-56ea-49fe-8538-8668d51ce41d.png)

- Gateway Handler 를 통해서 어떤 요청인지 판단
- Global -> Custom -> Logging 순서로 시작되고 Logging -> Custom -> Global을 순서로 종료된다.
- Proxied Service 는 지금 first-service 와 second-service

**람다식을 풀어서 설명**

```java
  @Override
    public GatewayFilter apply(Config config) {

        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage {}", config.getBaseMessage());
            if (config.isPreLogger()) {
                log.info("Logging Filter Start: request uri -> {}", request.getURI());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Logging Filter End: response code -> {}", response.getStatusCode());
                }
            }));

        }, Ordered.HIGHEST_PRECEDENCE);

        return filter;


    }
```

`new OrderedGatewayFilter()`: 해당 구현체는 생성자로 GatewayFilter 와 순서에 해당하는 order 를 받는다.
- 해당 구현체는 GatewayFilter 를 implements 하므로 filter 를 정의한다.
- Spring 의 Web Flux 를 사용하므로 ServerRequest, ServerResponse 를 사용해야하는데 두 가지 인스턴스를 사용하도록 도와주는 것이 ServerWebExchange 객체이다.
- GatewayFilterChain 객체를 통해 다양한 필터(pre-filter, post-filter)들을 연결해준다.

![image](https://user-images.githubusercontent.com/83503188/192819902-63c026b8-412b-48fb-a399-937ae11a56f8.png)

![image](https://user-images.githubusercontent.com/83503188/192822040-f88a6584-1d41-47c2-b132-8a6a86d389e7.png)

- 순서가 달라졌다 ?

![image](https://user-images.githubusercontent.com/83503188/192821658-8e664fda-0d0b-4218-89f8-46e54039b390.png)

- 위의 order 파라미터 때문이다. 

### Spring Cloud Gateway - Eureka 연동 

Eureka 라는 네이밍 서비스에 Spring Cloud Gateway 를 등록

![image](https://user-images.githubusercontent.com/83503188/192822887-b2955606-d02b-4570-9680-aebb8e118be0.png)

클라이언트에서 API gateway 를 통과해서 요청 정보를 보내게 되면 유레카 서버로 전달되어 해당 요청을 분석해서 마이크로서비스의 위치정보를 전달 받아서 해당 정보를 통해 포워딩이 이뤄진다.

```yml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway GlobalFilter
            preLogger: true
            postLogger: true
      routes:
        - id: first-service
          uri: lb://MY-FIRST-SERVICE
          predicates:
            - Path=/first-service/**
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: lb://MY-SECOND-SERVICE
          predicates:
            - Path=/second-service/**
```

- uri 를 보면 lb(load balancer) 뒤에 네이밍 서비스(유레카 서비스)에 등록된 인스턴스 이름을 적어줌을 볼 수 있다.


**first-service, second-service 유레카 등록**


```yml
...
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```

![image](https://user-images.githubusercontent.com/83503188/192824426-7c48fc87-0ed2-4e9f-876c-a3bd750ffe8f.png)

![image](https://user-images.githubusercontent.com/83503188/192825032-5cfef433-4a27-4170-8bb9-5e397804440b.png)

![image](https://user-images.githubusercontent.com/83503188/192825128-3e2890cc-a7c4-452a-867f-5b86e79a66a9.png)

### Spring Cloud Gateway - Load Balancer

인텔리제이에서 서버 2개 이상 기동하는 방법

![image](https://user-images.githubusercontent.com/83503188/192825473-db1d6756-996e-424a-a9cc-953249b53ee6.png)

![image](https://user-images.githubusercontent.com/83503188/192827422-aa3a1990-e51d-4655-bc49-3adc068686ff.png)

현재 first, second 서비스 모두 2개씩 기동 중인 상태 해당 url로 요청이 들어오면 어디로 로드 밸런싱?

랜덤 포트 적용

```yml
server:
  port: 0

spring:
  application:
    name: my-first-service

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
  instance:
    instance-id: ${spring.cloud.client.ip-address}:${spring.application.instance_id:${random.value}}
    prefer-ip-address: true
```

![image](https://user-images.githubusercontent.com/83503188/192828240-97abb61d-048c-43ce-a694-ea8cd871d3db.png)

```java
@RestController
@RequestMapping("first-service")
@Slf4j
public class FirstServiceController {
    Environment env;

    @Autowired
    public FirstServiceController(Environment env) {
        this.env = env;
    }

   ...

    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        
        log.info("Server port={}", request.getServerPort());

        log.info("spring.cloud.client.hostname={}", env.getProperty("spring.cloud.client.hostname"));
        log.info("spring.cloud.client.ip-address={}", env.getProperty("spring.cloud.client.ip-address"));

        return String.format("Hi, there. This is a message from First Service on PORT %s"
                , env.getProperty("local.server.port"));
    }
}
```


![image](https://user-images.githubusercontent.com/83503188/192829221-4ef1d6bf-c78b-478f-bcb0-cda77be47286.png)





