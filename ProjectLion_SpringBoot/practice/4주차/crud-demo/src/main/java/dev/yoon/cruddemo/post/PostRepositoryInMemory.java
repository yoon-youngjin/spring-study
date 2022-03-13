package dev.yoon.cruddemo.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepositoryInMemory implements PostRepository {
    private static final Logger logger = LoggerFactory.getLogger(PostRepositoryInMemory.class);
    private final List<PostDto> postDtoList;

    public PostRepositoryInMemory() {
        this.postDtoList = new ArrayList<>();
    }

    @Override
    public boolean save(PostDto postDto) {
        return this.postDtoList.add(postDto);
    }

    @Override
    public List<PostDto> findAll() {
        return this.postDtoList;
    }

    @Override
    public PostDto findById(int id) {
        return this.postDtoList.get(id);
    }

    @Override
    public boolean update(int id, PostDto postDto) {
        PostDto targetDto = this.postDtoList.get(id);

        if (postDto.getContent() != null) {
            targetDto.setContent(postDto.getContent());
        }
        if (postDto.getTitle() != null) {
            targetDto.setTitle(postDto.getTitle());
        }
        this.postDtoList.set(id, targetDto);
        return true;
    }

    @Override
    public boolean delete(int id) {
        this.postDtoList.remove(id);
        return true;
    }
}
