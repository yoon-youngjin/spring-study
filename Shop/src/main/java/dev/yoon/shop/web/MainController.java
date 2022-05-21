package dev.yoon.shop.web;

import dev.yoon.shop.global.config.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {

    @GetMapping("/")
    public String main(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails!=null) {
            System.out.println(userDetails.getUsername());
            userDetails.getAuthorities().stream().forEach(
                    grantedAuthority -> System.out.println(grantedAuthority.getAuthority())
            );

        }

        return "main";
    }

}
