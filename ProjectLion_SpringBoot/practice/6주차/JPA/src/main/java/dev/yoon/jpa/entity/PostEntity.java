package dev.yoon.jpa.entity;

import dev.yoon.jpa.dto.PostDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "post")
@Getter @Setter
@NoArgsConstructor
public class PostEntity extends BaseEntity{

    // JPA를 사용할 때 class기반의 object를 사용하기를 권장, 프리미티브 타입 사용 제한
    // => 기본형의 경우 null값을 받을 수 없음
    @Id // jpa에 해당 필드가 PK임을 명시
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 테이블을 생성하면서 아이디 생성 규칙을 명시
    private Long id;

    private String title;

    private String content;

    private String writer;

    // 게시판은 게시글을 가지고 있음 => 일대다 관계
    // fetch = FetchType.LAZY :
    // fetchtype은 연관관계에 있는 entity를 불러올때 불러오는 방식을 정의하는 것
    @ManyToOne(
            targetEntity = BoardEntity.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;


    public static PostEntity createPostEntity(PostDto postDto) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setContent(postDto.getContent());
        postEntity.setWriter(postDto.getWriter());
        postEntity.setBoardEntity(null);
        return postEntity;
    }
}
