package com.example.kotlintestcodewitharchitecture.medium

import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserJpaRepository
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
class UserJpaRepositoryTest @Autowired constructor(
    private var userJpaRepository: UserJpaRepository,
) {
    @Test
    fun `UserRepository가 제대로 연결되었다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )

        // when
        val result = userJpaRepository.save(userEntity)

        // then
        result.id.shouldNotBeNull()
    }

    @Test
    fun `findByIdAndStatus로 유저 데이터를 찾아올 수 있다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        val result = userJpaRepository.findByIdAndStatus(
            id = savedUserEntity.id,
            userStatus = UserStatus.ACTIVE
        )

        // then
        result.shouldNotBeNull()
    }

    @Test
    fun `findByIdAndStatus는 데이터가 없으면 null을 반환한다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.INACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        val result = userJpaRepository.findByIdAndStatus(
            id = savedUserEntity.id,
            userStatus = UserStatus.ACTIVE
        )

        // then
        result.shouldBeNull()
    }

    @Test
    fun `findByEmailAndStatus로 유저 데이터를 찾아올 수 있다`() {
        // given
        val userEntity = UserEntity(
            email = "dudwls143@gmail.com",
            address = "seoul",
            nickname = "yoon",
            status = UserStatus.ACTIVE,
            certificationCode = "aaaaaa-aaaa-aaa-aaa"
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        // when
        val result = userJpaRepository.findByEmailAndStatus(
            email = savedUserEntity.email,
            userStatus = UserStatus.ACTIVE
        )

        // then
        result.shouldNotBeNull()
    }


}