package com.example.kotlintestcodewitharchitecture.repository

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Column(name = "address", nullable = false)
    val address: String,

    @Column(name = "certification_code", nullable = false)
    val certificationCode: String,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    @Column(name = "last_login_at")
    var lastLoginAt: Long? = null,
)