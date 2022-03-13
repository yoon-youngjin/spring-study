package dev.yoon.cruddemo.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("post")
// http://localhost:8080/post
public class PostRestController {
    private static final Logger logger = LoggerFactory.getLogger(PostRestController.class);
    private final PostService postService;

    public PostRestController(
            @Autowired PostService postService
    ) {
        this.postService = postService;
    }

    // method : create에 대해서 권장되는 http method => PostMapping
    @PostMapping()
    // 201
    @ResponseStatus(HttpStatus.CREATED)
    // path : http://localhost:8080/post
    // HttpServletRequest : 실제 자바 상에서 구현된 http인터페이스
    public void createPost(@RequestBody PostDto postDto, HttpServletRequest request) {
        logger.info(postDto.toString());
        logger.info(request.getHeader("Content-Type"));
        this.postService.createPost(postDto);
    }

    // method : read에 대해서 권장되는 http method => GetMapping
    @GetMapping()
    // ResponseStatus가 없으면 default : 200
    // path : http://localhost:8080/post
    public List<PostDto> readPostAll() {
        logger.info("in read post all");
        return this.postService.readPostAll();
    }


    // method : read에 대해서 권장되는 http method => GetMapping
    @GetMapping("{id}")
    // path : http://localhost:8080/post/{id}
    // GET /post/0/ : 경로를 사용하는 경우 : post라는 자원중에서 특정한 자원을 명백히 선택하는 경우
    // GET /post?id=0 : RequestParam(Queryparam)를 사용하는 경우 => post라는 자원중에서 질문을 날리는 경우
    public PostDto readPostOne(@PathVariable("id") int id) {
        logger.info("in read post one");
        return this.postService.readPost(id);
    }

    // postmapping은 없던 자원을 새로 만들어서 넣는 것
    // putmapping은 기존의 자원을 변경하여 다시 넣는 것
    // method : update에 대해서 권장되는 http method => PutMapping
    // path : http://localhost:8080/post/{id}
    // PUT /post/{id}
    // 204
    // 현재 body가 없음(= return값이 void) : body를 기다리는 상태가 될수 있으므로 : HttpStatus.NO_CONTENT, HttepStatus.ACCEPTED
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("{id}")
    public void updatePost(@PathVariable("id") int id, @RequestBody PostDto postDto) {
        logger.info("target id: " + id);
        logger.info("update content: " + postDto);
        this.postService.updatePost(id, postDto);
    }

    // method : update에 대해서 권장되는 http method => DeleteMapping
    // path : http://localhost:8080/post/{id}
    // DELETE /post/{id}
    // 202
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("{id}")
    public void deletePost(@PathVariable("id") int id) {
        this.postService.deletePost(id);
        logger.info(this.postService.readPostAll().toString());
    }

    //TODO 표준 restful한 API
    //TODO POST /post : create
    //TODO GET /post/0 : read-one
    //TODO GET /post : read-all
    //TODO PUT /post/0 : update
    //TODO DELETE /post/0 : delete

}
