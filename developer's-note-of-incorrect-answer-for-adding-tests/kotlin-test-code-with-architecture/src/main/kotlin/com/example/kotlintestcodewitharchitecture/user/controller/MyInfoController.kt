package com.example.kotlintestcodewitharchitecture.user.controller

import com.example.kotlintestcodewitharchitecture.user.controller.port.UserService
import com.example.kotlintestcodewitharchitecture.user.controller.request.UserUpdateRequest
import com.example.kotlintestcodewitharchitecture.user.controller.response.MyProfileResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users/me")
class MyInfoController(
    private val userService: UserService,
) {

    @GetMapping
    fun getMyInfo(
        @RequestHeader("EMAIL") email: String,
    ): ResponseEntity<MyProfileResponse> {
        val user = userService.getByEmail(email)
        userService.login(user.id)
        return ResponseEntity
            .ok()
            .body(MyProfileResponse.of(user))
    }

    @PutMapping
    fun update(
        @RequestHeader("EMAIL") email: String,
        @RequestBody userUpdate: UserUpdateRequest,
    ): ResponseEntity<MyProfileResponse> {
        val user = userService.getByEmail(email)
        val updateUser = userService.update(user.id, userUpdate.toServiceDto())
        return ResponseEntity
            .ok()
            .body(MyProfileResponse.of(updateUser))
    }
}