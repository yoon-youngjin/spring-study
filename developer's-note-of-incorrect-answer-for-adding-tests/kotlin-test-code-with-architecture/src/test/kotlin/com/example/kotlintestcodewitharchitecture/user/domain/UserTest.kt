package com.example.kotlintestcodewitharchitecture.user.domain

import com.example.kotlintestcodewitharchitecture.common.domain.exception.CertificationCodeNotMatchedException
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubUuidHolder
import com.example.kotlintestcodewitharchitecture.random
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {
    @Test
    fun `User는 UserCreate로 데이터를 만들 수 있다`() {
        // given
        val userCreate = random<UserCreate>()
        val uuidHolder = StubUuidHolder("aaaaaaa-aa-aaaa-aaaa")

        // when
        val result = User.from(userCreate, uuidHolder)

        // then
        assertSoftly(result) {
            it.email shouldBe userCreate.email
            it.address shouldBe userCreate.address
            it.nickname shouldBe userCreate.nickname
            it.status shouldBe UserStatus.PENDING
            it.certificationCode shouldBe uuidHolder.random()
        }
    }

    @Test
    fun `User는 UserUpdate로 데이터를 업데이트 할 수 있다`() {
        // given
        val user = random<User>()
        val userUpdate = random<UserUpdate>()

        // when
        val result = user.update(userUpdate)

        // then
        assertSoftly(result) {
            it.address shouldBe userUpdate.address
            it.nickname shouldBe userUpdate.nickname
            it.certificationCode shouldBe user.certificationCode
            it.status shouldBe user.status
        }
    }

    @Test
    fun `User는 로그인을 할 수 있고 로그인시 마지막 로그인 시간이 변경된다`() {
        // given
        val user = random<User>()
        val clockHolder = StubClockHolder(Clock.systemUTC().millis())

        // when
        val result = user.login(clockHolder)

        // then
        assertSoftly(result) {
            it.lastLoginAt shouldBe clockHolder.millis()
            it.email shouldBe user.email
            it.nickname shouldBe user.nickname
            it.status shouldBe user.status
        }
    }

    @Test
    fun `User는 인증 코드로 계정을 활성화 할 수 있다`() {
        // given
        val certificateCode = random<String>()
        val user = random<User>().copy(
            status = UserStatus.PENDING,
            certificationCode = certificateCode
        )

        // when
        val result = user.certificate(certificateCode)

        // then
        result.status shouldBe UserStatus.ACTIVE
    }

    @Test
    fun `User는 잘못된 인증 코드로 계정을 활성화 하려하면 에러를 던진다`() {
        // given
        val certificateCode = random<String>()
        val user = random<User>().copy(
            status = UserStatus.PENDING,
            certificationCode = certificateCode
        )

        // when, then
        val exception = assertThrows<CertificationCodeNotMatchedException> {
            user.certificate(certificateCode + "wrong")
        }
        exception.message shouldBe "자격 증명에 실패하였습니다."
    }
}