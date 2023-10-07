package com.example.kotlintestcodewitharchitecture.user.controller.response

import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val status: UserStatus,
    val lastLoginAt: Long?,
) {
    companion object {
        fun of(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                status = user.status,
                lastLoginAt = user.lastLoginAt,
            )
        }
    }
}