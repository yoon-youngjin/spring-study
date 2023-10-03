package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.dto.UserCreateDto
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.repository.UserRepository
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
    private val userRepository: UserRepository,
) {

    @MockBean
    lateinit var mailSender: JavaMailSender

    @AfterEach
    fun clean() {
        userRepository.deleteAllInBatch()
    }

    @Test
    fun `유저를 생성할 수 있다`() {
        // given
        val userCreateDto = random<UserCreateDto>()
        BDDMockito.doNothing().`when`(mailSender).send(any(SimpleMailMessage::class.java))

        // when, then
        mockMvc.perform(post("/api/users")
            .content(objectMapper.writeValueAsString(userCreateDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated)
    }

}
