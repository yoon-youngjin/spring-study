package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import com.example.kotlintestcodewitharchitecture.model.dto.PostCreateDto
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.repository.PostRepository
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
import com.example.kotlintestcodewitharchitecture.repository.UserRepository
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
    private val userRepository: UserRepository,
    @Autowired
    private val postRepository: PostRepository,
) {


    @AfterEach
    fun clean() {
        postRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @Test
    fun `사용자는 게시물을 작성할 수 있다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )

        val postCreateDto = random<PostCreateDto>().copy(
            writerId = savedUserEntity.id
        )

        // when, then
        mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postCreateDto)))
            .andExpect(status().isCreated)
    }

}