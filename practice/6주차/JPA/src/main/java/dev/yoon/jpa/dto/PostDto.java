package dev.yoon.jpa.dto;

import dev.yoon.jpa.entity.PostEntity;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private int id;

    @NotNull(message = "not null")
    private String title;

    @Size(max = 40, message = "size under 40")
    private String content;

    @Size(min = 3, max = 10, message = "size between 3 - 10")
    private String writer;

    private int boardId;

//    @Valid
//    private ValidTestDto testDto;

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
