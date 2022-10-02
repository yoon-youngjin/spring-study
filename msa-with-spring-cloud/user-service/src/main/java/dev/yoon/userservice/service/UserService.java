package dev.yoon.userservice.service;

import dev.yoon.userservice.dto.UserDto;
import dev.yoon.userservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDto create(UserDto userDto);

    UserDto getUserByUserId(String userId);

    List<UserEntity> getUserByAll();

    UserDto getUserDetailsByEmail(String email);
}
