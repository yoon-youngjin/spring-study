# Configuration Service

yml 파일 내용이 변경되면 애플리케이션 자체를 다시 빌드, 배포해야하는 단점을 설정 파일을 외부 시스템에서 관리하도록 변경하여 해결

### Spring Cloud Config

- 분산 시스템에서 서버 클라이언트 구성에 필요한 설정 정보(application.yml)를 외부 시스템에서 관리
- 하나의 중앙화 된 저장소에서 구성요소 관리 가능
- 각 서비스를 다시 빌드하지 않고, 바로 적용 가능
- 애플리케이션 배포 파이프라인을 통해 DEV(개발)-UAT(테스트)-PROD(운영) 환경에 맞는 구성 정보 사용
  - 각 환경에 따라서 설정 정보가 다를 수 있다. 

![image](https://user-images.githubusercontent.com/83503188/193601215-d343b670-4078-4e26-951b-4083aea36209.png)

- 구성 정보를 파일관리시스템에 저장한 뒤 Cloud Config Server가 가져와서 Microserive 에 데이터(설정 정보)를 전달해주는 과정을 거친다.
- 동적으로 어플리케이션의 구성 정보를 변경할 수 있다. 

![image](https://user-images.githubusercontent.com/83503188/193603145-86283fe2-3b12-49a9-8c54-c0f4d545b86c.png)

### Local Git Repository

git-local-repo 디렉토리 생성

**ecommerce.yml**

```yml
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key # 토큰 키
gateway:
  ip: 127.0.0.1
```
- git add .
- git commit -m "upload an application yaml file"

깃은 Local repo와 remote repo로 나눠져있는데, add 커맨드를 통해 추적관리를 시작

commit을 통해 Local repo에 등록 push를 진행하면 Remote repo로 등록하여 서버와 로컬을 동기화한다.

commit만 진행하면 Local repo에만 등록하는 것이다.

#### 우선순위

설정파일 Repo에 존재하는 yml파일의 우선순위를 지정할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/193608132-68039768-f3fb-4c41-9c83-95705c651cc9.png)

해당 마이크로서비스들은 어떤 설정 파일을 사용할 것인지에 대해서 명시한다.-> user-service.yml / profile: prod -> user-service-prod.yml

![image](https://user-images.githubusercontent.com/83503188/193608924-e45ddd42-d553-487b-b5fc-efb243b09809.png)

### Spring Cloud Config - 프로젝트 생성

- 라이브러리
  - Config Server

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
```

**application.yml**
```yml
server:
  port: 8888

spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: file:///C:\Users\dudwl\WorkSpace\SSS\msa-with-spring-cloud\git-local-repo

```

### User Microservice에서 Spring Cloud Config 연동

- 라이브러리 추가
  - spring-cloud-starter-config
  - spring-cloud-starter-bootstrap

- bootstrap.yml 추가
  - `application.yml`보다 우선순위가 높은 파일
  - 읽어오고자하는 설정 정보의 위치 저장

**bootstrap.yml**

```yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce
```

**userController**

```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"
                        + ", port(local.server.port)=" + env.getProperty("local.server.port")
                        + ", port(server.port)=" + env.getProperty("server.port")
                        + ", token secret=" + env.getProperty("token.secret")
                        + ", token expiration time=" + env.getProperty("token.expiration_time"));
    }
...
}

