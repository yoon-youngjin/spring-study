package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.dto.GetMyProfileResponse
import com.example.kotlintestcodewitharchitecture.model.dto.GetUserResponse
import com.example.kotlintestcodewitharchitecture.model.dto.UserUpdateDto
import com.example.kotlintestcodewitharchitecture.service.UserService
import java.net.URI
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<GetUserResponse> {
        return ResponseEntity.ok(
            GetUserResponse.of(
                userEntity = userService.getById(id)
            )
        )
    }

    @GetMapping("/{id}/verify")
    fun verifyEmail(
        @PathVariable id: Long,
        @RequestParam certificationCode: String,
    ): ResponseEntity<Void> {
        userService.verifyEmail(id, certificationCode)
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("http://localhost:3000"))
            .build()
    }

    @GetMapping("/me")
    fun getMyInfo(
        @RequestHeader("EMAIL") email: String,
    ): ResponseEntity<GetMyProfileResponse> {
        val userEntity = userService.getByEmail(email)
        userService.login(userEntity.id)
        return ResponseEntity
            .ok()
            .body(GetMyProfileResponse.of(userEntity))
    }

    @PutMapping("/me")
    fun updateMyInfo(
        @RequestHeader("EMAIL") email: String,
        @RequestBody userUpdateDto: UserUpdateDto,
    ): ResponseEntity<GetMyProfileResponse> {
        val userEntity = userService.getByEmail(email)
        val updatedUserEntity = userService.update(userEntity.id, userUpdateDto)
        return ResponseEntity
            .ok()
            .body(GetMyProfileResponse.of(updatedUserEntity))
    }
}