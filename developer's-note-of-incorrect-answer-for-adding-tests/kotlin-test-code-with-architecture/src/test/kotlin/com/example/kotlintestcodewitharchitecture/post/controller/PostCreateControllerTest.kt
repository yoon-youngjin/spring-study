package com.example.kotlintestcodewitharchitecture.post.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus


class PostCreateControllerTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val userRepository = standaloneTestContext.userRepository
    private val postCreateController = standaloneTestContext.postCreateController

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