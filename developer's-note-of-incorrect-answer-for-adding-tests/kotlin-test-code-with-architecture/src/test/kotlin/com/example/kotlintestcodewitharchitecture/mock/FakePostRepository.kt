package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.service.port.PostRepository

class FakePostRepository : PostRepository {
    private val posts: MutableMap<Long, Post> = mutableMapOf()

    override fun findByIdOrNull(id: Long): Post? {
        return posts[id]
    }

    override fun save(post: Post): Post {
        val entityForSave = if (post.id == 0L) {
            val newId = posts.nextId()
            post.copy(id = newId)
        } else {
            post
        }
        posts[entityForSave.id] = entityForSave
        return entityForSave
    }
}