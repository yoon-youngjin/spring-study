package dev.yoon.refactoring_board.dto;

import dev.yoon.refactoring_board.domain.Board;
import dev.yoon.refactoring_board.domain.Post;
import dev.yoon.refactoring_board.dto.common.DateTime;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class PostDto {

    @Getter
    public static class Req {
//        @NotNull
//        private Long userId;

        @NotNull
        @Size(min = 3, max = 10, message = "size between 3 - 10")
        private String title;

        @NotNull
        @Size(max = 40, message = "size under 40")
        private String content;

        @NotNull
        @Size(min = 3, max = 10, message = "size between 3 - 10")
        private String writer;

        @NotNull
        private String pw;

        @Builder
        public Req(String title, String content, String writer, String pw) {
//            this.userId = userId;
            this.title = title;
            this.content = content;
            this.writer = writer;
            this.pw = pw;

        }

        public Post toEntity() {
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .writer(this.writer)
                    .pw(this.pw)
                    .build();
        }


    }

    @Getter
    public static class Res {
//        @NotNull
//        private Long userId;

        private Long boardId;

        private String boardName;

        private DateTime dateTime;


        @NotNull
        @Size(min = 3, max = 10, message = "size between 3 - 10")
        private String title;

        @NotNull
        @Size(max = 40, message = "size under 40")
        private String content;

        @NotNull
        @Size(min = 3, max = 10, message = "size between 3 - 10")
        private String writer;

        public Res(Post post) {
//            this.userId = post.getId();
            this.boardId = post.getBoard().getId();
            this.boardName = post.getBoard().getName();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.writer = post.getWriter();
            this.dateTime = new DateTime(post.getCreatedDate(), post.getModifiedDate());

        }

    }


//    public PostDto(Post post) {
////        this.userId = post.getUser().getId();
//        this.boardId = post.getBoard().getId(); // LAZY
//        this.boardName = post.getBoard().getName(); // LAZY
//        this.title = post.getTitle();
//        this.content = post.getContent();
//        this.writer = post.getWriter();
//        this.pw = "*****";
//    }
//
//    public static PostDto createPostDtoPassWordMasked(Post post) {
//        PostDto postDto = new PostDto();
////        postDto.setUserId(post.getUser().getId());
//        postDto.setBoardId(post.getBoard().getId());
//        postDto.setBoardName(post.getBoard().getName());
//        postDto.setTitle(post.getTitle());
//        postDto.setWriter(post.getWriter());
//        postDto.setPw("*****");
//        postDto.setContent(post.getContent());
//        return postDto;
//    }


}
