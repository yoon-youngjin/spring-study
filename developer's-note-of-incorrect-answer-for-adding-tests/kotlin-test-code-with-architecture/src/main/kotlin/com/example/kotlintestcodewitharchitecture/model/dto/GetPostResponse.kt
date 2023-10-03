package com.example.kotlintestcodewitharchitecture.model.dto

import com.example.kotlintestcodewitharchitecture.repository.PostEntity

data class GetPostResponse(
    val id: Long,
    val content: String,
    val createdAt: Long,
    val modifiedAt: Long? = null,
    val writer: GetUserResponse,
) {

    companion object {
        fun of(postEntity: PostEntity) : GetPostResponse {
            return GetPostResponse(
                id = postEntity.id,
                content = postEntity.content,
                createdAt = postEntity.createdAt,
                modifiedAt = postEntity.modifiedAt,
                writer = GetUserResponse.of(postEntity.writer)
            )
        }
    }
}