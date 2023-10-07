package com.example.kotlintestcodewitharchitecture.post.infrastructure

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "posts")
data class PostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "content", nullable = false)
    val content: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Long,

    @Column(name = "modified_at")
    val modifiedAt: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val writer: UserEntity,
) {
    fun toModel(): Post {
        return Post(
            id = id,
            content = content,
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            writer = writer.toModel(),
        )
    }

    companion object {
        fun from(post: Post): PostEntity {
            return PostEntity(
                id = post.id,
                content = post.content,
                createdAt = post.createdAt,
                modifiedAt = post.modifiedAt,
                writer = UserEntity.from(post.writer),
            )
        }
    }
}
