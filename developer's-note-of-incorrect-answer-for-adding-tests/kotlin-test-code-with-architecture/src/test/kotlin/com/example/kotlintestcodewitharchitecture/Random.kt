package com.example.kotlintestcodewitharchitecture

import com.example.kotlintestcodewitharchitecture.model.dto.PostCreateDto
import com.example.kotlintestcodewitharchitecture.model.dto.PostUpdateDto
import com.example.kotlintestcodewitharchitecture.model.dto.UserCreateDto
import com.example.kotlintestcodewitharchitecture.model.dto.UserUpdateDto
import com.example.kotlintestcodewitharchitecture.repository.PostEntity
import com.example.kotlintestcodewitharchitecture.repository.UserEntity
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

        UserCreateDto::class -> UserCreateDto(
            email = random(seed),
            nickname = random(seed),
            address = random(seed),
        )

        UserUpdateDto::class -> UserUpdateDto(
            nickname = random(seed),
            address = random(seed),
        )

        PostCreateDto::class -> PostCreateDto(
            writerId = random(seed),
            content = random(seed),
        )

        PostUpdateDto::class -> PostUpdateDto(
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
