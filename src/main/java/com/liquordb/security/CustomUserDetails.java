package com.liquordb.security;

import com.liquordb.dto.user.UserResponseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record CustomUserDetails (
        UUID userId,
        UserResponseDto dto,
        String password
) implements UserDetails {

    @Override
    public String getUsername() {
        return dto.email();
    }

    @Override
    public String getPassword() {
        return password;
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