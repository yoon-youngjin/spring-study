package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.random
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class PostCreateControllerTest(
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
    fun `사용자는 게시물을 작성할 수 있다`() {
        // given
        val savedUserEntity = userJpaRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )

        val postCreate = random<PostCreate>().copy(
            writerId = savedUserEntity.id
        )

        // when, then
        mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postCreate)))
            .andExpect(status().isCreated)
    }

}