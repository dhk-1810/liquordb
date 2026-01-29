package com.liquordb.service;

import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.UserRepository;
import com.liquordb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        UserResponseDto dto = UserMapper.toDto(user);
        return new CustomUserDetails(user.getId(), dto, user.getPassword());
    }
}