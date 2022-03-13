package dev.yoon.cruddemo.post;

import java.util.List;

public interface PostRepository {
    boolean save(PostDto postDto);

    List<PostDto> findAll();

    PostDto findById(int id);

    boolean update(int id, PostDto postDto);

    boolean delete(int id);


}
