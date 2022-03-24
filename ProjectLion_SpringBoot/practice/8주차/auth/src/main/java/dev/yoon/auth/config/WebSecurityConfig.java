package dev.yoon.auth.config;

import dev.yoon.auth.infra.CustomUserDetailsService;
import dev.yoon.auth.infra.NaverOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
// @EnableWebSecurity: SpringSecurity의 설정을 조작할 준비가 되었음을 Spring ioc에 알려주는것
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userService;
    private final NaverOAuth2Service naverOAuth2Service;

    /**
     * AuthenticationManagerBuilder: 기본값은 default security password를 사용
     * 사용자 관리, 사용자 username과 password가 일치하는지에 대한 확인 과정
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * inMemoryAuthentication(): 메모리 상에서 유저 검증을 함
         * 기본적인 spring boot에 내장되어 구현된 객체 중 하나를 유저관리를 위해 사용
         * withUser를 통해 user에 대한 정보를 포함시킴
         * user를 만들면 마지막으로 User(SpringBoot 내장 객체 User.java) -> build()를 호출
         * build에서 passwordEncoder 사용
         * SpringSecurity에서 기타 DB를 사용하여 사용자 정보를 저장하고 싶은 경우 UserDetailsService를 구현한 객체를 사용하여 auth.userDetailsService()를 사용
         *
         */

//        auth.inMemoryAuthentication()
//                .withUser("user1")
//                .password(passwordEncoder().encode("user1pass"))
//                .roles("USER")
//                .and()
//                .withUser("admin1")
//                .password(passwordEncoder().encode("admin1pass"))
//                .roles("ADMIN");

        auth.userDetailsService(userService);
    }

    /**
     * HttpSecurity: filter와 유사, HttpSecurity객체가 앱 전반적인 관장하는 설정을 가짐
     * http설정에 원하는 값들을 함수로써 추가하면서 보안설정을 조작
      */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                /**
                 * Url기반의 권한 확인 기법-> Url이 주어졌을 때 언제는 허용, 언제는 허용x 설정하는 함수
                 */
                .authorizeRequests()
                /**
                 * 어떤 요청에 대해서도 허용
                 */
//                .anyRequest()
//                .permitAll();
                /**
                 * 로그인한 모든 요청만 허용
                 * 모든 요청에 대해서 로그인이 필요
                 * 로그인 안한 상태로 접근 시 403 처리
                 * 401: 사용자가 누구인지 증명 못한 경우
                 * 403: 허가받지 않은 사용자
                 */
//                .anyRequest()
//                .authenticated();
                /**
                 * 로그인 안한 사용자 모두 허용
                 */
//                .anyRequest()
//                .anonymous()
                /**
                 * home 및 home 하위 계층을 의미
                 * 해당 url은 로그인 안한 사용자도 접근 가능
                 *
                 */
                // board/1/post/2 -> /board/*/post/**
                // *: 하나의 path, **: 나머지 path 전부
                .antMatchers("/home/**", "/user/**") // 다중 처리 가능
                .anonymous()
                /**
                 * 위의 url 이외의 나머지 path
                 * 모두 로그인 필수
                 */
                .anyRequest()
                .authenticated()
                /**
                 * anyRequest 이후에 추가적인 설정은 오류가남
                 * 꼭 마지막에 처리
                 */
//                .antMatchers("/**")
//                .permitAll();
                /**
                 * 위의 authorizeRequests가 끝나고 다시 httpSecurity를 받아옴
                 * formLogin() 설정 만으로 "/" -> redirection "/login"
                 * loginPage를 통해 (default = "/login") 로그인 페이지 설정
                 * 로그인 성공 시 redirect할 url
                 * 위의 authorizeRequests보다 높은 우선순위에서 작동하여 /user/login페이지 접근 가능
                 */
            .and()
                .formLogin()
                .loginPage("/user/login")
                .defaultSuccessUrl("/home")
                .permitAll()
                /**
                 * oauth2를 이용한 로그인 처리
                 * 소셜 로그인
                 * 중간의 and: oauth2도 하나의 빌더라고 생각 -> oauth2를 돌려받기 위한 and임
                 */
            .and()
                .oauth2Login()
                    .userInfoEndpoint()
                    .userService(this.naverOAuth2Service)
                .and()
                    .defaultSuccessUrl("/home")
            .and()
                /**
                 * 로그아웃 처리
                 * invalidateHttpSession: http섹션 객체를 사용할 때 저장했던 내용들을 삭제
                 */
                .logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/home")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll();

    }


}
