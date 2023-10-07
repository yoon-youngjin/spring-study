package com.example.kotlintestcodewitharchitecture.user.infrastructure

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun findByIdAndStatus(id: Long, userStatus: UserStatus): User? {
        return userJpaRepository.findByIdAndStatus(
            id = id,
            userStatus = userStatus
        )?.toModel()
    }

    override fun findByEmailAndStatus(email: String, userStatus: UserStatus): User? {
        return userJpaRepository.findByEmailAndStatus(
            email = email,
            userStatus = userStatus
        )?.toModel()
    }

    override fun save(user: User): User {
        return userJpaRepository.save(UserEntity.from(user)).toModel()
    }

    override fun getById(id: Long): User {
        return findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Users", id)
    }

    override fun findByIdOrNull(id: Long): User? {
        return userJpaRepository.findByIdOrNull(id)?.toModel()
    }

}