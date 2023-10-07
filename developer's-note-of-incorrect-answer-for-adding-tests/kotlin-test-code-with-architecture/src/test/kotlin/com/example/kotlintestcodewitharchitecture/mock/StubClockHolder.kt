package com.example.kotlintestcodewitharchitecture.mock

import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder

class StubClockHolder(
    private val millis: Long
) : ClockHolder {
    override fun millis(): Long {
        return millis
    }
}