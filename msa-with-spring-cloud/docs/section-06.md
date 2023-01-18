# Users Microservice - 2

- Features
  - 신규 회원 등록 X
  - 회원 로그인
  - 상세 정보 확인 X
  - 회원 정보 수정/삭제
  - 상품 주문 - X
  - 주문 내역 확인 - X

- Apis

![image](https://user-images.githubusercontent.com/83503188/193392676-627c5fb9-c151-4114-84b4-b5b072bdfcb4.png)

![image](https://user-images.githubusercontent.com/83503188/193392734-538ca0d1-a95f-4fb8-b62e-7a7e4135ba12.png)

로그인에 성공하면 클라이언트는 서버로 부터 응답 헤더를 통해 token, userId를 반환 받는다.

### Users Microservice - AuthenticationFilter 추가

**RequestLogin**

```java
@Data
public class RequestLogin {
    
    @NotNull(message = "Email cannot be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equals or grater than 8 characters")
    private String password;
}
```

**AuthenticationFilter**

```java
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication
            (HttpServletRequest request,
             HttpServletResponse response) throws AuthenticationException {

        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>())
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
       
    }
}
```

- `RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);`: 전달되어진 InputStream 에 어떠한 값이 들어있을 때 원하는 자바 클래스 타입으로 변형,
- InputStream 으로 받은 이유는 전달받고하는 로그인의 값은 Post 형태로 전달되어  RequestParam 으로 받을 수 없기 때문에 InputStream 으로 처리한다.
- `return getAuthenticationManager().authenticate(...));`
  - 사용자가 입력한 이메일과 아이디 값을 스프링 시큐리티에서 사용할 수 있는 값으로 UsernamePasswordAuthenticationToken으로 변형
  - UsernamePasswordAuthenticationToken 값을 AuthenticationManager 에 전달하여 인증 요청
  - 토큰을 전달받은 AuthenticationManager 는 아이디와 패스워드를 통해 비교작업 수행 

**WebSecurity**

- 기존의 WebSecurity 는 모든 /users/로 들어오는 요청을 permit


```java
@Configuration // 다른 빈보다 등록 우선순위가 높아진다.
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/user-service/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.80.1") // 통과시키고자하는 IP 주소
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable(); // h2-console 에 접근하기 위한 disable
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- `http.authorizeRequests().antMatchers("/**")`: 모든 요청을 검증
  - `.hasIpAddress("192.168.80.1")`: 통과시킬 아이피 주소
  - `.addFilter(getAuthenticationFilter());` : 앞에서 만든 AuthenticationFilter 추가
- `authenticationFilter.setAuthenticationManager(authenticationManager());` 인증처리를 하기위한 매니저로 스프링 시큐리티에서 매니저를 가져와서 set 

정리하면 모든 요청을 통과시키지 않을 것이며 사용자의 아이피는 제한적이며 해당 필터를 통과한 데이터에만 권한을 부여하고 작업을 진행시킨다.

### Users MicroService - loadUserByUsername() 구현



**WebSecurity**

인증처리를 위한 configure 추가

```java
@Configuration // 다른 빈보다 등록 우선순위가 높아진다.
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    ...

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
        super.configure(auth);
    }
}

```
- protected void configure(HttpSecurity http) throws Exception {} : 권한에 관련된 부분
- protected void configure(AuthenticationManagerBuilder auth) throws Exception {}: 인증에 관련된 부분

- 인증에 있어서 AuthenticationManagerBuilder 가 가진 userDetailService 를 사용
- userDetailService 는 사용자가 전달한 내용을 가지고 로그인 처리를 해준다.
- 먼저 사용자 데이터를 가져와서 입력으로 받은 비밀번호를 암호화하여 데이터베이스의 암호화비밀번호와 비교한다.

**UserService**

UserDetailsService 상속

```java
public interface UserService extends UserDetailsService {

    UserDto create(UserDto userDto);
    UserDto getUserByUserId(String userId);
    List<UserEntity> getUserByAll();

}
```



**UserServiceImpl**

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
...
@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPwd(),
                false, false, false, false,
                new ArrayList<>());
    }
}
```
- `return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), ...);`: 사용자 이메일을 통해 검색이 완료되면 pwd 비교하고 pwd 비교가 완료되면 검색된 사용자값 반환, new ArrayList는 권한 목록
 

### Users Microservice - Routes 정보 변경
**apigateway-service**

```yml
...
      routes:
#        - id: user-service
#          uri: lb://USER-SERVICE
#          predicates:
#            - Path=/user-service/**
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/login
            - Method=POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-service/(?<segment>,*), /$\{segment}
        - id: user-service
            uri: lb://USER-SERVICE
            predicates:
              - Path=/user-service/users
              - Method=POST
            filters:
              - RemoveRequestHeader=Cookie
              - RewritePath=/user-service/(?<segment>,*), /$\{segment}
        - id: user-service
            uri: lb://USER-SERVICE
            predicates:
              - Path=/user-service/**
              - Method=GET
            filters:
              - RemoveRequestHeader=Cookie
              - RewritePath=/user-service/(?<segment>,*), /$\{segment}
```

- filters:
  - RemoveRequestHeader=Cookie: POST로 전달되어오는 값은 매번 새롭게 새로운 데이터처럼 인식하기 위해서 RequestHeader값을 초기화한다

- filters:
  - RewritePath=/user-service/(?<segment>,*), /$\{segment}: 요청으로 들어온 패턴 중에서 /user-service는 빼고 rewrite



**user-service: UserController**

```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController { ... }
```

```java
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    ...
  

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        log.debug(((User)authResult.getPrincipal()).getUsername());

    }
}
```
- UserDetailService 의 loadUserByUsername 에 의해 반환된 User 객체를 successfulAuthentication 에서 확인할 수 있다.

