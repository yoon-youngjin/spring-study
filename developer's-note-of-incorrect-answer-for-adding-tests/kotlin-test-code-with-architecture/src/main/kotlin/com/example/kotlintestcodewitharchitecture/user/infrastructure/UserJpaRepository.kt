package com.example.kotlintestcodewitharchitecture.user.infrastructure

import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {

    fun findByIdAndStatus(id: Long, userStatus: UserStatus) : UserEntity?
    fun findByEmailAndStatus(email: String, userStatus: UserStatus) : UserEntity?
}