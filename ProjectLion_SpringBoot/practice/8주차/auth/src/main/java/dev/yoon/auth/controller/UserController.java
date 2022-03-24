package dev.yoon.auth.controller;

import dev.yoon.auth.domain.User;
import dev.yoon.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("login")
    public String login() {
        return "login-form";
    }

    @GetMapping("signup")
    public String signUp() {
        return "signup-form";
    }

    /**
     * 권한은 기본값이 존재
     * 기본값이 없는 경우 선택을 하도록 권유
     * 타임리프: 타임리프 로딩 시에 채워넣을 객체 설정가능
     * 전통적인 통신 패턴: post -> redirect -> get
     */
    @PostMapping("signup")
    public String signUpPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password_check") String passwordCheck
    ) {
        if(!password.equals(passwordCheck))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return "redirect:/home";
    }
}
