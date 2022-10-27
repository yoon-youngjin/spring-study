package dev.yoon.userservice.security;

import dev.yoon.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

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
        http.authorizeRequests().antMatchers("/users/", "/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress(IP)
                .and()
                .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable(); // h2-console 에 접근하기 위한 disable
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        System.out.println("getAuthenticationFilter()");

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env, authenticationManager());
        return authenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }


}
