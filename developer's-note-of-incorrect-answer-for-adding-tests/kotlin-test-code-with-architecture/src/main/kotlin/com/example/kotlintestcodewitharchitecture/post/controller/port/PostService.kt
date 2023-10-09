package com.example.kotlintestcodewitharchitecture.post.controller.port

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PostService {
    fun getPostById(id: Long): Post
    fun update(id: Long, postUpdate: PostUpdate): Post
    fun create(postCreate: PostCreate): Post
}
