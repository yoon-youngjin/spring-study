package dev.yoon.jpa.dao;

import dev.yoon.jpa.dto.PostDto;
import dev.yoon.jpa.entity.PostEntity;
import dev.yoon.jpa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.Iterator;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostDao {

    private final PostRepository postRepository;

    public void createPost(PostDto postDto) {
        PostEntity postEntity = PostEntity.createPostEntity(postDto);

        this.postRepository.save(postEntity);
    }

    public PostEntity readPost(int id) {
        Optional<PostEntity> postEntity = postRepository.findById((long) id);

        if (postEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return postEntity.get();
    }

    public Iterator<PostEntity> readPostAll() {
        return postRepository.findAll().iterator();

    }

    public void updatePost(int id, PostDto postDto) {
        Optional<PostEntity> OpPostEntity = this.postRepository.findById((long) id);

        if (OpPostEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        PostEntity postEntity = OpPostEntity.get();

        postEntity.setTitle(postDto.getTitle() == null ? postEntity.getTitle() : postDto.getTitle());
        postEntity.setContent(postDto.getContent() == null ? postEntity.getContent() : postDto.getContent());
        postEntity.setWriter(postDto.getWriter() == null ? postEntity.getWriter() : postDto.getWriter());

        this.postRepository.save(postEntity);

    }

    public void deletePost(int id) {
//        this.postRepository.deleteById((long) id);

        Optional<PostEntity> targetEntity = this.postRepository.findById((long) id);
        if(targetEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        this.postRepository.delete(targetEntity.get());

    }


}
