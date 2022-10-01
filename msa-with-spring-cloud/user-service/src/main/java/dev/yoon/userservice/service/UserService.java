package dev.yoon.userservice.service;

import dev.yoon.userservice.dto.UserDto;
import dev.yoon.userservice.entity.UserEntity;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getUserByUserId(String userId);

    List<UserEntity> getUserByAll();

}
