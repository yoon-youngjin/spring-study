package com.example.kotlintestcodewitharchitecture.user.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class UserControllerTest {

    private val standaloneTestContext = StandaloneTestContext()

    private val clockHolder = standaloneTestContext.clockHolder
    private val userRepository = standaloneTestContext.userRepository
    private val userController = standaloneTestContext.userController

    @Test
    fun `특정 유저의 정보를 전달 받을 수 있다 - 개인정보는 소거되어야 한다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )

        // when
        val result = userController.getById(savedUser.id)

        // then
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.OK
            it.body shouldNotBe null
            it.body?.let {
                it.id shouldBe savedUser.id
                it.email shouldBe savedUser.email
                it.nickname shouldBe savedUser.nickname
                it.status shouldBe savedUser.status
            }
        }
    }

    @Test
    fun `사용자는 존재하지 않는 유저의 아이디로 api 호출할 경우 404 응답을 받는다`() {
        // when, then
        val exception = shouldThrow<ResourceNotFoundException> {
            userController.getById(0L)
        }
        exception.message shouldBe "Users 에서 ID 0 를 찾을 수 없습니다."
    }

    @Test
    fun `사용자는 인증 코드로 계정을 활성화 시킬 수 있다`() {
        // given
        val certificateCode = random<String>()
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.PENDING,
                certificationCode = certificateCode,
            )
        )

        // when, then
        val result = userController.verifyEmail(savedUser.id, certificateCode)

        // then
        val verifiedUser = userRepository.findByIdOrNull(savedUser.id)
        assertSoftly {
            result.statusCode shouldBe HttpStatus.FOUND
            verifiedUser!!.status shouldBe UserStatus.ACTIVE
        }
    }

    @Test
    fun `사용자는 인증 코드가 일치하지 않을 경우 권한 없음 에러가 발생한다`() {
        // given
        val certificateCode = random<String>()
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.PENDING,
                certificationCode = certificateCode
            )
        )
        // when, then
        assertSoftly {
            val exception = shouldThrow<CertificationCodeNotMatchedException> {
                userController.verifyEmail(savedUser.id, certificateCode + "wrong")
            }
            exception.message shouldBe "자격 증명에 실패하였습니다."
        }
    }
}
