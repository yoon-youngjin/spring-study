package com.example.kotlintestcodewitharchitecture.post.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository : JpaRepository<PostEntity, Long>