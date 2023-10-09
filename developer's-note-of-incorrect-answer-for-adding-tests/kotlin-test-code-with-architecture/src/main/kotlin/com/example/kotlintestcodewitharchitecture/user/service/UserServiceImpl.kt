package com.example.kotlintestcodewitharchitecture.user.service

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.common.service.port.UuidHolder
import com.example.kotlintestcodewitharchitecture.user.controller.port.UserService
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val certificationService: CertificationService,
    private val uuidHolder: UuidHolder,
    private val clockHolder: ClockHolder,
) : UserService {
    // get은 애초에 데이터가 없으면 에러를 던진다는 의미가 내포
    override fun getByEmail(email: String): User {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            ?: throw ResourceNotFoundException("Users", email)
    }

    override fun getById(id: Long): User {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            ?: throw ResourceNotFoundException("Users", id)
    }


    @Transactional
    override fun create(userCreate: UserCreate): User {
        val user = User.from(userCreate, uuidHolder)
        val savedUser = userRepository.save(user)
        certificationService.send(
            email = savedUser.email,
            userId = savedUser.id,
            certificationCode = savedUser.certificationCode
        )
        return savedUser
    }

    @Transactional
    override fun update(id: Long, userUpdate: UserUpdate): User {
        val user = getById(id)
        val updatedUser = user.update(
            userUpdate = userUpdate,
        )
        return userRepository.save(updatedUser)
    }

    @Transactional
    override fun login(id: Long) {
        val user = getById(id)
        userRepository.save(user.login(clockHolder))
    }

    @Transactional
    override fun verifyEmail(id: Long, certificationCode: String) {
        val user = userRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Users", id)
        val certificatedUser = user.certificate(certificationCode)
        userRepository.save(certificatedUser)
    }
}