```

![image](https://user-images.githubusercontent.com/83503188/193614263-ba81a845-6002-4691-995c-87a36b7ef0f7.png)

![image](https://user-images.githubusercontent.com/83503188/193615008-2ec3f6ec-5a0b-4a2e-ad2e-5e69d1edfb6a.png)

#### Changed configuration values

Config Server는 언제든지 변경될 수 있는 상태 

**정보 변경 시 다시 가져오는 3가지 방법**

1. 서버 재기동 -> 의미 없음
2. Actuator refresh
3. Spring cloud bus 사용 ** -> 다음 과정

#### Spring Boot Actuator

- Application 상태, 모니터링
- Metric 수집을 위한 Http End Point 제공

- 라이브러리 추가
  - spring-boot-starter-actuator

**WebSecurity**

WebSecurity에서 `/actuator/**`에 대한 요청 인증 허가

```java
@Configuration // 다른 빈보다 등록 우선순위가 높아진다.
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env; // 설정 정보의 JWT 정보를 가져오기 위한 빈
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    static final String IP = "127.0.0.1";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress(IP)
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable(); // h2-console 에 접근하기 위한 disable
    }
...
}
```

**application.yml**

```yml
...
management:
  endpoint:
    web:
      exposure:
        include: refresh,health,beans
```

**설정 정보 변경** -> **post-127.0.0.1/{port}/actuator/refresh**

**ecommerce.yml**

```yml
token:
  expiration_time: 864 # 만료 기간 -> 하루
  secret: secret-key2 # 토큰 키
gateway:
  ip: 127.0.0.1

```


![image](https://user-images.githubusercontent.com/83503188/193621507-605f23b3-0528-4c6d-be30-631128af7849.png)

![image](https://user-images.githubusercontent.com/83503188/193621697-6aff7a35-fe67-4ecf-8825-b1f4032d7230.png)

### Spring Cloud Gateway에서 Spring Cloud Config 연동 

- 라이브러리 추가
  - spring-boot-start-config
  - spring-boot-start-bootstrap
  - spring-boot-start-actuator

**bootstrap.yml** 

```yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce
```


**application.yml**

```yml
- id: user-service
  uri: lb://USER-SERVICE
  predicates:
    - Path=/user-service/actuator/**
    - Method=GET, POST
  filters:
    - RemoveRequestHeader=Cookie
    - RewritePath=/user-service/(?<segment>.*), /$\{segment}

...
management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans, httptrace
```

- `httptrace`: 클라이언트 요청이 들어와서 스프링부트에 구성되어있는 각각의 Microservice들의 호출, 처리되는 상태같은 tracing을 확인할 수 있는 기능


```java
@Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
```

- 위와 같이 `HttpTraceRepository` 를 빈으로 등록하게 되면 클라이언트가 요청했던 트레이스 정보가 메모리에 담겨서 필요할 때 엔드포인트로 확인할 수 있다.

![image](https://user-images.githubusercontent.com/83503188/193625215-26e95203-4662-427b-855e-5a6cd3020c06.png)

### Profiles을 사용한 Configuration 적용

![image](https://user-images.githubusercontent.com/83503188/193626288-b70881ff-9da2-4cca-b9f8-75ac802ad401.png)


설정 파일 추가 

**ecommerce-dev.yml**

```yml
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key-dev # 토큰 키
gateway:
  ip: 127.0.0.1

```
**ecommerce-prod.yml**

```yml
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key-prod # 토큰 키
gateway:
  ip: 127.0.0.1

```

`bootstrap.yml`을 통해서 Config Server 로부터 가져올 profile 설정


**user-service - bootstrap.yml**

```yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce

  profiles:
    active: dev
```

**apigateway-service - bootstrap.yml**

```yml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce

  profiles:
    active: prod
```

![image](https://user-images.githubusercontent.com/83503188/193628332-8e3773d6-cbd1-419f-bbb4-d8aace0937d5.png)

### Remote Git Repository

`git remote add origin https://github.com/yoon-youngjin/spring-cloud-config.git`

**config-service - application.yml**

```yml
server:
  port: 8888

spring:
  application:
    name: config-service
  cloud:
    config:
      server:
        git:
          uri: https://github.com/yoon-youngjin/spring-cloud-config

```
- private repo라면 username, password 명시

![image](https://user-images.githubusercontent.com/83503188/193629896-1985ad92-2c07-4908-ab25-4b58b13c4a9e.png)

### Native File Repository

깃을 사용하는 것이 아닌 Local 파일 시스템을 이용하는 방법

**bootstrap.yml**

```yml
server:
  port: 8888

spring:
  application:
    name: config-service
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: file:///C:\Users\dudwl\WorkSpace\spring-cloud-config
        git:
          uri: https://github.com/yoon-youngjin/spring-cloud-config
```

![image](https://user-images.githubusercontent.com/83503188/193630837-9c552b1b-013d-424c-94e3-cfe73b51006d.png)
