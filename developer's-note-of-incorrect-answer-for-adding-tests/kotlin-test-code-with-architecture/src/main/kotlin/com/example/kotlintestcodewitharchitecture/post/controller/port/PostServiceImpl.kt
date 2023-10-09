package com.example.kotlintestcodewitharchitecture.post.controller.port

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.post.controller.port.PostService
import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val clockHolder: ClockHolder,
) : PostService {
    override fun getPostById(id: Long): Post {
        return postRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Posts", id)
    }

    @Transactional
    override fun create(postCreate: PostCreate): Post {
        val user = userRepository.getById(postCreate.writerId)
        val savedPost = Post.from(user, postCreate, clockHolder)
        return postRepository.save(savedPost)
    }

    @Transactional
    override fun update(id: Long, postUpdate: PostUpdate): Post {
        val post = getPostById(id)
        val updatedPost = post.update(postUpdate, clockHolder)
        return postRepository.save(updatedPost)
    }
}
