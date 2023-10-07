package com.example.kotlintestcodewitharchitecture.post.controller

import com.example.kotlintestcodewitharchitecture.post.controller.port.PostService
import com.example.kotlintestcodewitharchitecture.post.controller.response.PostResponse
import com.example.kotlintestcodewitharchitecture.post.domain.PostCreate
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
    fun create(@RequestBody postCreate: PostCreate): ResponseEntity<PostResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(PostResponse.of(postService.create(postCreate)))
    }
}
