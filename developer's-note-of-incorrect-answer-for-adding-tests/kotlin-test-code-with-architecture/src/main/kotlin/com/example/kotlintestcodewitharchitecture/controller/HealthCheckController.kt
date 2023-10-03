package com.example.kotlintestcodewitharchitecture.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {

    @GetMapping("/health")
    fun health(): ResponseEntity<Void> {
        return ResponseEntity
            .ok()
            .build()
    }
}