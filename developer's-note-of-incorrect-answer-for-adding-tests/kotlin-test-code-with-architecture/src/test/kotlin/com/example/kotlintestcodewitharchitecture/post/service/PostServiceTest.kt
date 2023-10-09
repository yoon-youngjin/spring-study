package com.example.kotlintestcodewitharchitecture.post.service

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.mock.StubClockHolder
import com.example.kotlintestcodewitharchitecture.post.controller.port.PostService
import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.Test


class PostServiceTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val userRepository: UserRepository = standaloneTestContext.ref()
    private val postRepository: PostRepository = standaloneTestContext.ref()

    private val clockHolder: StubClockHolder = standaloneTestContext.ref<ClockHolder>() as StubClockHolder
    private val postService = standaloneTestContext.ref<PostService>()

    @Test
    fun `getById로 존재하는 게시물을 찾을 수 있다`() {
        // given
        val post = random<Post>()
        val savedPost = postRepository.save(post)

        // when
        val result = postService.getPostById(savedPost.id)

        // then
        assertSoftly(result) {
            it.content shouldBe savedPost.content
            it.writer shouldBe savedPost.writer
        }
    }

    @Test
    fun `postCreateDto를 이용하여 게시물을 생성할 수 있다`() {
        // given
        val user = random<User>().copy(
            status = UserStatus.ACTIVE
        )
        val savedUser = userRepository.save(user)
        val request = random<PostCreate>().copy(
            writerId = savedUser.id
        )
        clockHolder.setUp(Clock.systemUTC().millis())

        // when
        val result = postService.create(request)

        // then
        assertSoftly(result) {
            it.id.shouldNotBeNull()
            it.createdAt shouldBe clockHolder.millis()
        }
    }

    @Test
    fun `postUpdateDto를 이용하여 게시물을 수정할 수 있다`() {
        // given
        val savedPost = postRepository.save(random<Post>())
        val request = random<PostUpdate>()
        clockHolder.setUp(Clock.systemUTC().millis())

        // when
        val result = postService.update(savedPost.id, request)

        // then
        assertSoftly(result) {
            it.content shouldBe request.content
            it.writer.id shouldBe savedPost.writer.id
            it.modifiedAt shouldBe clockHolder.millis()
        }
    }
}