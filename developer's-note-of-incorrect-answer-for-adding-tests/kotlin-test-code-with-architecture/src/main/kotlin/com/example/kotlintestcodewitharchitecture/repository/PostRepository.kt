package com.example.kotlintestcodewitharchitecture.repository

import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<PostEntity, Long>