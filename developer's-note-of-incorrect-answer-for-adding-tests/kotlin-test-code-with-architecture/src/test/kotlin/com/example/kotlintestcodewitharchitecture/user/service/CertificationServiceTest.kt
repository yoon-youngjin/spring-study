package com.example.kotlintestcodewitharchitecture.user.service

import com.example.kotlintestcodewitharchitecture.mock.FakeMailSender
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CertificationServiceTest {

    @Test
    fun `이메일과 컨텐츠가 정상적으로 만들어져서 보내지는지 테스트한다`() {
        // given
        val email = "dudwls143@gmail.com"
        val mailSender = FakeMailSender()
        val certificationService = CertificationService(mailSender)

        // when
        certificationService.send(
            email = email,
            userId = 1L,
            certificationCode = "aaaaaa-aaaa-aaa-aaa",
        )

        // then
        val sendMails = mailSender.getSendMails(email)
        assertSoftly(sendMails[0]) {
            it.first shouldBe "Please certify your email address"
            it.second shouldBe
                "Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaaa-aaaa-aaa-aaa"
        }
    }
}