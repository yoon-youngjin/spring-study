# 1주차 과제 피드백 

1.  환경에 따라서 ddl-auto값을 다르게 하자
   - [x] create: 기존테이블 삭제 후 다시 생성 (DROP + CREATE)
   - [x] create-drop: create와 같으나 종료시점에 테이블 DROP
   - [x] update: 변경분만 반영(운영DB에서는 사용하면 안됨)
   - [x] validate: 엔티티와 테이블이 정상 매핑되었는지만 확인
   - [x] none: 사용하지 않음(사실상 없는 값이지만 관례상 none이라고 한다.)
   - Spring Profile
     - Spring Profile은 런타임 시 지정한 profile 값에 따라서 각 설정 파일을 로드 하도록 할 수 있다.
     - 스프링 부트에서는 기본적으로 실행 시 resource 폴더에서 application.properties(yml)를 찾아서 로드한다.
     - 다음으로 profile 값이 있다면 application-${profile}.properties(yml) 파일을 찾아 로드한다.
     - 설정 파일 내부의 설정 값들은 가장 마지막에 로드한 값으로 오버라이드 된다.
     - profile 값을 넘겨주지 않으면 기본 값은 default 이다.
     - 설정파일은 디폴트파일(application.yml)에는 중복되는 값을 넣고 다른 부분만 나눠진 설정파일에 옮기는 것이 좋다

   - [x] application.yml: default로 사용할 설정 파일
   - [x] application-dev.yml: 개발환경에서 사용할 설정 파일
   - [x] application-prod.yml: 운영환경에서 사용할 설정 파일
   - [x] application-test.yml: 테스트환경에서 사용할 설정 파일

   - db 비밀번호와 같이 private한 값들

<p align="center">
  <img src="https://user-images.githubusercontent.com/83503188/165684218-da95a80d-66ed-4b9a-90cd-6833bd71982d.png" width="400px" height="250px"/>
  <img src="https://user-images.githubusercontent.com/83503188/165679221-85266528-96e0-433a-8033-e494dd3d9750.png" width="400px" height="250px"/>
</p>

> 운영 장비에서는 절대 crate, create-drop, update 사용하면 안된다.
> 
> 개발 초기 단계는 create 또는 update
> 
> 테스트 서버는 update 또는 validate
> 
> 스테이징과 운영 서버는 validate 또는 none
> 
> 하지만 로컬 환경을 제외한 나머지 서버에서는 최대한 직접 쿼리를 날려서 적용하는 것이 가장 좋다.

2. 패키지 구조를 확실하게 하자

- domain 패키지: 각 도메인 별로 핵심 로직을 모아둔 패키지
- global 패키지: 프로젝트 전반에 영향을 주는 util, config, error를 모아둔 패키지
- infra 패키지: sms, 이메일 전송, 파일 컴퓨터 저장 처리를 하는 패키지
- web 패키지: 웹 개발을 위한 패키지

![image](https://user-images.githubusercontent.com/83503188/165678912-f34961f2-7086-488c-b1dd-d63fc90e31aa.png)

3. dto에 종속성을 가지는 것을 피하자

- 회원가입과 같은 핵심 로직들은 특정 dto에 종속되는 것을 지양하자
- 예를들어, 또 다른 형태의 회원가입 페이지를 만들 경우 MemberService에 불필요한 서비스 메소드(`register2`)를 추가하여 대응해야 하는 비효율성이 발생할 수 있다.

---

# 1주차 공부

## MemberService

- `@Transactional` 어노테이션을 통해 데이터를 넣다가 오류나는 경우 rollback 자동화
- `@Transactional`의 readonly값을 true로 해주어서 jpa의 변경감지 기능을 끈다
- 빈 주입으로 생성자 주입을 사용하면 테스트 코트 작성 시 스프링을 사용하지 않아도 직접 객체를 만들어서 넣어줄 수 있으므로 스프링에서 생성자 주입을 가장 권장함

## SpringSecurity

### SecurityConfig -> WebSecurityConfigureAdater를 상속

- WebSecurityConfigureAdapter는 SecurityConfig에서 웹 보안을 초기화해주는 클래스
- @EnableWebSecurity 필터를 활성화하기 위한 어노테이션
- @Configuration 설정파일임을 알리는 어노테이션
- HttpSecurity를 파라미터로 가진 configure 함수를 오버라이딩

  - http.formlogin()을 통해 로그인 페이지 지정
  - .loginPage: 로그인 페이지 경로
  - .loginProcessingUrl: 로그인 처리 경로
  - .defaultSuccessfulUrl: 로그인 성공 후 이동할 경로
  - .userParameter: 유저 파라미터 이름
  - .passwordParameter: 비밀번호 파라미터 이름
  <p align="center">
    <img src="https://user-images.githubusercontent.com/83503188/165680534-134392c1-db6e-442c-8630-36266938218a.png" width="400px" height="250px"/>
  </p>
     - userParameter, passwordParameter의 인자 값 
  - .failureUrl: 로그인 실패 시 이동할 경로  
  
  - http.authorizeRequests()를 통해 각 페이지마다 권한 설정
  - .antMatchers("/", "/login", "/register").permitAll(): 인증되지 않은 사용자는 3가지 경로에 접근 가능
  - .antMatchers("/admin/**").hasRole("ADMIN"): ADMIN권한을 가진 유저만 admin이 붙은 경로에 접근 가능
  - .anyRequest().authenticated(): 위의 페이지를 제외한 나머지 모든 페이지는 인증을 받아야지 접근 가능
  
  - http.exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPointImpl()): 유저가 자원을 요청했는데 인증을 실패한 경우 처리방법

