package com.example.kotlintestcodewitharchitecture.user.controller

import com.example.kotlintestcodewitharchitecture.user.controller.port.UserService
import com.example.kotlintestcodewitharchitecture.user.controller.response.UserResponse
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
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
        @RequestBody userCreate: UserCreate,
    ): ResponseEntity<UserResponse> {
        val user = userService.create(userCreate)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(UserResponse.of(user))
    }
}