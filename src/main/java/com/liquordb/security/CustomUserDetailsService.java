package com.liquordb.security;

import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.auth.WithdrawnUserException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            if (user.getWithdrawnAt() == null || user.getWithdrawnAt().isBefore(LocalDateTime.now().minusWeeks(1))) {
                throw new WithdrawnUserException(); // 1주일 초과 시 로그인 불가
            }
            // 1주일 이내 탈퇴 유저는 복구를 위해 UserDetails 생성을 진행함
        }

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getPassword()
        );
    }
}