package com.example.kotlintestcodewitharchitecture.post.controller

import com.example.kotlintestcodewitharchitecture.post.controller.port.PostService
import com.example.kotlintestcodewitharchitecture.post.controller.response.PostResponse
import com.example.kotlintestcodewitharchitecture.post.domain.PostUpdate
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
    fun getById(@PathVariable id: Long): ResponseEntity<PostResponse> {
        return ResponseEntity
            .ok()
            .body(PostResponse.of(postService.getPostById(id)))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody postUpdate: PostUpdate): ResponseEntity<PostResponse> {
        return ResponseEntity
            .ok()
            .body(PostResponse.of(postService.update(id, postUpdate)))
    }

}