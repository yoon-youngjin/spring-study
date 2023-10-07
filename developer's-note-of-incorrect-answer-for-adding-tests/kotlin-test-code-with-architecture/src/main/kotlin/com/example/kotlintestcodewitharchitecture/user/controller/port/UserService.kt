package com.example.kotlintestcodewitharchitecture.user.controller.port

import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate

interface UserService {
    fun getById(id: Long): User
    fun verifyEmail(id: Long, certificationCode: String)
    fun getByEmail(email: String): User
    fun login(id: Long)
    fun update(id: Long, userUpdate: UserUpdate): User
    fun create(userCreate: UserCreate): User
}