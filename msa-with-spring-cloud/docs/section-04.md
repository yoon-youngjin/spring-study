# Users Microservice - 1

![image](https://user-images.githubusercontent.com/83503188/193068451-80fcf247-8b77-4e2a-b5e2-05a68d8268e2.png)

**Features**
- 신규 회원 등록
- 회원 로그인
- 상세 정보 확인
- 회원 정보 수정/삭제
- 상품 주문
- 주문 내역 확인

Apis

![image](https://user-images.githubusercontent.com/83503188/193069009-e8b7b639-609d-49c7-8cbf-65320f422a1d.png)

### Users Microservice - 프로젝트 생성


**라이브러리 추가**
- Lombok
- H2
- Spring Boot DevTools: 웹 앱을 수정한 뒤 종료했다가 다시 키지않아도 reload해주는 기능 포함된 라이브러리
- Spring Web
- Eureka Discovery Client
- JPA
- Model Mapper
- Spring Security

**Eureka 서버에 등록**

```java
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}

```

**Configuration 정보 추가**

```yml
server:
  port: 0

spring:
  application:
    name: user-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: h2-console

eureka:
  instance:
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka

greeting:
  message: Welcome to the Simple E-Commerce.
```

**해당 설정 정보를 사용하는 2가지 방법**
1. Environment 사용
2. @Value 사용

#### Environment 사용

**UserController**

```java
@RestController
@RequestMapping("/")
public class UserController {

    Environment env;

    @Autowired
    public UserController(Environment env) {
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }
}
```

#### @Value 사용

**Greeting.java**

```java
@Component
@Data
public class Greeting {

    @Value("${greeting.message}")
    private String message;
}
```

**UserController**

```java
@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private Greeting greeting;

    @GetMapping("/health_check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

}

```

### Users Microservice - H2 데이터베이스 연동

```yml
server:
  port: 0

spring:
  application:
    name: user-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console

...
```

### User Microservice - 사용자 추가

**회원 가입**

![image](https://user-images.githubusercontent.com/83503188/193078945-d08935a7-a475-43e5-bb85-e0ba67138831.png)


**RequestUser**
- 사용자의 요청으로 들어올 객체

```java
@Data
public class RequestUser {

    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "name not be less than two characters")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equal or grater than 8 characters")
    private String pwd;
    
}
```

**UserDto**

- 데이터베이스에 저장하고 다른 계층으로 이동시키기 위한 용도의 객체

```java
@Data
public class UserDto {
    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createdAt;
    
    private String encryptedPwd;
}

```

**UserEntity**
- 데이터베이스로 만들어져야하는 요소

```java
@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false,unique = true)
    private String userId;

    @Column(nullable = false,unique = true)
    private String encryptedPwd;
}
```

**UserService**

```java
public interface UserService {

    UserDto create(UserDto userDto);
}

```

**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        // 매퍼가 매칭시킬 수 있는 환경 설정 정보 지정
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPwd("encrypted_password");

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);
        return returnUserDto;
    }
}
```

**UserController**

```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;

   ...

    @PostMapping("/users")
    public String createUser(@RequestBody RequestUser user) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.create(userDto);

        return "Create user method is called";
    }

}

```

Post 에 의해서 정상적으로 데이터가 반영된 경우에는 201("created OK")라는 성공메시지가 더욱 정확한 방법이다.

**ResponseUser **
- 클라이언트 반환용 객체

```java
@Data
public class ResponseUser {

    private String email;
    private String name;
    private String userId;

}
```


```java
 @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.create(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }
```

![image](https://user-images.githubusercontent.com/83503188/193088198-1bb7b1a8-60e7-40ff-8600-b3adb91d4f0a.png)

### User Microservice - Security

- 회원가입 시 입력받은 비밀번호를 암호화해서 데이터베이스에 저장하는 작업을 진행
- Authentication + Authorization

1. step1: 애플리케이션에 spring security jar을 Dependency에 추가
2. step2: WebSecurityConfigurerAdapter를 상속받는 Security Configuration 클래스 생성
3. step3: Security Configuration 클래스에 @EnableWebSecurity 추가
4. step4: Authentication -> configure(AuthenticcationManagerBuilder auth) 메서드를 재정의 
5. step5: Password encode를 위한 BcryptPasswordEncoder 빈 정의
6. step6: Authorization -> configure(HttpSecurity http) 메서드를 재정의


**WebSecurity**

로그인은 구현되지 않았으므로 인증은 통과되었다고 가정

```java
@Configuration // 다른 빈보다 등록 우선순위가 높아진다.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.headers().frameOptions().disable(); // h2-console 에 접근하기 위한 disable
    }
}
```

- @Configuration: 다른 빈보다 등록 우선순위가 높아진다.

![image](https://user-images.githubusercontent.com/83503188/193090774-72414f09-6e05-4830-938d-b6c401d5deb3.png)

위와 같은 오류가 발생하는 이유는 h2 database는 html에 프레임별로 데이터가 나눠져 있기 때문에 무시하는 코드를 추가해야 한다.

- BCryptPasswordEncoder
  - Password를 해싱하기 위해 Bcrypt 알고리즘 사용
  - 랜덤 Salt를 부여하여 여러번 Hash를 적용한 암호화 방식 -> 같은 비밀번호가 들어와도 매번 다른 암호화 비밀번호로 저장된다.

**WebSecurity**

```java
@Configuration // 다른 빈보다 등록 우선순위가 높아진다.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.headers().frameOptions().disable(); // h2-console 에 접근하기 위한 disable
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
}

```

![image](https://user-images.githubusercontent.com/83503188/193092281-734952c3-1265-4181-b729-6b2795efb40b.png)




