package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.common.service.port.UuidHolder

class StubUuidHolder(
    private val uuid: String,
): UuidHolder {
    override fun random(): String {
        return uuid
    }
}