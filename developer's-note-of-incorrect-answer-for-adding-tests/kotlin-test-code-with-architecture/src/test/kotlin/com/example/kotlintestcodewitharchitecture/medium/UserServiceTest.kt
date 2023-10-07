package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.user.controller.port.UserService
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserJpaRepository
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class UserServiceTest(
    @Autowired
    private val userService: UserService,
    @Autowired
    private val userJpaRepository: UserJpaRepository,
) {

    @MockBean
    private lateinit var mailSender: JavaMailSender

    @AfterEach
    fun clean() {
        userJpaRepository.deleteAllInBatch()
    }

    @Test
    fun `getByEmail은 ACTIVE 상태인 유저를 찾아올 수 있다`() {
        // given
        val email = "dudwls143@gmail.com"
        val userEntity = UserEntity(
            email = email,
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        userJpaRepository.save(userEntity)

        // when
        val result = userService.getByEmail(email)

        // then
        result.email shouldBe email
    }

    @Test
    fun `getByEmail은 PENDING 상태인 유저는 찾아올 수 없다`() {
        // given
        val email = "dudwls143@gmail.com"
        val userEntity = UserEntity(
            email = email,
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.PENDING,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        userJpaRepository.save(userEntity)

        // when, then
        val exception = shouldThrow<ResourceNotFoundException> {
            userService.getByEmail(email)
        }
        exception.message shouldBe "Users 에서 ID $email 를 찾을 수 없습니다."
    }

    @Test
    fun `getById은 ACTIVE 상태인 유저를 찾아올 수 있다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        val result = userService.getById(savedUserEntity.id)

        // then
        result.shouldNotBeNull()
    }

    @Test
    fun `getById은 PENDING 상태인 유저는 찾아올 수 없다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.PENDING,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when, then
        val exception = shouldThrow<ResourceNotFoundException> {
            userService.getById(savedUserEntity.id)
        }
        exception.message shouldBe "Users 에서 ID ${savedUserEntity.id} 를 찾을 수 없습니다."
    }

    @Test
    fun `userCreateDto를 이용하여 유저를 생성할 수 있다`() {
        // given
        val request = UserCreate(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
        )
        BDDMockito.doNothing().`when`(mailSender).send(SimpleMailMessage())

        // when
        val result = userService.create(request)

        // then
        assertSoftly(result) {
            it.id.shouldNotBeNull()
            it.status shouldBe UserStatus.PENDING
//            it.certificationCode shouldBe
        }
    }

    @Test
    fun `userUpdateDto를 이용하여 유저를 수정할 수 있다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)
        val request = UserUpdate(
            address = "change-address",
            nickname = "yoon2",
        )

        // when
        val result = userService.update(savedUserEntity.id, request)

        // then
        assertSoftly(result) {
            it.address shouldBe request.address
            it.nickname shouldBe request.nickname
            it.status shouldBe userEntity.status
            it.email shouldBe userEntity.email
        }
    }

    @Test
    fun `user가 로그인하면 마지막 로그인 시간이 변경된다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        userService.login(savedUserEntity.id)

        // then
        val result = userService.getById(savedUserEntity.id)
        println(result.lastLoginAt!! > 0)
//        result.lastLoginAt!! shouldBe 0L
        // result.lastLoginAt shouldBe
    }

    @Test
    fun `PENDING 상태의 사용자는 인증 코드로 ACTIVE 시킬 수 있다`() {
        // given
        val certificationCode = "aaaaaa-aaaa-aaa-aaa"
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.PENDING,
            certificationCode = certificationCode
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        userService.verifyEmail(savedUserEntity.id, certificationCode)

        // then
        val result = userService.getById(savedUserEntity.id)
        result.status shouldBe UserStatus.ACTIVE
    }

    @Test
    fun `PENDING 상태의 사용자는 잘못된 인증 코드를 받으면 에러를 던진다`() {
        // given
        val certificationCode = "aaaaaa-aaaa-aaa-aaa"
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.PENDING,
            certificationCode = certificationCode
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when, then
        val exception = shouldThrow<CertificationCodeNotMatchedException> {
            userService.verifyEmail(savedUserEntity.id, "aaaaaa-aaaa-aaa-aaa-bbb")
        }
        exception.message shouldBe "자격 증명에 실패하였습니다."
    }
}
