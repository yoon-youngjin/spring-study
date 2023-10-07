package com.example.kotlintestcodewitharchitecture.post.infrastructure

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PostRepositoryImpl(
    private val postJpaRepository: PostJpaRepository,
) : PostRepository {
    override fun findByIdOrNull(id: Long): Post? {
        return postJpaRepository.findByIdOrNull(id)?.toModel()
    }

    override fun save(post: Post): Post {
        return postJpaRepository.save(PostEntity.from(post)).toModel()
    }
}