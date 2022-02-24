package dev.yoon.jpa.controller;

import dev.yoon.jpa.dao.PostDao;
import dev.yoon.jpa.dto.PostDto;
import dev.yoon.jpa.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("post")
public class PostController {

    private final PostService postService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostDto postDto) {

        this.postService.createPost(postDto);

    }

    @GetMapping("{id}")
    public PostDto readPost(@PathVariable("id") int id) {
        PostDto dto = this.postService.readPost(id);
        return dto;

    }

    @GetMapping("")
    public List<PostDto> readPostAll() {
        List<PostDto> postDtoList = postService.readPostAll();

        return postDtoList;
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updatePost(
            @PathVariable("id") int id,
            @RequestBody PostDto dto) {

        postService.updatePost(id, dto);

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deletePost(
            @PathVariable("id") int id) {
        postService.deletePost(id);

    }
}
