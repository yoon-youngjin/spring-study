package com.example.kotlintestcodewitharchitecture.post.service.port

import com.example.kotlintestcodewitharchitecture.post.domain.Post

interface PostRepository {
    fun findByIdOrNull(id: Long): Post?
    fun save(post: Post): Post
}