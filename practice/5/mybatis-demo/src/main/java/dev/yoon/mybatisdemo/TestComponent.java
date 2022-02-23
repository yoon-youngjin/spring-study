package dev.yoon.mybatisdemo;

import dev.yoon.mybatisdemo.dao.PostDao;
import dev.yoon.mybatisdemo.dto.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestComponent {
    public final PostDao postDao;
    public TestComponent(
            @Autowired PostDao postDao
    ) {
        this.postDao = postDao;
        PostDto newPost = new PostDto();
        newPost.setTitle("From Mybatis");
        newPost.setContent("Hello Database!");
        newPost.setWriter("yoon");
        newPost.setBoard(1);

        this.postDao.createPost(newPost);

//        List<PostDto> postDtoList = this.postDao.readPostAll();
//        System.out.println(postDtoList.get(postDtoList.size() - 1));

//        PostDto postDto = postDtoList.get(0);
//        postDto.setContent("Update From Mybatis");
//        postDao.updatePost(postDto);
//
//        System.out.println(this.postDao.readPost(postDto.getId()));


    }
}
