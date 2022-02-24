package dev.yoon.jpa.dto;

import dev.yoon.jpa.entity.PostEntity;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private int id;

    private String title;

    private String content;

    private String writer;

    private int boardId;

    public static PostDto createPostDto(PostEntity entity) {
        return new PostDto(
                Math.toIntExact(entity.getId()),
                entity.getTitle(),
                entity.getContent(),
                entity.getWriter(),
                entity.getBoardEntity() == null
                        ? 0 : Math.toIntExact(entity.getBoardEntity().getId())
        );
    }
}
