package dev.yoon.jpa.service;

import dev.yoon.jpa.dao.PostDao;
import dev.yoon.jpa.dto.PostDto;
import dev.yoon.jpa.entity.PostEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;

    public void createPost(PostDto postDto) {
        this.postDao.createPost(postDto);


    }

    public PostDto readPost(int id) {
        PostEntity postEntity = this.postDao.readPost(id);
        return PostDto.createPostDto(postEntity);
    }

    public List<PostDto> readPostAll() {
        Iterator<PostEntity> it = this.postDao.readPostAll();
        List<PostDto> postDtoList = new ArrayList<>();

        while (it.hasNext()) {
            PostEntity postEntity = it.next();
            postDtoList.add(PostDto.createPostDto(postEntity));
        }

        return postDtoList;
    }

    public void updatePost(int id, PostDto postDto) {
        this.postDao.updatePost(id, postDto);

    }

    public void deletePost(int id) {
        this.postDao.deletePost(id);

    }

}
