package com.example.kotlintestcodewitharchitecture.mock

fun <T> MutableMap<Long, T>.nextId() = keys.maxOfOrNull { it }?.plus(1L) ?: 1L
