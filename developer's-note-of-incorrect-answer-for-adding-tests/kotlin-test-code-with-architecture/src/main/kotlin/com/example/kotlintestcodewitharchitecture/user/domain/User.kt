package com.example.kotlintestcodewitharchitecture.user.domain

import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.common.service.port.UuidHolder

data class User(
    val id: Long = 0,
    val email: String,
    val nickname: String,
    val address: String,
    val certificationCode: String,
    var status: UserStatus,
    var lastLoginAt: Long? = null,
) {
    fun update(userUpdate: UserUpdate): User {
        return this.copy(
            nickname = userUpdate.nickname,
            address = userUpdate.address,
        )
    }

    fun login(clockHolder: ClockHolder): User {
        return this.copy(
            lastLoginAt = clockHolder.millis(),
        )
    }

    fun certificate(certificationCode: String): User {
        if (certificationCode != this.certificationCode) {
            throw CertificationCodeNotMatchedException()
        }
        return this.copy(
            status = UserStatus.ACTIVE
        )
    }

    companion object {
        fun from(userCreate: UserCreate, uuidHolder: UuidHolder): User {
            return User(
                email = userCreate.email,
                nickname = userCreate.nickname,
                address = userCreate.address,
                status = UserStatus.PENDING,
                certificationCode = uuidHolder.random(),
            )
        }
    }
}