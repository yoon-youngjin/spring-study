package dev.yoon.mybatisdemo.mapper;

import dev.yoon.mybatisdemo.dto.PostDto;

import java.util.List;

// xml에 정의해준 namespace = interface이름
public interface PostMapper {
    // xml에 정의해준 id값 = 함수이름, parameterType = 파라미터
    // insert, delete, update문의 경우 return값이 몇개의 row가 조작되었는지가 반환됨
    int createPost(PostDto dto);

//    PostDto readPost(int id);
//
//    List<PostDto> readPostAll();
//
//    int updatePost(PostDto postDto);
//
//    int deletePost(int id);

}
