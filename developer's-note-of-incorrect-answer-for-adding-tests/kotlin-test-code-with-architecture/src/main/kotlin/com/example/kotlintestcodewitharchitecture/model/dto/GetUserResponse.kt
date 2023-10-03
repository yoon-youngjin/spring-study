package com.example.kotlintestcodewitharchitecture.model.dto

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import com.example.kotlintestcodewitharchitecture.repository.UserEntity

data class GetUserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val status: UserStatus,
    val lastLoginAt: Long?,
) {
    companion object {
        fun of(userEntity: UserEntity): GetUserResponse {
            return GetUserResponse(
                id = userEntity.id,
                email = userEntity.email,
                nickname = userEntity.nickname,
                status = userEntity.status,
                lastLoginAt = userEntity.lastLoginAt,
            )
        }
    }
}