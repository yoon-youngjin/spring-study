package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.UserStatus
import com.example.kotlintestcodewitharchitecture.model.dto.UserUpdateDto
import com.example.kotlintestcodewitharchitecture.random
import com.example.kotlintestcodewitharchitecture.repository.PostRepository
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
import com.example.kotlintestcodewitharchitecture.repository.UserRepository
import com.example.kotlintestcodewitharchitecture.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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
class UserControllerTest(
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
    fun `특정 유저의 정보를 전달 받을 수 있다 - 개인정보는 소거되어야 한다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )

        // when, then
        mockMvc.perform(get("/api/users/${savedUserEntity.id}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedUserEntity.id))
            .andExpect(jsonPath("$.email").value(savedUserEntity.email))
            .andExpect(jsonPath("$.address").doesNotExist())
            .andExpect(jsonPath("$.nickname").value(savedUserEntity.nickname))
            .andExpect(jsonPath("$.status").value(savedUserEntity.status.toString()))
    }

    @Test
    fun `사용자는 존재하지 않는 유저의 아이디로 api 호출할 경우 404 응답을 받는다`() {
        // when, then
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Users 에서 ID 1 를 찾을 수 없습니다."))
    }

    @Test
    fun `사용자는 인증 코드로 계정을 활성화 시킬 수 있다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.PENDING
            )
        )
        // when, then
        mockMvc.perform(get("/api/users/${savedUserEntity.id}/verify")
            .queryParam("certificationCode", savedUserEntity.certificationCode))
            .andExpect(status().isFound())

        userRepository.findByIdOrNull(savedUserEntity.id)!!.status shouldBe UserStatus.ACTIVE
    }

    @Test
    fun `사용자는 인증 코드가 일치하지 않을 경우 권한 없음 에러가 발생한다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.PENDING
            )
        )
        // when, then
        mockMvc.perform(get("/api/users/${savedUserEntity.id}/verify")
            .queryParam("certificationCode", "${savedUserEntity.certificationCode}-xxxx"))
            .andExpect(status().isForbidden())
    }


    @Test
    fun `사용자는 내 정보를 불러올 때 개인정보인 주소도 갖고 올 수 있다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )
        // when, then
        mockMvc.perform(get("/api/users/me")
            .header("EMAIL", savedUserEntity.email))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedUserEntity.id))
            .andExpect(jsonPath("$.email").value(savedUserEntity.email))
            .andExpect(jsonPath("$.nickname").value(savedUserEntity.nickname))
            .andExpect(jsonPath("$.address").value(savedUserEntity.address))
    }

    @Test
    fun `사용자는 내 정보를 수정할 수 있다`() {
        // given
        val savedUserEntity = userRepository.save(
            random<UserEntity>().copy(
                status = UserStatus.ACTIVE
            )
        )
        val userUpdateDto = random<UserUpdateDto>()

        // when, then
        mockMvc.perform(put("/api/users/me")
            .header("EMAIL", savedUserEntity.email)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(userUpdateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value(userUpdateDto.nickname))
            .andExpect(jsonPath("$.address").value(userUpdateDto.address))
    }
}