```java
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /**
     * 뒤 챕터에서 ajax로 비동기 처리를 하는 부분이 있음
     * 그런 경우 header에 x-requested-with라는 값이 들어감
     * 그리고 해당 값안에 XMLHttpRequest라는 값이 들어가 있음
     * 그런 경우 로그인 페이지로 리다이렉트하지 않고 401에러를 반환
     * ajax요청을 받을 쪽에서 401에러가 오면 로그인 페이지로 이동하거나 하는 식의 로직을 구현하면 됨
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }else {
            response.sendRedirect("/login");
        }
    }
}

```


## Audit 기능

- 엔티티의 생성이나 수정을 감시
```java
public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String modifiedBy = "";
        if(authentication instanceof AnonymousAuthenticationToken) {
            modifiedBy = httpServletRequest.getRequestURI();
        } else {
            modifiedBy = authentication.getName();
        }
        return Optional.of(modifiedBy);
    }

}
```
- Authentication: SecurityContextHolder에서 인증 정보를 가져옴 -> 로그인을 하지 않았을 때는 해당 인터페이스의 구현체가 AnonymousAuthenticationToken임 -> 인증이 안된 경우이므로 사용자의 아이디를 넣을 수 없으므로 요청이 온 주소를 꺼내서 넣어줌(추적에 간편) -> 로그인을 한 경우라면 유저의 이메일을 꺼내와서 수정자와 등록자로 지정

```java
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

}
```
- Audit 기능을 사용하기 위해서 Bean으로 등록해야함 -> AudifConfig클래스를 만들고 @EnableJpaAuditing 어노테이션을 붙여줌으로써 Audit 시작
- 앞에서 구현한 구현체를 빈으로 등록 

- BaseEntity, BaseTimeEntity에 @EntityListeners(AuditingEntityListener.class) 어노테이션을 통해 리스너를 부착

- 수정자에는  @CreatedBy, 생성자 @LastModifiedBy
- 생성일에는 @CreatedDate 수정일에는 @LastModifiedDate

## ThymeLeaf

- fragment: 페이지마다 공통으로 사용하는 요소를 공통화하는 방법 
- layout: shoplayout html파일을 만들어서 공통으로 들어가는 프래그먼트와 공통 파일을 관리
- 레이아웃을 이용하여 공통요소, 공통css 자바스크립트 파일을 관리
- html에 타임리프 설정: <html xmlns:th="http://www.thymeleaf.org">
- th:fragment: 해당 파일을 조각처럼 만들어서 다른페이지에서 가져다가 사용할 수 있음

```html
 <html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
```
- 네임스페이스에 타임리프를 사용하기 위해 타임리프 org를 넣고 layout기능을 사용하기 위해 thymeleaf/layout 네임스페이스를 추가

- script도 shoplayout 파일 body의 맨아래쪽에 넣어줌 
  - script는 바디 맨아래에 위치시키는 것을 권장 -> script를 위에 위치 시키면 페이지를 그리는 부분에서 지연이 발생할 수 있고 스크립트 영역쪽에서 dom이 모두 그려지기 전에 해당 dom을 사용하면 오류가 발생

```html
<div th:replace="fragments/header::header"></div>

<div class="content">
     <th:block layout:fragment="content">

     </th:block>
</div>

```
- th:replace를 이용하여 fragment패키지에서 만들어둔 헤더영역을 가져옴 
  - fragments 패키지의 header.html의 header fragment를 가져옴
- content영역에만 각페이지 마다 들어가는 html을 만들어주면 된다 
  - 메인페이지 같은 경우 shoplayout의 content영역에 들어갈 내용만 작성해주면 된다.
- layout:fragment를 통해 shoplayout의 content에 적용

```html
<html xmlns:sec="http://www.w3.org/1999/xhtml"
      xmlns:layout=http://www.ultraq.net.nz/thymeleaf/layout
      layout:decorate="~{layout/shoplayout}">
```

