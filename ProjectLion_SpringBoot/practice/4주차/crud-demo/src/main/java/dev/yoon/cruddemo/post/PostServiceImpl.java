package dev.yoon.cruddemo.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostRepository postRepository;


    // @Autowired
    // Spring IOC에서 해주는 부분
    // => interface를 요구해도 인터페이스가 구현된 구현체중에서 정의된 우선순위로 구현체를 반환해준다.
    // => 현재는 inMemory class만 있으므로 postRepoinmemory가 반환
    public PostServiceImpl(
            @Autowired PostRepository postRepository
    ) {
        this.postRepository = postRepository;
    }

    @Override
    public void createPost(PostDto postDto) {
        // TODO
        if (!this.postRepository.save(postDto)) {
            throw new RuntimeException("save failed");
        }

    }

    @Override
    public List<PostDto> readPostAll() {
        return this.postRepository.findAll();
    }

    @Override
    public PostDto readPost(int id) {
        return this.postRepository.findById(id);
    }

    @Override
    public void updatePost(int id, PostDto postDto) {
        this.postRepository.update(id, postDto);
    }

    @Override
    public void deletePost(int id) {
        this.postRepository.delete(id);
    }
}
