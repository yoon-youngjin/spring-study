package dev.yoon.study_spring_security.service;

import dev.yoon.study_spring_security.domain.User;
import dev.yoon.study_spring_security.dto.UserDto;
import dev.yoon.study_spring_security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    /**
     * Spring Security 필수 메소드 구현
     * @param email 이메일
     * @return UserDetails
     * @throws UsernameNotFoundException 유저가 없을 때 예외 발생
     */
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public UserDto save(UserDto dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        dto.setPassword(encoder.encode(dto.getPassword()));
        User user = User.builder()
                .email(dto.getEmail())
                .auth(dto.getAuth())
                .password(dto.getPassword()).build();

        userRepository.save(user);
        return dto;

    }

    public User findByEmail(String email) {

        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.get();
    }
}
