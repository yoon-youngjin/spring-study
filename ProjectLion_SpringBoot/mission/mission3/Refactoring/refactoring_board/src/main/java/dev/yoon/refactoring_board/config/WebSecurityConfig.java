package dev.yoon.refactoring_board.config;

import dev.yoon.refactoring_board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    // 정적인 파일에 대한 요청들
    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/**",
            "/image/**",
            "/swagger/**",
            "/swagger-ui/**",
            // other public endpoints of your API may be appended to this array
            "/h2/**"
    };

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception { // 5
        http
//                .cors()
//                .and() // session 전략
                .csrf().disable();
//                .authenticationManager()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
//                .and() // 페이지 권한 설정
//                .authorizeRequests() // 6
//                .antMatchers("/auth/**").permitAll()
//                .antMatchers("/login", "/signup", "/user").permitAll(); // 누구나 접근 허용
//                .antMatchers("/").hasRole("USER") // USER, ADMIN만 접근 가능
//                .antMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 가능
//                .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
//                .and()
//                .formLogin() // 7
//                .loginPage("/user") // 로그인 페이지 링크
//                .permitAll();
//                .and() // 에러 핸들링
//                .exceptionHandling().accessDeniedPage("/admin/error");
//                .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 주소
//                .and()
//                .logout() // 8
//                .logoutSuccessUrl("/login") // 로그아웃 성공시 리다이렉트 주소
//                .invalidateHttpSession(true); // 세션 날리기
    }

//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception { // 9
//
//        auth.userDetailsService(userService)
//                // 해당 서비스(userService)에서는 UserDetailsService를 implements해서
//                // loadUserByUsername() 구현해야함 (서비스 참고)
//                .passwordEncoder(new BCryptPasswordEncoder());
//    }
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
}
