package dev.yoon.jpa.controller;

import com.google.gson.Gson;
import dev.yoon.jpa.aspect.LogArguments;
import dev.yoon.jpa.aspect.LogExecutionTime;
import dev.yoon.jpa.aspect.LogReturn;
import dev.yoon.jpa.dao.PostDao;
import dev.yoon.jpa.dto.PostDto;
import dev.yoon.jpa.dto.ValidTestDto;
import dev.yoon.jpa.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("post")
public class PostController {

    private final PostService postService;

    public PostController(
            @Autowired PostService postService,
            @Autowired Gson gson
    ) {
        this.postService = postService;
        log.info(gson.toString());
    }

    @LogArguments
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody @Valid PostDto postDto) {
        this.postService.createPost(postDto);


    }

    @LogReturn
    @GetMapping("{id}")
    public PostDto readPost(@PathVariable("id") int id) {
        PostDto dto = this.postService.readPost(id);
        return dto;
    }

    @LogExecutionTime
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

    @GetMapping("test-log")
    public void testLog() {
        // trace는 개발단계에서만 사용하고 상용단계에서는 지워주는것이 좋다
        // debug레벨 로그까지 작성해줘도 좋다
        // info warn error는 출력이 되고 trace와 debug는 출력이 안되는 이유는 출력 레벨 기본값이 info이므로
        // => yml에서 변경가능
        log.trace("TRACE Log Message");
        log.debug("DEBUG Log Message");
        log.info("INFO Log Message");
        log.warn("WARN Log Message");
        log.error("ERROR Log Message");
    }

    @PostMapping("test-valid")
    public void testValid(
            @RequestBody @Valid ValidTestDto dto
    ) {
        log.warn(dto.toString());
    }
}
