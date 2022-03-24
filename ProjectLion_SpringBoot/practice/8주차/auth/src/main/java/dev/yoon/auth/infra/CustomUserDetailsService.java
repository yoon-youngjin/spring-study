package dev.yoon.auth.infra;

import dev.yoon.auth.domain.User;
import dev.yoon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 비밀번호가 필요 없다?
     * 스프링 부트를 사용하면 사용자가 자신의 정보를 제공했을 때(= 로그인)
     * 제공받은 username을 통해 UserDetails를 반환
     * UserDetails내에는 사용자의 비밀번호, 닉네임같은 정보가 들어있음
     * UserDetails: 스프링 부트 시큐리티 내에서 사용자 정보를 관리하기 위한 기초적인 객체 인터페이스
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final User user = this.userRepository.findByUsername(username);
        return user;
    }
}
