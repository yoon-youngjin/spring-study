package com.example.kotlintestcodewitharchitecture.common.infrastructure

import com.example.kotlintestcodewitharchitecture.common.service.port.ClockHolder
import java.time.Clock
import org.springframework.stereotype.Component

@Component
class SystemClockHolder : ClockHolder {
    override fun millis(): Long {
        return Clock.systemUTC().millis()
    }
}