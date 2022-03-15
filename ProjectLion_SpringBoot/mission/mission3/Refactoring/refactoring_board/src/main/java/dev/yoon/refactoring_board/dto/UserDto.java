package dev.yoon.refactoring_board.dto;

import dev.yoon.refactoring_board.domain.Address;
import dev.yoon.refactoring_board.domain.Location;
import dev.yoon.refactoring_board.domain.user.User;
import dev.yoon.refactoring_board.domain.user.UserCategory;
import dev.yoon.refactoring_board.dto.common.DateTime;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Req {
        @NotEmpty
        private String name;

        private UserCategory userCategory;

        public Req(String name, UserCategory userCategory) {
            this.name = name;
            this.userCategory = userCategory;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpReq {

//        @Valid
//        private Email email;

        @NotEmpty
        private String name;

        private String password;

        private Location location;

        private Address address;

        private UserCategory userCategory;

        @Builder
        public SignUpReq(String name, String password, Address address, Location location, UserCategory userCategory) {
            this.name = name;
            this.password = password;
            this.address = address;
            this.location = location;
            this.userCategory = userCategory;
        }

        public User toEntity() {
            return User.builder()
                    .username(this.name)
                    .password(this.password)
                    .userCategory(this.userCategory)
                    .build();
        }

    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Res {

        @NotEmpty
        private String name;

        private String password;

        private Address address;

        private Location location;

        private DateTime dateTime;

        @NotEmpty
        private UserCategory userCategory;

        public Res(User user) {
            this.name = user.getUsername();
            this.password = user.getPassword();

            this.address = user.getArea().getAddress();
            this.location = user.getArea().getLocation();
            this.dateTime = new DateTime(user.getCreatedDate(), user.getModifiedDate());
            this.userCategory = user.getUserCategory();
        }

    }


}
