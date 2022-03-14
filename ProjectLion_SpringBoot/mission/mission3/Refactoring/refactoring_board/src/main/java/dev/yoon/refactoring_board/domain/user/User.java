package dev.yoon.refactoring_board.domain.user;

import dev.yoon.refactoring_board.common.BaseTimeEntity;
import dev.yoon.refactoring_board.domain.Area;
import dev.yoon.refactoring_board.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "community_user")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserCategory userCategory;

    @ManyToOne(
            targetEntity = Area.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "area_id")
    private Area area;

    @Builder
    public User(String username, String password,Area area, UserCategory userCategory) {
        this.username = username;
        this.area = area;
        this.password = password;
        this.userCategory = userCategory;
    }

    public User(UserDto.SignUpReq dto) {
        this.username = dto.getName();
        this.password = dto.getPassword();
        this.userCategory = dto.getUserCategory();

    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return false;
//    }

    public void updateUser(UserDto.Req userDto) {

        this.username = userDto.getName();
        this.userCategory = userDto.getUserCategory();

    }

}
