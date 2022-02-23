package dev.yoon.cruddemo.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Deprecated
// 더 이상 사용하지 않을 클래스임을 명시 => 당장은 사용하지만 미래는 사용하지 않음을 알려준다.
@Controller
@ResponseBody
/**
 * Controller와 responsebody를 붙이는 것은 restcontroller가 되는 것과 동일
 * => 클래스내의 모든 함수들이 responsebody가 붙은 형태로 함수 선언이 된다.
 */
//@RequestMapping("post")
// localhost:8080/post
public class PostController {
    private static final Logger logger =
            LoggerFactory.getLogger(PostController.class);

    private final PostService postService;

    public PostController(
            @Autowired PostService postService
    ) {
        this.postService = postService;
    }

    @PostMapping("create")
    // localhost:8080/post
    public void createPost(@RequestBody PostDto postDto) {
        logger.info(postDto.toString());
        this.postService.createPost(postDto);
    }

    @GetMapping("read-all")
    public List<PostDto> readPostAll() {
        logger.info("in read all");
        return this.postService.readPostAll();
    }

    @GetMapping("read-one")
    public PostDto readPostOne(@RequestParam("id") int id) {
        logger.info("in read one");
        return this.postService.readPost(id);
    }

    @PostMapping("update")
    public void updatePost(
            @RequestParam("id") int id,
            @RequestBody PostDto postDto
    ) {
        logger.info("target id: " + id);
        logger.info("update content: " + postDto);
        this.postService.updatePost(id, postDto);
    }

    @DeleteMapping("delete")
    public void delete(
            @RequestParam("id") int id
    ) {
        logger.info(this.postService.readPostAll().toString());
        this.postService.deletePost(id);
    }

}
