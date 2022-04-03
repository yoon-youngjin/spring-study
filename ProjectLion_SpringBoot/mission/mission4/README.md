# Misson 4

# 로그인, 사용자 데이터 다루기

Chapter 8 기준으로 로그인을 하는 방법을 배워봤습니다.

Mission 3의 산출물을 기준으로, 로그인 기능을 추가하는 연습을 해봅시다.

Mission 3에서 진행하여, 코드가 제공된 Challenge Mission 프로젝트를 가지고 진행하는 것을 가정합니다.


## Basic Mission

---

**로그인, 회원가입 구현**

Spring Security를 활용하여, 로그인, 회원가입 기능을 구현해 봅시다. 강의에서 사용한 UserDetailsService를 활용하되, 필요한 정보를 다 포함할 수 있도록 합시다.

1. `UserEntity` 는 저희가 사용자를 저장하기 위해 정의했던 `Entity` 입니다. 잠시 리뷰를 해봅시다.
    1. `username` , `password` 는 일반적인 서비스의 아이디, 비밀번호로 활용됩니다.
    2. `residence` 는 `AreaEntity` 를 필요로 합니다.
    3. `isShopOwner` 는 회원가입 시에 추가되어야 합니다.
2. `CommunityUserDetailsService` 클래스를 정의하고, `UserDetailsService` 의 구현체로 선언합시다.
    1. `UserRepository` 를 멤버 객체로 가지고 있어, `loadByUsername` 등의 함수에서 사용할 수 있어야 합니다.
    2. 주어진 `username` 에 해당하는 사용자가 없다면, `UsernameNotFoundException` 을 throw 할 수 있도록 작성합시다.
3. `UserRepository` 를 통해 받아온 `UserEntity` 를 `UserDetails` 의 형태로 반환할 수 있어야 합니다.
    1. `UserDetails` 는 인터페이스로서, Spring Security에서 요구하는 정보를 제공할 수 있는 getter 함수들을 구현하도록 명시되어 있습니다.
    2. 강의에서 사용한 미리 구현된 `User` 객체를 사용하거나,
    3. 직접 `UserDetails` 를 구현하여, 필요한 내용을 전달하면 됩니다.
4. `UserController` 라고 `@Controller` Bean을 만들고, 강의와 유사하게 로그인, 회원가입 등의 기능을 추가합시다.
    1. 강의에서 사용한 `signup-form.html` 을 적당히 수정하면, shop owner를 form에 추가할 수 있습니다. `type="checkbox"` 는 `Boolean` 형으로 Controller 에서 받을 수 있습니다.

        ```html
        <form th:action="@{/user/signup}" method="post">
            <input type="text" name="username" placeholder="아이디"><br>
            <input type="password" name="password" placeholder="비밀번호"><br>
            <input type="password" name="password_check" placeholder="비밀번호 확인"><br>
            is shop owner&nbsp;<input type="checkbox" name="is_shop_owner"><br>
            <button type="submit">회원가입</button>
        </form>
        ```

    2. `AreaEntity` 는 편의상 랜덤으로 지정해 줍시다.

### 세부 사항

1. `AreaEntity` 의 경우, 더미 데이터를 우선 활용합니다.
    1. 서울시 서초구 서초동, 37.4877° N, 127.0174° E
    2. 서울시 강남구 역삼동, 37.4999° N, 127.0374° E
    3. 서울시 강남구 삼성동, 37.5140° N, 127.0565° E
2. `UserDetailsService` 를 구현할때, `UserEntity` 의 모든 정보가 `UserDetails` 에 포함될 필요는 없습니다. 기본적으로 `UserDetails` 는 인터페이스이며, 정의된 함수들이 다 구현되어 있는 어떤 클래스든 상관없이 사용할 수 있습니다.

## Challenge Mission

---

Spring Security의 기능은 대부분 Java Servlet Filter를 구성함으로서 만들어집니다. 새로운 Filter를 구현하여, SSO의 초석을 닦아봅시다.

1. 기본적인 로그인 기능이 구현된 서버(SSO)를 구성합시다.
    1. 강의에서 사용된 `login-form.html` , `signup-form.html` 등을 활용하여도 무방합니다.
    2. SSO를 활용하고자 하는 서버로서, Mission 3의 프로젝트를 활용합니다.
2. Community Project에 새로운 Filter를 정의합니다.
    1. Filter에서 Cookie 정보를 확인하여, `likelion_login_cookie` 가 존재하는지를 확인합니다.
    2. 있다면 해당 내용을 로그로 출력하고, 없을경우 없다고 출력합니다.
