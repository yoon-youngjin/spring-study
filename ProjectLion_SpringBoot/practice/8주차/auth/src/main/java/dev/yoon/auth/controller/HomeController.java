package dev.yoon.auth.controller;

import dev.yoon.auth.infra.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final AuthenticationFacade authenticationFacade;

    /**
     * 백엔드에서 회원 접속할 경우
     * 회원의 정보를 알아내는 방법
     * 1. Principal
     * 2. Authentication -> sessionId를 가짐
     * 서비스를 만들면서 Service어노테이션도 쓰고 Component 어노테이션도 사용하면서 계속해서 내려가면서 전달해줘야하는 단점
     * -> SecurityContextHolder: authentication존재 -> SpringApp 어디에서든 정보를 받아올 수 있음
     */
    @GetMapping
    public String home(Authentication authentication) {
        try {
            Object details = authenticationFacade.getAuthentication().getDetails();
            log.info("detatils: {}", details);

//            Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
//            log.info("detatils: {}",details);
//
//            log.info("connected user:{}", authentication.getName());
//            log.info("get Details:{}", authentication.getDetails());
//            log.info("connected user:{}", principal.getName());
        } catch (NullPointerException e) {
            log.info("no user logged in");
        }
        return "index";
    }
}
