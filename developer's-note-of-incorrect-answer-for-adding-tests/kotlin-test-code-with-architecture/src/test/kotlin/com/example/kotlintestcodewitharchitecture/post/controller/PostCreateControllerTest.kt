package com.example.kotlintestcodewitharchitecture.post.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Clock
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus


class PostCreateControllerTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val clockHolder = standaloneTestContext.ref<ClockHolder>() as StubClockHolder
    private val userRepository: UserRepository = standaloneTestContext.ref()
    private val postCreateController: PostCreateController = standaloneTestContext.ref()

    @Test
    fun `사용자는 게시물을 작성할 수 있다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val postCreate = random<PostCreate>().copy(
            writerId = savedUser.id
        )
        clockHolder.setUp(Clock.systemUTC().millis())

        // when
        val result = postCreateController.create(postCreate)

        // then
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.CREATED
            it.body shouldNotBe null
            it.body?.let {
                it.content shouldBe postCreate.content
                it.writer.id shouldBe postCreate.writerId
            }
        }
    }
}