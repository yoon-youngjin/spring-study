package com.example.kotlintestcodewitharchitecture.user.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.controller.request.UserUpdateRequest
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class MyInfoControllerTest {

    private val standaloneTestContext = StandaloneTestContext()

    private val clockHolder = standaloneTestContext.clockHolder
    private val userRepository = standaloneTestContext.userRepository
    private val myInfoController = standaloneTestContext.myInfoController

    @Test
    fun `사용자는 내 정보를 불러올 때 개인정보인 주소도 갖고 올 수 있다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )

        // when
        val result = myInfoController.getMyInfo(savedUser.email)

        // then
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.OK
            it.body?.let {
                it.id shouldBe savedUser.id
                it.email shouldBe savedUser.email
                it.nickname shouldBe savedUser.nickname
                it.address shouldBe savedUser.address
            }
            userRepository.findByIdOrNull(savedUser.id)!!.lastLoginAt shouldBe clockHolder.millis()
        }
    }

    @Test
    fun `사용자는 내 정보를 수정할 수 있다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val userUpdateRequest = random<UserUpdateRequest>()

        // when, then
        val result = myInfoController.update(savedUser.email, userUpdateRequest)
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.OK
            it.body?.let {
                it.id shouldBe savedUser.id
                it.nickname shouldBe userUpdateRequest.nickname
                it.address shouldBe userUpdateRequest.address
            }
        }
    }
}
