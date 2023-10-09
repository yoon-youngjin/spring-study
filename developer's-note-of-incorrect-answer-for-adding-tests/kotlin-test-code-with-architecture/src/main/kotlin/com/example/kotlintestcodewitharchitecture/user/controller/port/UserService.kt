package com.example.kotlintestcodewitharchitecture.user.controller.port

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.common.service.port.UuidHolder
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import com.example.kotlintestcodewitharchitecture.user.service.CertificationService
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface UserService {
    fun getById(id: Long): User
    fun verifyEmail(id: Long, certificationCode: String)
    fun getByEmail(email: String): User
    fun login(id: Long)
    fun update(id: Long, userUpdate: UserUpdate): User
    fun create(userCreate: UserCreate): User
}
