package com.example.kotlintestcodewitharchitecture.user.service

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserServiceImplTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val userService = standaloneTestContext.userService
    private val userRepository = standaloneTestContext.userRepository
    private val uuidHolder = standaloneTestContext.uuidHolder
    private val clockHolder = standaloneTestContext.clockHolder

    @Test
    fun `getByEmail은 ACTIVE 상태인 유저를 찾아올 수 있다`() {
        // given
        val email = random<String>()
        val user = random<User>().copy(
            email = email,
            status = UserStatus.ACTIVE,
        )
        userRepository.save(user)

        // when
        val result = userService.getByEmail(email)

        // then
        result.email shouldBe email
    }

    @Test
    fun `getByEmail은 PENDING 상태인 유저는 찾아올 수 없다`() {
        // given
        val email = random<String>()
        val user = random<User>().copy(
            email = email,
            status = UserStatus.PENDING,
        )
        userRepository.save(user)

        // when, then
        val exception = shouldThrow<ResourceNotFoundException> {
            userService.getByEmail(email)
        }
        exception.message shouldBe "Users 에서 ID $email 를 찾을 수 없습니다."
    }

    @Test
    fun `getById은 ACTIVE 상태인 유저를 찾아올 수 있다`() {
        // given
        val user = random<User>().copy(
            status = UserStatus.ACTIVE,
        )
        val savedUser = userRepository.save(user)

        // when
        val result = userService.getById(savedUser.id)

        // then
        result.shouldNotBeNull()
    }

    @Test
    fun `getById은 PENDING 상태인 유저는 찾아올 수 없다`() {
        // given
        val user = random<User>().copy(
            status = UserStatus.PENDING,
        )
        val savedUserEntity = userRepository.save(user)

        // when, then
        val exception = shouldThrow<ResourceNotFoundException> {
            userService.getById(savedUserEntity.id)
        }
        exception.message shouldBe "Users 에서 ID ${savedUserEntity.id} 를 찾을 수 없습니다."
    }

    @Test
    fun `userCreateDto를 이용하여 유저를 생성할 수 있다`() {
        // given
        val request = random<UserCreate>()

        // when
        val result = userService.create(request)

        // then
        assertSoftly(result) {
            it.id.shouldNotBeNull()
            it.status shouldBe UserStatus.PENDING
            it.certificationCode shouldBe uuidHolder.random()
        }
    }

    @Test
    fun `userUpdateDto를 이용하여 유저를 수정할 수 있다`() {
        // given
        val user = random<User>().copy(
            status = UserStatus.ACTIVE
        )
        val savedUser = userRepository.save(user)
        val request = random<UserUpdate>()

        // when
        val result = userService.update(savedUser.id, request)

        // then
        assertSoftly(result) {
            it.address shouldBe request.address
            it.nickname shouldBe request.nickname
            it.status shouldBe user.status
            it.email shouldBe user.email
        }
    }

    @Test
    fun `user가 로그인하면 마지막 로그인 시간이 변경된다`() {
        // given
        val user = random<User>().copy(
            status = UserStatus.ACTIVE
        )
        val savedUser = userRepository.save(user)

        // when
        userService.login(savedUser.id)

        // then
        val result = userService.getById(savedUser.id)
        assertSoftly(result) {
            it.lastLoginAt = clockHolder.millis()
        }
    }

    @Test
    fun `PENDING 상태의 사용자는 인증 코드로 ACTIVE 시킬 수 있다`() {
        // given
        val certificationCode = random<String>()
        val user = random<User>().copy(
            status = UserStatus.PENDING,
            certificationCode = certificationCode,
        )
        val savedUserEntity = userRepository.save(user)

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
        val user = random<User>().copy(
            status = UserStatus.PENDING,
            certificationCode = certificationCode
        )
        val savedUserEntity = userRepository.save(user)

        // when, then
        val exception = shouldThrow<CertificationCodeNotMatchedException> {
            userService.verifyEmail(savedUserEntity.id, certificationCode + "wrong")
        }
        exception.message shouldBe "자격 증명에 실패하였습니다."
    }
}
