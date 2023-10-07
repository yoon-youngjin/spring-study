package com.example.kotlintestcodewitharchitecture.post.domain

import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import com.example.kotlintestcodewitharchitecture.user.domain.User
import java.time.Clock

data class Post(
    val id: Long = 0,
    val content: String,
    val createdAt: Long,
    val modifiedAt: Long? = null,
    val writer: User,
) {
    fun update(postUpdate: PostUpdate, clockHolder: ClockHolder): Post {
        return this.copy(
            content = postUpdate.content,
            modifiedAt = clockHolder.millis()
        )
    }

    companion object {
        fun from(user: User, postCreate: PostCreate, clockHolder: ClockHolder): Post {
            return Post(
                content = postCreate.content,
                writer = user,
                createdAt = clockHolder.millis(),
            )
        }
    }
}