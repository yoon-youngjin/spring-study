package dev.yoon.jpa.repository;

import dev.yoon.jpa.entity.PostEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<PostEntity, Long> {
    // CrudRepo에서 제공하는 함수이외의 기능을 사용하고 싶은 경우 따로 작성
    // select * from POST where writer = ?
    List<PostEntity> findAllByWriter(String writer);

    //  select * from POST where writer = ? and board_entity_id = ?
//    List<PostEntity> findAllByWriterAndBoardEntity(String writer, BoardEntity entity);

    // writer의 내용을 포함하는 PostEntity 반환
    List<PostEntity> findAllByWriterContaining(String writer);

}
