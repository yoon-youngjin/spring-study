package com.example.kotlintestcodewitharchitecture.post.controller.port

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate

interface PostService {
    fun getPostById(id: Long): Post
    fun update(id: Long, postUpdate: PostUpdate): Post
    fun create(postCreate: PostCreate): Post
}