- layout:decorate를 이용하여 layout 폴더 아래의 shoplayoyt html파일을 적용

```html
<link th:href="@{/css/shoplayout.css}" rel="stylesheet">
```
- 링크도 th:href를 통해 애플리케이션 루트도 같이 만들어줌
- `/shop` 경로로 들어가면 `/shop/css/shoplayout.css` 로 자동 변경

- 기존 path:/인 경우

<p align="center">
  <img src="https://user-images.githubusercontent.com/83503188/165682047-53492103-e409-4df4-a29c-ba38d3263708.png" width="400px" height="250px"/>
</p>

- 루트 패스 변경

<p align="center">
  <img src="https://user-images.githubusercontent.com/83503188/165682044-eb07e4f2-09bd-4c3a-98cb-38dced309f19.png" width="400px" height="250px"/>
  <img src="https://user-images.githubusercontent.com/83503188/165682050-93835a3c-8510-4bbf-9a61-0778472871d0.png" width="400px" height="250px"/>
</p>
- 루트 컨텍스트가 변경되더라도 따로 대응할 필요가 없어진다

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```
- Spring security를 사용하면 기본적으로 post요청에 csrf토큰을 보내줘야함 
  - 해당 페이지에서 발생한 요청인지 검증하기 위해

- implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5'
  - 스프링 시큐리티의 인증 여부에 따라서 메뉴 노출 여부를 결정할 수 있음
  - 해당 html파일의 네임스페이스에 `xmlns:sec="http://www.thymeleaf.org/extras/spring-security"` 추가
  - sec:authorize를 통해 인증 여부를 파악

## 입력 값 검증

- Bean Validation, Spring, thymeleaf 통합이 좋음
  - @NotBlank: String을 검증할 때 사용, NotEmpty, NotNull과 비교했을 때 null체크는 물론이고, string에 공백이 포함된 경우도 체크해줌
  - BindingResult: 오류에 대한 정보를 담아줌

```java
if (bindingResult.hasErrors()) {
            return "login/registerform";
        }
```

- 입력 값에 오류가 있는 경우 회원가입 폼으로 재 전송

![image](https://user-images.githubusercontent.com/83503188/165682502-dc76e483-d6e5-4f16-b900-eb9da1c20abc.png)

- 적용된 object의 field중 name에 오류가 있는 경우 field-error클래스를 적용(th:errorclass)
- name 필드에 오류가 있으면 오류 문구 출력 (th:errors)


```java
bindingResult.reject("mismatchedPassword", ErrorCode.MISMATCHED_PASSWORD.getMessage());
return "login/registerform";
```

- valid에 대한 오류가 아닌 패스워드 불일치와 같은 오류 처리
- reject 함수를 통해 글로벌 오류를 넣음


## LoginService
- domain 패키지는 특정 뷰에 종속된 dto의 존재를 모르게 디자인
- 회원을 등록하는 로직이 여러 화면에서 부르는 경우 특정 dto에 종속을 하면 memberservice에서 memberregisterdto를 받게 되면 나중에 다른 뷰에서 프론트에서 받는 dto가 변경될 수 있으므로 dto에 종속되는 것을 막아야함
- Member saveMember = Member.createMember(member); 데이터베이스 저장하는 객체는 Entity클래스 내에 정적 팩토리 메소드를 만들어서 한군데에서 관리

## UserDetails

```java
public UserDetailsImpl(Member member) {
        this.member = member;
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
    }

```
- UserDetails를 구현체인 UserDetailsImpl를 생성하여 Member와 권한리스트를 필드로 가짐
- SimpleGrantedAuthority: GrantedAuthority를 구현한 구현체 
```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
}
```

- UserDetailsService를 구현한 memberservice를 security에 알려줘야함
- passwordEncoder를 등록함으로써 사용자의 입력으로 들어온 비밀번호와 db에 암호화된 비밀번호를 비교해서 맞는지 검증

```java
@Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }


