package dev.yoon.auth.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "USER_ENTITY")
@Getter @Setter
@ToString
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String role;

    /**
     *
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     *
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     *
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * UserDetails 내의 로그인 정보가 만료 상태인지 확인
     * false: 사용 불가 계정
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 유저가 휴면 계정인지 판단
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호의 유통기한이 만료되었는지 판단
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * 사용자의 계정이 사용 가능한지 판단
     * isAccountNonExpired <-> isEnabled
     * isAccountNonExpired: 애초에 계정이 처음 발급된 시점에 만료 기한을 설정하여 기한이 만료된 경우 -> 일시적 계정, 기한에 따라 계정이 수동적으로 상태가 변경
     * isEnabled: 계정을 사용을 막을 경우 -> 사용자가 더 이상 계정을 사용하지 않는다고 능동적으로 명시
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
