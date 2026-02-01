package com.liquordb.security;

import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "userId")
public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final UserResponseDto dto;
    private final String password;

    @Override
    public String getUsername() {
        return dto.username();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleWithPrefix = dto.role().getAuthority();
        return List.of(new SimpleGrantedAuthority(roleWithPrefix));
    }

    @Override
    public boolean isEnabled() {
        return dto.status().isAvailable();
    }

}