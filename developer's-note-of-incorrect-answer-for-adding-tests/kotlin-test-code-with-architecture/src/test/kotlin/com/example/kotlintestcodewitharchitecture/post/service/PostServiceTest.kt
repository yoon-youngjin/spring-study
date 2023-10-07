package com.example.kotlintestcodewitharchitecture.post.service

import com.example.kotlintestcodewitharchitecture.StandaloneTestContext
import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class PostServiceTest {
    private val standaloneTestContext = StandaloneTestContext()

    private val userRepository = standaloneTestContext.userRepository
    private val postRepository = standaloneTestContext.postRepository

    private val clockHolder = standaloneTestContext.clockHolder
    private val postService = standaloneTestContext.postService

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