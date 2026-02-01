package com.liquordb.service;

import com.liquordb.dto.user.UserLoginRequestDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserResponseDto restore(UserLoginRequestDto request) {}
}
