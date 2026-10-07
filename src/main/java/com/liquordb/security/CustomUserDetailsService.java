package com.liquordb.security;

import com.liquordb.entity.User;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * DB에서 해당 이메일의 유저를 조회하여 CustomUserDetails 생성.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getPassword()
        );
    }
}