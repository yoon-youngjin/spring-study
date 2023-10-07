package com.example.kotlintestcodewitharchitecture.common.infrastructure

import com.example.kotlintestcodewitharchitecture.common.service.port.UuidHolder
import java.util.UUID
import org.springframework.stereotype.Component

@Component
class SystemUuidHolder : UuidHolder {
    override fun random(): String {
        return UUID.randomUUID().toString()
    }
}