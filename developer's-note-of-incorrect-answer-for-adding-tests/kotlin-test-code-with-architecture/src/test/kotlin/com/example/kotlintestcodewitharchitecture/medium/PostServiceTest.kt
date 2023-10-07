package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.post.controller.port.PostService
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.post.infrastructure.PostEntity
import com.example.kotlintestcodewitharchitecture.post.infrastructure.PostJpaRepository
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserJpaRepository
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
    private val postJpaRepository: PostJpaRepository,
    @Autowired
    private val userJpaRepository: UserJpaRepository,
) {

    @AfterEach
    fun clean() {
        postJpaRepository.deleteAllInBatch()
        userJpaRepository.deleteAllInBatch()
    }

    @Test
    fun `getById로 존재하는 게시물을 찾을 수 있다`() {
        // given
        val savedUserEntity = userJpaRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )
        val savedPost = postJpaRepository.save(
            PostEntity(
                content = "test",
                createdAt = Clock.systemUTC().millis(),
                writer = savedUserEntity,
            )
        ).toModel()

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
        val savedUserEntity = userJpaRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )

        val request = PostCreate(
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

        val savedUserEntity = userJpaRepository.save(
            UserEntity(
                email = "dudwls143@gmail.com",
                address = "seoul",
                nickname = "yoon",
                status = UserStatus.ACTIVE,
                certificationCode = "aaaaaa-aaaa-aaa-aaa"
            )
        )
        val savedPost = postJpaRepository.save(
            PostEntity(
                content = "test",
                createdAt = Clock.systemUTC().millis(),
                writer = savedUserEntity,
            )
        ).toModel()
        val request = PostUpdate(
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