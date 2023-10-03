package com.example.kotlintestcodewitharchitecture.service

import com.example.kotlintestcodewitharchitecture.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.model.dto.PostCreateDto
import com.example.kotlintestcodewitharchitecture.model.dto.PostUpdateDto
import com.example.kotlintestcodewitharchitecture.repository.PostEntity
import com.example.kotlintestcodewitharchitecture.repository.PostRepository
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
import java.time.Clock
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val userService: UserService,
) {
    fun getPostById(id: Long): PostEntity {
        return postRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Posts", id)
    }

    @Transactional
    fun create(postCreateDto: PostCreateDto): PostEntity {
        val userEntity = userService.getById(postCreateDto.writerId)
        return postRepository.save(
            PostEntity(
                content = postCreateDto.content,
                writer = userEntity,
                createdAt = Clock.systemUTC().millis(),
            )
        )
    }

    @Transactional
    fun update(id: Long, postUpdateDto: PostUpdateDto): PostEntity {
        val postEntity = getPostById(id)
        return postRepository.save(
            postEntity.copy(
                content = postUpdateDto.content,
                modifiedAt = Clock.systemUTC().millis(),
            )
        )
    }
}
