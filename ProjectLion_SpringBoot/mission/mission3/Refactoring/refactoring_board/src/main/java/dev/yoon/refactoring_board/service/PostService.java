package dev.yoon.refactoring_board.service;

import dev.yoon.refactoring_board.domain.Board;
import dev.yoon.refactoring_board.domain.Post;
import dev.yoon.refactoring_board.dto.PostDto;
import dev.yoon.refactoring_board.exception.BoardNotFoundException;
import dev.yoon.refactoring_board.exception.PostNotFoundException;
import dev.yoon.refactoring_board.repository.BoardRepository;
import dev.yoon.refactoring_board.repository.PostRepository;
import dev.yoon.refactoring_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public PostDto.Res createPost(Long id, PostDto.Req postDto) {
//        Optional<User> optionalUser = this.userRepository.findById(postDto.getUserId());
//        optionalUser.orElseThrow(() -> new UserNotFoundException(id));

        Optional<Board> optionalBoard = this.boardRepository.findById(id);
        optionalBoard.orElseThrow(() -> new BoardNotFoundException(id));

        Post post = postDto.toEntity();
        Board board = optionalBoard.get();
        board.addPost(post);

//        User user = optionalUser.get();
//        user.addPost(post);
        return new PostDto.Res(this.postRepository.save(post));

    }

    public List<PostDto.Res> readPostAllbyBoardId(Long boardId) {
        Board boardById = findBoardById(boardId);
        List<Post> postList = this.postRepository.findPostAllbyBoardId(boardById.getId());

        List<PostDto.Res> postDtos = new ArrayList<>();
        for (Post post : postList) {
            PostDto.Res postDto = new PostDto.Res(post);
            postDtos.add(postDto);
        }
        return postDtos;
    }

    public PostDto.Res readPostOneByBoardId(Long boardId, Long postId) {
        Board board = findBoardById(boardId);

        Optional<Post> optionalPost = this.postRepository.findPostOnebyBoardId(board.getId(), postId);
        optionalPost.orElseThrow(()-> new PostNotFoundException(postId));

        return new PostDto.Res(optionalPost.get());
    }


    public boolean updatePost(Long boardId, Long postId, PostDto.Req postDto) {

        Board board = findBoardById(boardId);
        Optional<Post> optionalPost = this.postRepository.findPostOnebyBoardId(board.getId(), postId);
        optionalPost.orElseThrow(()-> new PostNotFoundException(postId));

        optionalPost.get().update(postDto);
        return true;

    }

    public boolean deletePost(Long boardId, Long postId, PostDto.Req postDto) {
        Board board = findBoardById(boardId);
        Optional<Post> optionalPost = this.postRepository.findPostOnebyBoardId(board.getId(), postId);
        optionalPost.orElseThrow(()-> new PostNotFoundException(postId));

        Post post = optionalPost.get();
        if(post.getPw().equals(postDto.getPw()))
            this.postRepository.delete(post);

        return true;
    }

    public Board findBoardById(Long id) {
        Optional<Board> board = boardRepository.findById(id);
        board.orElseThrow(() -> new BoardNotFoundException(id));
        return board.get();
    }


}
