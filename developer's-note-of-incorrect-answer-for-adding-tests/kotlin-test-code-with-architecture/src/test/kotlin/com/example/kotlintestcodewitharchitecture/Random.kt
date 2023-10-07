package com.example.kotlintestcodewitharchitecture

import com.example.kotlintestcodewitharchitecture.post.domain.Post
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
import com.example.kotlintestcodewitharchitecture.user.domain.UserCreate
import com.example.kotlintestcodewitharchitecture.user.domain.UserUpdate
import com.example.kotlintestcodewitharchitecture.post.infrastructure.PostEntity
import com.example.kotlintestcodewitharchitecture.user.controller.request.UserUpdateRequest
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.infrastructure.UserEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import kotlin.random.Random
import kotlin.reflect.KClass
import io.kotest.property.RandomSource

inline fun <reified T : Any> random(seed: Int? = null): T = random(T::class, seed)

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
fun <T : Any> random(kClass: KClass<T>, seed: Int?): T {
    val random = Random(seed)
    val randomSource = RandomSource(seed)

    return when (kClass) {
        User::class -> User(
            id = random(seed),
            email = random(seed),
            nickname = random(seed),
            address = random(seed),
            certificationCode = random(seed),
            status = random(seed),
            lastLoginAt = random(seed),
        )

        Post::class -> Post(
            id = random(seed),
            content = random(seed),
            createdAt = random(seed),
            modifiedAt = random(seed),
            writer = random(seed),
        )

        UserEntity::class -> UserEntity(
            email = random(seed),
            nickname = random(seed),
            address = random(seed),
            certificationCode = random(seed),
            status = random(seed),
            lastLoginAt = random(seed),
        )

        PostEntity::class -> PostEntity(
            content = random(seed),
            createdAt = random(seed),
            modifiedAt = random(seed),
            writer = random(seed)
        )

        UserCreate::class -> UserCreate(
            email = random(seed),
            nickname = random(seed),
            address = random(seed),
        )

        UserUpdate::class -> UserUpdate(
            nickname = random(seed),
            address = random(seed),
        )

        UserUpdateRequest::class -> UserUpdateRequest(
            nickname = random(seed),
            address = random(seed),
        )

        PostCreate::class -> PostCreate(
            writerId = random(seed),
            content = random(seed),
        )

        PostUpdate::class -> PostUpdate(
            content = random(seed),
        )

        // Kotlin types
        String::class -> Arb.string().next(randomSource)
        Long::class -> Arb.long().next(randomSource)
        Int::class -> Arb.int().next(randomSource)
        Float::class -> Arb.float().next(randomSource)
        Double::class -> Arb.double().next(randomSource)

        else -> kClass.java.enumConstants.run {
            if (this != null) {
                this.random(random)
            } else {
                throw UnsupportedOperationException("$kClass is not supported")
            }
        }
    } as T

}

fun Random(seed: Int?) = seed?.let { Random(seed) } ?: Random.Default

fun RandomSource(seed: Int?) = seed?.let { RandomSource.seeded(it.toLong()) } ?: RandomSource.default()
