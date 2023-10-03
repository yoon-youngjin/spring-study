package com.example.kotlintestcodewitharchitecture.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class HealthCheckControllerTest(
    @Autowired
    private val mockMvc: MockMvc,
){
    @Test
    fun `헬스 체크 응답이 200으로 내려온다`(){
        mockMvc
            .perform(get("/health"))
            .andExpect(status().isOk())
    }
}
