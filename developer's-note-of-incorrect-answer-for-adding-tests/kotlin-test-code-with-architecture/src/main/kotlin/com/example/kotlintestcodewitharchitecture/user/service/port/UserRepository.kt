package com.example.kotlintestcodewitharchitecture.user.service.port

import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus

interface UserRepository {
    fun getById(id: Long): User
    fun findByIdOrNull(id: Long): User?
    fun findByIdAndStatus(id: Long, userStatus: UserStatus): User?
    fun findByEmailAndStatus(email: String, userStatus: UserStatus): User?
    fun save(user: User): User
}