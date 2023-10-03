package com.example.kotlintestcodewitharchitecture.repository

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {

    fun findByIdAndStatus(id: Long, userStatus: UserStatus) : UserEntity?
    fun findByEmailAndStatus(email: String, userStatus: UserStatus) : UserEntity?
}