package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder

class StubClockHolder : ClockHolder {
    private var millis: Long? = null

    fun setUp(millis: Long) {
        this.millis = millis
    }

    override fun millis(): Long {
        return millis ?: TODO("Not yet implemented")
    }
}