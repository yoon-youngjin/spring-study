# Chapter 4: Spring Security

애플리케이션을 만들기 위해서는 보통 인증/인가 등의 보안이 필요하다. 스프링 시큐리티는 스프링 기반의 애플리케이션을 위한 보안 솔루션을 제공

애플리케이션의 보안에서 중요한 두 가지 영역은 `인증`과 `인가`

- 웹이서 인증이란 해당 리소스에 대해서 작업을 수행할 수 있는 주체인지 확인하는 것
  - 예를 들어 어떤 커뮤니티에서 게시판의 글을 보는 것은 로그인을 하지 않아도 되지만, 댓글을 작성하려면 로그인을 해야한다. 댓글을 달기 위해서는 로그인이라는 인증 절차가 필요
- 인가는 인증 과정 이후에 일어난다.
  - 커뮤니티를 관리하는 관리자 페이지에 접근하는 URL을 입력했을 때 해당 URL은 커뮤니티의 관리자만 접근할 수 있어야 한다. 
  - 이때 접근하는 사용자가 해당 URL에 대해서 인가된 회원인지를 검사하는 것, 인가된 유저라면 해당 URL에 대한 권한이 있기 때문에 접근이 가능

> 인증(Authentication)과 인가(Authorization) 
> 
> 인증: 유저가 누구인지 확인하는 절차, 회원과입과 로그인하는 것
> 
> 인가: 유저에 대한 권한을 허락하는 것 

## 스프링 시큐리티 설정 추가하기

```java
dependencies {
        ...
    implementation 'org.springframework.boot:spring-boot-starter-security'
    testImplementation 'org.springframework.security:spring-security-test'
        ...
}
```

- 시큐리티 설정을 추가함으로써 모든 요청에 인증을 요구한다.
- 인증이 필요 없는 경우: 상품 상세 페이지 조회
- 인증이 필요한 경우: 상품 주문
- 관리자 권한이 필요한 경우: 상품 등록

```java
@Configuration
@EnableWebSecurity // 1)
public class SecurityConfig extends WebSecurityConfigurerAdapter { 


    @Override
    protected void configure(HttpSecurity http) throws Exception { // 2)
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() { // 3)
        return new BCryptPasswordEncoder();
    }
}
```

1. WebSecurityConfigureAdapter를 상속받는 클래스에 `@EnableWebSecurity` 어노테이션을 선언하면 SpringSecurityFilterChain이 자동으로 포함, WebSecurityConfigurerAdapter를 상속받아서 메소드 오버라이딩을 통해 보안 설정을 커스터마이징할 수 있다.
2. http 요청에 대한 보안을 설정한다. 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정을 작성
3. 비밀번호를 데이터베이스에 그대로 저장했을 경우, 데이터베이스가 해킹당하면 고객의 회원 정보가 그대로 노출된다. 이를 해결하기 위해 BCryptPasswordEncoder의 해시 함수를 이용하여 비밀번호를 암호화하여 저장한다.

---

> CSRF
> 
> CSRF(Cross Site Request Forgery)란 사이트간 위조 요청으로 자신의 의지와 상관없이 해커가 의도한 대로 수정, 등록, 삭제 등의 행위를 웹사이트 요청하게 하는 공격

- 스프링 시큐리티를 사용할 경우 기본적으로 CSRF를 방어하기 위해 모든 POST 방식의 데이터 전송에는 CSRF 토큰 값이 있어야 한다. CSRF 토큰은 실제 서버에서 허용한 요청이 맞는지 확인하기 위한 토큰, 사용자의 세션에 임의의 값을 저장하여 요청마다 그 값을 포함하여 전송하면 서버에서 세션에 저장된 값과 요청이 온 값이 일치하는지 확인하여 CSRF를 방어한다.


