package dev.yoon.mybatisdemo.dao;

import dev.yoon.mybatisdemo.dto.PostDto;
import dev.yoon.mybatisdemo.mapper.PostMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

// 실제로 mapper를 사용하여 통신을 하는 클래스
// @Repository : 해당 어노테이션이 붙은 클래스는 데이터를 주고 받기 위한 클래스임을 나타내는것
@Repository
public class PostDao {
    private final SqlSessionFactory sessionFactory;

    public PostDao(
            @Autowired SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public int createPost(PostDto postDto) {
        // openSession(true) : 디폴트, select제외한 table에 데이터 영향을 주는 것에 대해서 자동으로 저장
//        SqlSession session = sessionFactory.openSession();
//        // SqlSession에 존재하는 PostMapper.class의 구현체를 return
//        PostMapper mapper = session.getMapper(PostMapper.class);
//        int rowAffected = mapper.createPost(postDto);
//        // session.close를 자동으로 해주는 try-resource사용하자
//        session.close();
//        return rowAffected;
        try (SqlSession session = sessionFactory.openSession()) {
            PostMapper mapper = session.getMapper(PostMapper.class);
            return mapper.createPost(postDto);
        }
    }

//    public PostDto readPost(int id) {
//        try (SqlSession session = sessionFactory.openSession()) {
//            PostMapper mapper = session.getMapper(PostMapper.class);
//            return mapper.readPost(id);
//        }
//
//    }
//
//    public List<PostDto> readPostAll() {
//        try (SqlSession session = sessionFactory.openSession()) {
//            PostMapper mapper = session.getMapper(PostMapper.class);
//            return mapper.readPostAll();
//        }
//    }
//
//    public int updatePost(PostDto postDto) {
//        try (SqlSession session = sessionFactory.openSession()) {
//            PostMapper mapper = session.getMapper(PostMapper.class);
//            return mapper.updatePost(postDto);
//        }
//    }
//
//    public int deletePost(int id) {
//        // mapper 인스턴스는 thread-safe하지 않음
//        // => 빠른 요청에 있어서 각자의 요청에 영향을 미칠 수 있으므로 함수마다 session을 열어준다.
//        try (SqlSession session = sessionFactory.openSession()) {
//            PostMapper mapper = session.getMapper(PostMapper.class);
//            return mapper.deletePost(id);
//        }
//    }

}
