package dev.yoon.refactoring_board.controller;

import dev.yoon.refactoring_board.dto.PostDto;
import dev.yoon.refactoring_board.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("board/{boardId}/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PostDto.Res> createPost(
            @PathVariable("boardId") Long id,
            @RequestBody @Valid PostDto.Req postDto
    ) {
        return ResponseEntity.ok(this.postService.createPost(id, postDto));

    }

    @GetMapping()
    public ResponseEntity<List<PostDto.Res>> readPostAll(
            @PathVariable("boardId") Long boardId
    ) {
        List<PostDto.Res> postDtoList = this.postService.readPostAllbyBoardId(boardId);

        return ResponseEntity.ok(postDtoList);

    }

    @GetMapping("{postId}")
    public ResponseEntity<PostDto.Res> readPostOne(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId) {

        PostDto.Res postDto = this.postService.readPostOneByBoardId(boardId, postId);

        return ResponseEntity.ok(postDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @RequestBody PostDto.Req postDto) {
        postService.updatePost(boardId, postId, postDto);

        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable("boardId") Long boardId,
            @PathVariable("postId") Long postId,
            @RequestBody PostDto.Req postDto) {
        this.postService.deletePost(boardId, postId, postDto);
        return ResponseEntity.noContent().build();

    }
}
