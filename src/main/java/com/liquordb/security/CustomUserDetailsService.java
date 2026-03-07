package com.liquordb.security;

import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.exception.auth.WithdrawnUserException;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * DB에서 해당 이메일의 유저를 찾고, 탈퇴 여부(WITHDRAWN)를 체크.
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

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new WithdrawnUserException(); // DisabledException을 상속
        }

        UserResponseDto dto = UserMapper.toDto(user);
        return new CustomUserDetails(user.getId(), dto, user.getPassword());
    }
}