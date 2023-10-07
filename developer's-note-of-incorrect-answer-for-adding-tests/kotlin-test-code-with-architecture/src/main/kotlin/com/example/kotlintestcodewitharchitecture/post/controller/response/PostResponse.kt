package com.example.kotlintestcodewitharchitecture.post.controller.response

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.user.controller.response.UserResponse

data class PostResponse(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val modifiedAt: Long? = null,
    val writer: UserResponse,
) {

    companion object {
        fun of(post: Post) : PostResponse {
            return PostResponse(
                id = post.id,
                content = post.content,
                createdAt = post.createdAt,
                modifiedAt = post.modifiedAt,
                writer = UserResponse.of(post.writer)
            )
        }
    }
}