**과정**
attempAuthentication -> loadUserByUsername -> successfulAuthentication

- attempAuthentication: 사용자가 요청 메시지에 담은 데이터를 RequestLogin으로 변경
- loadUserByUsername: username을 통해 DB에서 엔티티 조회하여 엔티티를 통해 User 객체 생성
- successfulAuthentication: 로그인 로직 성공 후 작업 


![image](https://user-images.githubusercontent.com/83503188/193400815-d94ef3b8-1e32-46fa-911c-5de0b94450f5.png)

![image](https://user-images.githubusercontent.com/83503188/193400939-88720ba4-3cf5-47d6-a3ec-ef2a42d1f990.png)

### JWT 생성

- jsonwebtoken 라이브러리 추가

```yml
...
token:
  expiration_time: 86400000 # 만료 기간 -> 하루
  secret: secret-key # 토큰 키
```



```java
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;


    ...

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        String userName = ((User) authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.getUserDetailsByEmail(userName);

        String token = Jwts.builder()
                .setSubject(userDto.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());

    }
}
```


![image](https://user-images.githubusercontent.com/83503188/193417007-110ae0e0-9afc-4ac9-8667-72726c8488ca.png)

### JWT 처리 과정

**전통적인 인증 시스템**

![image](https://user-images.githubusercontent.com/83503188/193417054-89d3b9ab-4177-4694-b0fb-67a4ac3d548d.png)

기존의 웹앱은 클라이언트한테 보여주는 웹앱의 기술(HTML, JSP, ...) 들은 서버단에 구현 기술이었다.
그렇기 때문에 세션, 쿠키 연동에 문제가 없었지만 예를 들어 모바일 기기는 별도의 실행환경과 개발환경을 가지기 때문에 서버가 자바로 개발되었다면 자바에서 발급한 세션과 쿠키를 연동하기 힘들다.

- 문제점
  - 세션과 쿠키는 모바일 애플리케이션에서 유효하게 사용할 수 없음
  - 렌더링된 HTML 페이지가 반되지만, 모바일 애플리케이션에서는 JSON과 같은 포맷 필요

![image](https://user-images.githubusercontent.com/83503188/193417409-5a038251-538f-41df-8eff-d7e1364d9e2e.png)


**JWT 장점**
- 클라이언트 독립적인 서비스 (stateless)
- CDN(Contents Delivery Network): 중간에 캐시 서버를 두는 기술
- No Cookie-Session(No CSRF, 사이트간 요청 위조)
- 지속적인 토큰 저장

![image](https://user-images.githubusercontent.com/83503188/193417584-5a90fde3-3dfc-4897-83e0-203b25adbdef.png)

- JWT를 DB에 저장함으로써 다른 Microservice에서도 사용 가능

```yml
- id: user-service
  uri: lb://USER-SERVICE
  predicates:
    - Path=/user-service/** # health_check, welcome, ...
    - Method=GET
  filters:
    - RemoveRequestHeader=Cookie
    - RewritePath=/user-service/(?<segment>.*), /$\{segment}
    - AuthorizationHeaderFilter
```

- 로그인과 회원가입은 jwt를 검증하는 과정이 필요없기 때문에 나머지 작업에서만 jwt 인증

**apigateway-service**

jwt 라이브러리 추가

**AuthorizationHeaderFilter**

- API 호출 시 헤더에 로그인 시 받은 토큰을 전달해주는 작업 진행 -> 토큰 존재 ? 적절한 인증 ? 토큰 제대로 발급 ?, ...





```java
@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    public Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    // API 호출 시 헤더에 로그인 시 받은 토큰을 전달해주는 작업 진행
    // 토큰 존재 ? 적절한 인증 ? 토큰 제대로 발급 ?, ...
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 헤더에 존재하는 검증
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            // jwt 검증
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);

        }));
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        // JWT subject 를 추출하여 검증
        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJwt(jwt).getBody()
                    .getSubject();
        } catch (Exception e) { // 파싱 중 오류 처리
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

//        if (!subject.equals()) {
//            returnValue = false;
//        }


        return returnValue;

    }

    // Spring Cloud Gateway Service 는 기존의 Spring MVC로 구성하지 않는다.
    // HttpServletRequest, HttpServletResponse 를 사용할 수 있는 Spring MVC이 아닌 Spring Web Flux 를 사용함으로써 비동기 방식으로 데이터를 처리하게된다.
    // 비동기 방식에서 데이터를 처리하는 2가지 방법 중 하나인 Mono(단일값) -> Mono라는 단일값에 데이터를 넣어서 반환할 수 있다.
    // 단일값이 아닌 다중값 데이터에 대해서는 Flux 라는 형태로 반환

    // 에러 메시지 반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config {

    }
}

```

Header 존재 X
![image](https://user-images.githubusercontent.com/83503188/193442475-d7092392-9b50-4fe2-b0c6-0e2b8b46ddc0.png)

Header 존재 O
![image](https://user-images.githubusercontent.com/83503188/193443491-931509d7-092b-4710-9169-caa8e39b8aa3.png)


Spring Cloud Gateway Service 는 기존의 Spring MVC로 구성하지 않는다.

따라서 HttpServletRequest, HttpServletResponse 를 사용할 수 있는 Spring MVC이 아닌 Spring Web Flux 를 사용함으로써 비동기 방식으로 데이터를 처리하게된다.

비동기 방식에서 데이터를 처리하는 2가지 방법 중 하나인 Mono(단일값) -> Mono 단일값에 데이터를 넣어서 반환할 수 있다.

단일값이 아닌 데이터에 대해서는 Flux라는 형태로 반환
