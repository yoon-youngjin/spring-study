package com.example.kotlintestcodewitharchitecture.service

import com.example.kotlintestcodewitharchitecture.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.model.UserStatus
import com.example.kotlintestcodewitharchitecture.model.dto.UserCreateDto
import com.example.kotlintestcodewitharchitecture.model.dto.UserUpdateDto
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
import com.example.kotlintestcodewitharchitecture.repository.UserRepository
import java.time.Clock
import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val mailSender: JavaMailSender,
) {
    // get은 애초에 데이터가 없으면 에러를 던진다는 의미가 내포
    fun getByEmail(email: String): UserEntity {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            ?: throw ResourceNotFoundException("Users", email)
    }

    fun getById(id: Long): UserEntity {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            ?: throw ResourceNotFoundException("Users", id)
    }


    @Transactional
    fun create(userCreateDto: UserCreateDto): UserEntity {
        val userEntity = UserEntity(
            email = userCreateDto.email,
            nickname = userCreateDto.nickname,
            address = userCreateDto.address,
            status = UserStatus.PENDING,
            certificationCode = UUID.randomUUID().toString(),
        )

        val savedUserEntity = userRepository.save(userEntity)
        val certificationUrl = generateCertificationUrl(savedUserEntity)
        sendCertificationEmail(savedUserEntity.email, certificationUrl)
        return savedUserEntity
    }

    @Transactional
    fun update(id: Long, userUpdateDto: UserUpdateDto): UserEntity {
        val userEntity = getById(id)
        return userRepository.save(
            userEntity.copy(
                nickname = userUpdateDto.nickname,
                address = userUpdateDto.address,
            )
        )
    }

    @Transactional
    fun login(id: Long) {
        val userEntity = getById(id)
        userEntity.lastLoginAt = Clock.systemUTC().millis()
    }

    @Transactional
    fun verifyEmail(id: Long, certificationCode: String) {
        val userEntity = userRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Users", id)
        if (certificationCode != userEntity.certificationCode) {
            throw CertificationCodeNotMatchedException()
        }
        userEntity.status = UserStatus.ACTIVE
    }

    private fun sendCertificationEmail(email: String, certificationUrl: String) {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = "Please certify your email address"
        message.text = "Please click the following link to certify your email address: $certificationUrl"
        mailSender.send(message)
    }

    private fun generateCertificationUrl(userEntity: UserEntity): String {
        return "http://localhost:8080/api/users/${userEntity.id}/verify?certificationCode=${userEntity.certificationCode}"
    }


}