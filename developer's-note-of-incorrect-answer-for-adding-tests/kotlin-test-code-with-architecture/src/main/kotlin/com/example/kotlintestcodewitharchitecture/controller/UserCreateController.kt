package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.dto.GetUserResponse
import com.example.kotlintestcodewitharchitecture.model.dto.UserCreateDto
import com.example.kotlintestcodewitharchitecture.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserCreateController(
    private val userService: UserService,
) {
    @PostMapping
    fun createUser(
        @RequestBody userCreateDto: UserCreateDto,
    ): ResponseEntity<GetUserResponse> {
        val userEntity = userService.create(userCreateDto)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(GetUserResponse.of(userEntity))
    }
}