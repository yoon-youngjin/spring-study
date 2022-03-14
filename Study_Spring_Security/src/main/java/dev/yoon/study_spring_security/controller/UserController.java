package dev.yoon.study_spring_security.controller;

import dev.yoon.study_spring_security.domain.User;
import dev.yoon.study_spring_security.dto.LoginDto;
import dev.yoon.study_spring_security.dto.UserDto;
import dev.yoon.study_spring_security.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("user")
    public UserDto Singup(@RequestBody UserDto dto, HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();
        headerNames.parallelStream().forEach(s -> {
            System.out.println(s.toString());
        });
        return userService.save(dto);
    }

    @PostMapping("login")
    public ResponseEntity<?> Login(@RequestBody LoginDto dto, HttpSession session) {

        User user = userService.findByEmail(dto.getEmail());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        user.setTokenId(RequestContextHolder.currentRequestAttributes().getSessionId());

        return ResponseEntity.ok(user);
    }

    @GetMapping("admin")
    public String admin() {
        return "redirect:/admin";
    }

    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }
}
