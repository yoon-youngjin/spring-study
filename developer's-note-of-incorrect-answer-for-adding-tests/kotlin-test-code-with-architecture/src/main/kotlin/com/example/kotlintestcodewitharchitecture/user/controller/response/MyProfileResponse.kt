package com.example.kotlintestcodewitharchitecture.user.controller.response

import com.example.kotlintestcodewitharchitecture.user.domain.User

data class MyProfileResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val address: String,
) {
    companion object {
        fun of(user: User): MyProfileResponse {
            return MyProfileResponse(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
                address = user.address,
            )
        }
    }
}