package com.example.kotlintestcodewitharchitecture.model.dto

import com.example.kotlintestcodewitharchitecture.repository.UserEntity

data class GetMyProfileResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val address: String,
) {
    companion object {
        fun of(userEntity: UserEntity): GetMyProfileResponse {
            return GetMyProfileResponse(
                id = userEntity.id,
                email = userEntity.email,
                nickname = userEntity.nickname,
                address = userEntity.address,
            )
        }
    }
}