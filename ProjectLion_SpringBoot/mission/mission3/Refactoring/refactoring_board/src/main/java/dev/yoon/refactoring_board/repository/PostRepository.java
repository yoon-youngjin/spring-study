package dev.yoon.refactoring_board.repository;

import dev.yoon.refactoring_board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("select p from Post p where p.board.id =:boardId")
    List<Post> findPostAllbyBoardId(Long boardId);

    @Query("select p from Post p where p.board.id =:boardId and p.id =:postId")
    Optional<Post> findPostOnebyBoardId(Long boardId, Long postId);

}
