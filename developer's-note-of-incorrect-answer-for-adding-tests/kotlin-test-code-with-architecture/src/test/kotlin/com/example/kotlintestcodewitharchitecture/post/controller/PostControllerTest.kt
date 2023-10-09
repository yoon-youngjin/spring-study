package com.example.kotlintestcodewitharchitecture.post.controller

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus


class PostControllerTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val clockHolder = standaloneTestContext.ref<ClockHolder>() as StubClockHolder
    private val userRepository: UserRepository = standaloneTestContext.ref()
    private val postRepository: PostRepository = standaloneTestContext.ref()
    private val postController: PostController = standaloneTestContext.ref()

    @Test
    fun `사용자는 게시물을 단건 조회할 수 있다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val savedPost = postRepository.save(
            random<Post>().copy(
                writer = savedUser
            )
        )

        // when
        val result = postController.getById(savedPost.id)

        // then
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.OK
            it.body?.let {
                it.id shouldBe savedPost.id
                it.content shouldBe savedPost.content
                it.writer.id shouldBe savedUser.id
                it.writer.email shouldBe savedUser.email
            }
        }
    }

    @Test
    fun `사용자가 존재하지 않는 게시물을 조회할 경우 에러가 발생한다`() {
        // when, then
        val exception = assertThrows<ResourceNotFoundException> {
            postController.getById(0L)
        }
        exception.message shouldBe  "Posts 에서 ID 0 를 찾을 수 없습니다."
    }

    @Test
    fun `사용자는 게시물을 수정할 수 있다`() {
        // given
        val savedUser = userRepository.save(
            random<User>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val savedPost = postRepository.save(
            random<Post>().copy(
                writer = savedUser
            )
        )
        val postUpdate = random<PostUpdate>()
        clockHolder.setUp(Clock.systemUTC().millis())


        // when
        val result = postController.update(savedPost.id, postUpdate)

        // then
        assertSoftly(result) { it ->
            it.statusCode shouldBe HttpStatus.OK
            it.body?.let {
                it.id shouldBe savedPost.id
                it.content shouldBe postUpdate.content
                it.writer.id shouldBe savedUser.id
                it.writer.email shouldBe savedUser.email
                it.writer.nickname shouldBe savedUser.nickname
            }
        }
    }
}