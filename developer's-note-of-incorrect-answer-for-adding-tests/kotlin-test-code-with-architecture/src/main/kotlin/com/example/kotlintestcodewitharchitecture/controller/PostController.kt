package com.example.kotlintestcodewitharchitecture.controller

import com.example.kotlintestcodewitharchitecture.model.dto.GetPostResponse
import com.example.kotlintestcodewitharchitecture.model.dto.PostUpdateDto
import com.example.kotlintestcodewitharchitecture.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService,
) {
    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: Long): ResponseEntity<GetPostResponse> {
        return ResponseEntity
            .ok()
            .body(GetPostResponse.of(postService.getPostById(id)))
    }

    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: Long, @RequestBody postUpdateDto: PostUpdateDto): ResponseEntity<GetPostResponse> {
        return ResponseEntity
            .ok()
            .body(GetPostResponse.of(postService.update(id, postUpdateDto)))
    }

}