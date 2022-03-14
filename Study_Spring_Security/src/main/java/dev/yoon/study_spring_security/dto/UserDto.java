package dev.yoon.study_spring_security.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {

    private String email;

    private String password;

    private String auth;
}
