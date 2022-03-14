package dev.yoon.study_spring_security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    private String email;

    private String password;
}
