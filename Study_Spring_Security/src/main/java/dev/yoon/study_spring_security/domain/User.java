package dev.yoon.study_spring_security.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "auth")
    private String auth;

    @Column(name = "TOKEN_ID")
    private String tokenId;

    @Builder
    public User(String email, String password, String auth) {
        this.email = email;
        this.password = password;
        this.auth = auth;
    }


    /**
     * 사용자 권한을 콜렉션 형태로 반환
     * 단, 클래스 자료형은 GrantedAuthority를 구현해야함
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 중복을 막기 위해 Set을 사용
        Set<GrantedAuthority> roles = new HashSet<>();
        // ADMIN은 관리자의 권한(ADMIN)뿐만 아니라 일반 유저(USER)의 권한도 가지고 있기 때문에, ADMIN의 auth는 "ROLE_ADMIN,ROLE_USER"와 같은 형태로 전달
        for (String role : auth.split(",")) {
            roles.add(new SimpleGrantedAuthority(role));
        }
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료 x
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금 x
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드 만료되었는지 확인하는 로직
        return true; // true -> 만료 x
    }

    @Override
    public boolean isEnabled() {
        // 계정 사용 가능한지 확인하는 로직
        return true; // true -> 사용 o
    }
}
