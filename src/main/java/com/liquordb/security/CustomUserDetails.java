package com.liquordb.security;

import com.liquordb.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

    // TODO User를 가질것인가 일부필드만 가질것인가
    // private final User user

    private final UUID id;          // 엔터티의 PK (신고자 ID 등으로 활용)
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // 빌더나 생성자를 통해 엔터티 정보를 주입받음
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getUsername() { return email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    // 계정 만료, 잠금 등의 로직 (필요에 따라 엔터티 필드와 연동)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}