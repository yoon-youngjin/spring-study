package dev.yoon.mybatisdemo.dao;

import dev.yoon.mybatisdemo.dto.BoardDto;
import dev.yoon.mybatisdemo.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardDao {
    private final SqlSessionFactory sessionFactory;

    public int createBoard(BoardDto boardDto) {
        try (SqlSession session = sessionFactory.openSession()) {
            BoardMapper mapper = session.getMapper(BoardMapper.class);
            return mapper.createBoard(boardDto);
        }
    }



}
