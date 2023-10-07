package com.example.kotlintestcodewitharchitecture.user.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class UserCreateControllerTest {

    private val standaloneTestContext = StandaloneTestContext()

    private val userRepository = standaloneTestContext.userRepository
    private val userCreateController = standaloneTestContext.userCreateController

    @Test
    fun `사용자는 회원가입을 할 수 있고 회원가입된 사용자는 PENDING 상태이다`() {
        // given
        val userCreate = random<UserCreate>()

        // when
        val result = userCreateController.createUser(userCreate)

        // then
        val savedUser = userRepository.findByEmailAndStatus(userCreate.email, UserStatus.PENDING)!!
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.CREATED
            it.body shouldNotBe null
            it.body?.let {
                it.email shouldBe userCreate.email
                it.nickname shouldBe userCreate.nickname
                it.lastLoginAt shouldBe null
            }
            savedUser.status shouldBe UserStatus.PENDING
        }
    }
}
