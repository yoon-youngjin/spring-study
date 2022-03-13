package dev.yoon.cruddemo.post;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 목적
 * controller(endpoint)와 model(database) 비즈니스로직을 분리하는 것
 * 데이터 회수는 repo, 회수된 데이터를 검증하는 과정 service
 */
public interface PostService {
    void createPost(PostDto postDto);

    List<PostDto> readPostAll();

    PostDto readPost(int id);

    void updatePost(int id, PostDto postDto);

    void deletePost(int id);


}
