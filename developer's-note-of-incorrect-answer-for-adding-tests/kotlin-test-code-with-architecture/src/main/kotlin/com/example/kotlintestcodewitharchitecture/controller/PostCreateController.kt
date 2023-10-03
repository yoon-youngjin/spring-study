package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.dto.GetPostResponse
import com.example.kotlintestcodewitharchitecture.model.dto.PostCreateDto
import com.example.kotlintestcodewitharchitecture.service.PostService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostCreateController(
    private val postService: PostService,
) {
    @PostMapping
    fun createPost(@RequestBody postCreateDto: PostCreateDto): ResponseEntity<GetPostResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(GetPostResponse.of(postService.create(postCreateDto)))
    }
}
