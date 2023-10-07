package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.common.domain.exception.ResourceNotFoundException
import com.example.kotlintestcodewitharchitecture.user.domain.User
import com.example.kotlintestcodewitharchitecture.user.domain.UserStatus
import com.example.kotlintestcodewitharchitecture.user.service.port.UserRepository

class FakeUserRepository : UserRepository {
    private val users: MutableMap<Long, User> = mutableMapOf()

    override fun findByIdAndStatus(id: Long, userStatus: UserStatus): User? {
        return users.values
            .firstOrNull { it.id == id && it.status == userStatus }
    }

    override fun findByEmailAndStatus(email: String, userStatus: UserStatus): User? {
        return users.values
            .firstOrNull { it.email == email && it.status == userStatus }
    }

    override fun save(user: User): User {
        val entityForSave = if (user.id == 0L) {
            val newId = users.nextId()
            user.copy(id = newId)
        } else {
            user
        }
        users[entityForSave.id] = entityForSave
        return entityForSave
    }

    override fun getById(id: Long): User {
        return users[id]
            ?: throw ResourceNotFoundException("Users", id)
    }

    override fun findByIdOrNull(id: Long): User? {
        return users[id]
    }
}