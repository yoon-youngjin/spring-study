package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.post.infrastructure.PostEntity
import com.example.kotlintestcodewitharchitecture.post.infrastructure.PostJpaRepository
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserJpaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class PostControllerTest (
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val userJpaRepository: UserJpaRepository,
    @Autowired
    private val postJpaRepository: PostJpaRepository,
) {
    @AfterEach
    fun clean() {
        postJpaRepository.deleteAllInBatch()
        userJpaRepository.deleteAllInBatch()
    }

    @Test
    fun `사용자는 게시물을 단건 조회할 수 있다`() {
        // given
        val savedUserEntity = userJpaRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val savedPostEntity = postJpaRepository.save(
            random<PostEntity>().copy(
                writer = savedUserEntity
            )
        )

        // when, then
        mockMvc.perform(get("/api/posts/${savedPostEntity.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(savedPostEntity.id.toString()))
            .andExpect(jsonPath("$.content").value(savedPostEntity.content))
            .andExpect(jsonPath("$.writer.id").value(savedPostEntity.writer.id.toString()))
            .andExpect(jsonPath("$.writer.email").value(savedPostEntity.writer.email.toString()))
            .andExpect(jsonPath("$.writer.nickname").value(savedPostEntity.writer.nickname))
    }

    @Test
    fun `사용자가 존재하지 않는 게시물을 조회할 경우 에러가 발생한다`() {
        // when, then
        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isNotFound)
            .andExpect(content().string("Posts 에서 ID 1 를 찾을 수 없습니다."))
    }

    @Test
    fun `사용자는 게시물을 수정할 수 있다`() {
        // given
        val savedUserEntity = userJpaRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val savedPostEntity = postJpaRepository.save(
            random<PostEntity>().copy(
                writer = savedUserEntity
            )
        )

        val postUpdate = random<PostUpdate>()

        mockMvc.perform(put("/api/posts/${savedPostEntity.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postUpdate)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(savedPostEntity.id.toString()))
            .andExpect(jsonPath("$.content").value(postUpdate.content))
            .andExpect(jsonPath("$.writer.id").value(savedPostEntity.writer.id.toString()))
            .andExpect(jsonPath("$.writer.email").value(savedPostEntity.writer.email.toString()))
            .andExpect(jsonPath("$.writer.nickname").value(savedPostEntity.writer.nickname))
    }
}