3. `Community Project` 의 임시 홈페이지를 만들고, 로그인 버튼을 추가하여 클릭시 SSO 서버로 Redirect가 진행되도록 만듭니다.
    1. 구체적인 경로는 `/request-login` 으로 하고, `Query Parameter` 로 `request_from` 에 마지막 요청 위치가 포함되도록 합니다.
    2. 로그인 성공 이후, Cookie에 `likelion_login_cookie` 를 임의의 값으로 추가합시다.
    3. 로그인 성공 후  `/request-login` 로, 전달받은 데이터를 잃어버리지 않고 돌아가도록 합니다.
        - 참고 (b, c)

            ```java
            		private final CustomSuccessHandler customSuccessHandler;
            
                public WebSecurityConfig(
                        @Autowired CustomUserDetailsService customUserDetailsService,
                        @Autowired NaverOAuth2Service oAuth2UserService,
                        @Autowired CustomSuccessHandler customSuccessHandler
                ){
                    this.userDetailsService = customUserDetailsService;
                    this.oAuth2UserService = oAuth2UserService;
                    this.customSuccessHandler = customSuccessHandler;
                }
            ...
            				.formLogin()
                    .loginPage("/user/login")
                    .defaultSuccessUrl("/home")
                    .successHandler(customSuccessHandler)
                    .permitAll()
            ...
            ```

            ```java
            @Component
            public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
            
                @Override
                public void onAuthenticationSuccess(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Authentication authentication
                ) throws IOException, ServletException {
                    response.addCookie(new Cookie("likelion_login_cookie", "test_value"));
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }
            
            ```

    4. 로그인 진행후 `/request-login` 로 돌아와, 본래 요청을 보냈던 `Community Project` 로 Redirect 하도록 구성해 봅시다. 이때, Cookie에 추가한 `likelion_login_cookie` 역시 `Query Parameter` 에 추가합니다.
4. 앞서 `Communtiy Project` 에서 구성하였던 Filter에서, `(HttpServletRequest) request` 의 `getQueryString()` 에서 `likelion_login_cookie` 를 찾아내, Cookie에 저장하도록 합시다.
5. Filter 내부에서 `SecurityContextHolder.getContext()` 가 정상적으로 작동하는지 확인합니다.
    1. `Community Project` 에 `spring-boot-starter-security` 를 추가해 두도록 합니다.
    2. `SecurityContextHolder.getContext().setAuthentication()` 함수가 잘 호출되는지 확인합니다.
    3. 호출한 함수에 `new Authentication() { ... }` 을 인자로 전달하고, 내부 함수를 임시로 구현하여 어플리케이션이 사용자가 로그인 한것으로 판단하는지를 확인해 봅니다.
        - 참고

            ```java
            SecurityContextHolder.getContext().setAuthentication(new Authentication() {
                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.emptyList();
                    }
            
                    @Override
                    public Object getCredentials() {
                        return null;
                    }
            
                    @Override
                    public Object getDetails() {
                        return null;
                    }
            
                    @Override
                    public Object getPrincipal() {
                        return (Principal) () -> "dummy";
                    }
            
                    @Override
                    public boolean isAuthenticated() {
                        return true;
                    }
            
                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            
                    }
            
                    @Override
                    public String getName() {
                        return "dummy";
                    }
                });
                chain.doFilter(request, response);
            }
            ```

            ```java
            http
                    .authorizeRequests()
                    .antMatchers(
                            "/home/**",
                            "/user/signup/**",
                            "/",
                            "/css/**",
                            "/images/**",
                            "/js/**"
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            ```


### 세부 사항

1. 설명은 복잡해 보이지만, 강의에서 언급한 SSO 구현 방법론을 말로서 풀어 작성한 것입니다. 기본적으로 외부의 다른 서버에 저장되어 있는 사용자의 정보 표현을 일부 가져오는 것을 목표로 합니다.
2. 현재 요구 사항까지 진행할 경우, 아직 SSO 로그인이 진행되지는 않습니다. 로그인 성공 이후 받아오게 되는 `likelion_login_cookie` 를 가지고 실제 로그인한 사용자의 정보를 확인하는 과정이 필요합니다.
3. `AuthenticationSuccessHandler` 는 로그인이 성공한 뒤에만 실행되는, Filter와 유사한 동작을 하는 인터페이스 입니다.
4. `Query Parameter` 는 URL의 구조에 대하여, URL 뒤에 조회 등의 목적으로 추가적인 데이터를 첨부할 때 사용하는 인자입니다. `@GetMapping` 의 `@RequestParam` 으로 확인할 수 있습니다.