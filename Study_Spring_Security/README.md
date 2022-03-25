## Spring Security 
- 보안 솔루션을 제공해주는 Spring 기반의 스프링 하위 프레임워크

### 인증과 권한 
- 인증(Authentication): 자신이 ‘누구’라고 주장하는 사람을 정말 ‘누구’가 맞는지 확인하는 작업
- 권한(Authorization): 특정 부분에 접근할 수 있는지에 대한 여부를 확인하는 작업

### 프로젝트 설명
1. 로그인

      A. 로그인을 할 수 있는 페이지

      B. 누구나 접근이 가능

      C. 로그아웃을 하게 되면 보이는 페이지
2. 회원가입 페이지

      A. 회원가입을 할 수 있는 페이지

      B. 누구나 접근 가능
3. 유저 전용 페이지

      A. 로그인 성공하면 이동하는 페이지

      B. 유저, 관리자만 접근 가능

      C. 로그아웃 기능
4. 관리자 전용 페이지

      A. 관리자만 접근 가능

      B. 로그아웃 기능


### Config 파일 작성

- Spring Security에서 관련된 설정을 하기 위해 필요한 config 파일
- WebSecurityConfigureAdapter를 상속

```
@RequiredArgsConstructor
@EnableWebSecurity // 1
@Configuration 
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { // 2

  private final UserService userService; // 3

  @Override
  public void configure(WebSecurity web) { // 4
    web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception { // 5
    http
          .authorizeRequests() // 6
            .antMatchers("/login", "/signup", "/user").permitAll() // 누구나 접근 허용
            .antMatchers("/").hasRole("USER") // USER, ADMIN만 접근 가능
            .antMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 가능
            .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
        .and() 
          .formLogin() // 7
            .loginPage("/login") // 로그인 페이지 링크
            .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 주소
        .and()
          .logout() // 8
            .logoutSuccessUrl("/login") // 로그아웃 성공시 리다이렉트 주소
	    .invalidateHttpSession(true) // 세션 날리기
    ;
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception { // 9
    auth.userDetailsService(userService)
    	// 해당 서비스(userService)에서는 UserDetailsService를 implements해서 
        // loadUserByUsername() 구현해야함 (서비스 참고)
    	.passwordEncoder(new BCryptPasswordEncoder()); 
   }
}
```

1. Spring Security를 활성화
2. WebSecurityConfigureAdapter는 Spring Security의 설정파일로서의 역할을 하기 위한 상속 클래스
3. 유저 정보 가져오기 위함
4. 인증을 무시할 경로들을 설정 -> css, js, imgs는 무조건 접근이 가능해야 함
5. http 관련 인증
6. 접근에 대한 인증 설정이 가능

      A. anyMatchers를 통해 경로 설정과 권한 설정이 가능

      B. permitAll(): 누구나 접근 가능

      C. hasRole(): 특정 권한이 있는 사람만 접근 가능

      D. authenticated(): 권한이 있으면 무조건 접근 가능

      E. anyRequest: anyMatchers에서 설정하지 않은 나머지 경로
7. 로그인에 관한 설정

      A. loginPage(): 로그인 페이지 링크 설정

      B. defaultSuccessUrl(): 로그인 성공 후 리다이렉트 할 주소
8. 로그아웃에 관한 설정

      A. logoutSuccessUrl(): 로그아웃 성공 후 리다이렉트 할 주소

      B. invalidateHttpSession(): 로그아웃 이후 세션 전체 삭제 여부
9. 로그인할 떄 필요한 정보를 가져오는 곳

       A. 유저 정보를 가져오는 서비스를 userService으로 지정

        B. 패스워드 인코더는 아까 빈으로 등록해놓은 passwordEncoder()를 사용
