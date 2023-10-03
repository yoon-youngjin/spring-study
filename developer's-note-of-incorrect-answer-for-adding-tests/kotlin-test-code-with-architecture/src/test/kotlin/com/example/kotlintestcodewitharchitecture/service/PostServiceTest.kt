package com.example.kotlintestcodewitharchitecture.service

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import com.example.kotlintestcodewitharchitecture.model.dto.PostCreateDto
import com.example.kotlintestcodewitharchitecture.model.dto.PostUpdateDto
import com.example.kotlintestcodewitharchitecture.repository.PostEntity
import com.example.kotlintestcodewitharchitecture.repository.PostRepository
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
import com.example.kotlintestcodewitharchitecture.repository.UserRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.Clock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class PostServiceTest(
    @Autowired
    private val postService: PostService,
    @Autowired
    private val postRepository: PostRepository,
    @Autowired
    private val userRepository: UserRepository,
) {

    @AfterEach
    fun clean() {
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @Test
    fun `getById로 존재하는 게시물을 찾을 수 있다`() {
        // given
        val savedUserEntity = userRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )
        val savedPost = postRepository.save(
            PostEntity(
                content = "test",
                createdAt = Clock.systemUTC().millis(),
                writer = savedUserEntity,
            )
        )

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
        val savedUserEntity = userRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )

        val request = PostCreateDto(
            writerId = savedUserEntity.id,
            content = "test",
        )

        // when
        val result = postService.create(request)

        // then
        assertSoftly(result) {
            it.id.shouldNotBeNull()
        }
    }

    @Test
    fun `postUpdateDto를 이용하여 게시물을 수정할 수 있다`() {
        // given

        val savedUserEntity = userRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )
        val savedPost = postRepository.save(
            PostEntity(
                content = "test",
                createdAt = Clock.systemUTC().millis(),
                writer = savedUserEntity,
            )
        )
        val request = PostUpdateDto(
            content = "change-content",
        )

        // when
        val result = postService.update(savedPost.id, request)

        // then
        assertSoftly(result) {
            it.content shouldBe request.content
            it.writer.id shouldBe savedPost.writer.id
        }
    }
}