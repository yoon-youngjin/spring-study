package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserJpaRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.ArgumentMatchers.any
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserCreateControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper,
    @Autowired
    private val userJpaRepository: UserJpaRepository,
) {

    @MockBean
    lateinit var mailSender: JavaMailSender

    @AfterEach
    fun clean() {
        userJpaRepository.deleteAllInBatch()
    }

    @Test
    fun `유저를 생성할 수 있다`() {
        // given
        val userCreate = random<UserCreate>()
        BDDMockito.doNothing().`when`(mailSender).send(any(SimpleMailMessage::class.java))

        // when, then
        mockMvc.perform(post("/api/users")
            .content(objectMapper.writeValueAsString(userCreate))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
    }

}