```

- static폴더 내의 파일도 인증 대상이 되므로 인증 대상에서 제외

--- 

## 서버 기반 인증 vs. 토큰 기반 인증

- 특정 사용자가 서버에 접근 했을 때, 해당 사용자가 인증된 사용자인지 구분하기 위해서 여러 방법을 사용
1. 서버 기반 인증
2. 토큰 기반 인증

위 2가지 방법들은 각각의 장, 단점이 존재하기 때문에 상황에 맞게 적절한 방법을 선택해야 한다. 그 중 **JWT**는 '토큰 기반 인증'에 해당하는 방법

토큰을 사용한다는 것은 요청과 응답에 토큰을 함께 보내 이 사용자가 유효한 사용자인지를 검색하는 방법, 이때, 보통 **Json Web Token(JWT)**룰 사용해서 토큰을 전달

![image](https://user-images.githubusercontent.com/83503188/166202088-b670e5ca-a219-476f-a390-7809de4a0eb0.png)

- 클라이언트가 아이디와 비밀번호를 서버에게 전달하며 인증을 요청
- 서버는 아이디와 비밀번호를 통해 유효한 사용자인지 검증하고, 유효한 사용자인 경우 **토큰을 생성해서 응답**
- 클라이언트는 토큰을 저장해두었다가, **인증이 필요한 api에 요청할 때 토큰 정보와 함께 요청**
- 서버는 토큰이 유효한지 검증하고, 유효한 경우에 응답을 해준다.

### 토큰 사용 방식의 특징

1. 무상태성(stateless)
- 사용자의 인증 정보가 담겨있는 토큰을 클라이언트에 저장하기 때문에 서버에서 별도의 저장소가 필요 없어, 완전히 **무상태(stateless)**를 가질 수 있다.

2. 확장성
- 토큰 기반 인증을 사용하는 다른 시스템에 접근이 가능(ex. Facebook 로그인, Google 로그인)

3. 무결성
- HMAC(Hash-base Message Authentication) 기법이라고도 불리며, 발급 후의 토큰의 정보를 변경하는 행위가 불가능하다. 즉, 토큰이 변조되면 바로 알아차릴 수 있다.

4. 보안성
- 클라이언트가 서버에 요청을 보낼 때, 쿠키를 전달하지 않기 때문에 쿠키의 취약점은 사라진다.

### JWT?

JWT는 **토큰 기반 인증 시스템의 대표적인 구현체**이다. Java를 포함한 많은 프로그래밍 언어에서 이를 지원하며, 보통 회원 인증을 할 때에 사용

![image](https://user-images.githubusercontent.com/83503188/166202904-104c8d11-2836-4e23-bad6-d9f2cbeccbce.png)

JWT는 `.`을 기준으로 헤더(header) - 내용(payload) - 서명(signature)으로 이루어진다.


1. 헤더(header)
헤더는 토큰의 타입과 해싱 알고리즘을 지정하는 정보를 포함

- typ: 토큰의 타입을 지정, `JWT`라는 문자열이 들어가게 된다.
- alg: 해상 알고리즘을 지정

![image](https://user-images.githubusercontent.com/83503188/166203390-5ef870cf-67be-481c-b6bd-6e464c10cf0e.png)

위 예제를 해석하면, JWT 토큰으로 이루어져있고, 해당 토큰은 HS256으로 해상 알고리즘으로 사용되었다는 것을 알 수 있다.

2. 정보(payload)

토큰에 담을 정보가 들어간다. 정보의 한 덩어리를 클레임(claim)이라고 부르며, 클레임은 key-value의 한 상으로 이루어져 있다. 클레임의 종류는 세 종류

- **등록된(registered) 클레임**
  - 토큰에 대한 정보를 담기 위한 클레임들이며, **이미 이름이 등록되어있는 클레임**
  - `iss` : 토큰 발급자(issuer)
  - `sub` : 토큰 제목(subject)
  - `aud` : 토큰 대상자(audience)
  - `exp` : 토큰의 만료시간(expiraton). 시간은 NumericDate 형식으로 되어있어야 하며,(예: 1480849147370) 항상 현재 시간보다 이후로 설정되어있어야한다.
  - `nbf` : Not Before 를 의미하며, 토큰의 활성 날짜와 비슷한 개념. NumericDate 형식으로 날짜를 지정하며, 이 날짜가 지나기 전까지는 토큰이 처리되지 않는다.
  - `iat` : 토큰이 발급된 시간 (issued at)
  - `jti` : JWT의 고유 식별자로서, 주로 일회용 토큰에 사용한다.
- **공개(public) 클레임**
  - 말 그대로 공개된 클레임, 충돌을 방지할 수 있는 이름을 가져야하며, 보통 클레임 이름을 URI로 짓는다.
- **비공개(private) 클레임**
  - 클라이언트 - 서버간에 통신을 위해 사용되는 클레임

![image](https://user-images.githubusercontent.com/83503188/166204065-9aa53c4f-9c8b-4fe7-8da4-072610b05be1.png)

3. 서명(signature)
해당 토큰이 조작되었거나 변경되지 않았음을 확인하는 용도로 사용하며, 헤더(header)의 인코딩 값과 정보(payload)의 인코딩 값을 합친 후에 주어진 비밀키를 통해 해쉬값을 생성

![image](https://user-images.githubusercontent.com/83503188/166204207-a3e2ec0d-d4fe-48f4-bbb4-1cad0875e645.png)

Spring 환경에서 JWT를 다루기 위해 사용하는 라이브러리 `jsonwebtoken`

#### build.gradle

```java
dependencies {
    .
    .
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.1'
    .
    .
    .
}

```




