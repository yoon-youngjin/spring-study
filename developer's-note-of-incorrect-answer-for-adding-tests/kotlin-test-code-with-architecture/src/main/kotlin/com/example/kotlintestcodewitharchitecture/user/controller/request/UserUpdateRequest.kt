package com.example.kotlintestcodewitharchitecture.user.controller.request

import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate

class UserUpdateRequest(
    val nickname: String,
    val address: String,
) {
    // 이런식으로 작성해서 컨트롤러에 존재하는 DTO를 서비스로 넘기지 않을 수 있다.
    fun toServiceDto(): UserUpdate {
        return UserUpdate(
            nickname = nickname,
            address = address,
        )
    }
}