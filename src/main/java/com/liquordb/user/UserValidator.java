package com.liquordb.user;

import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserStatus;
import com.liquordb.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;

/**
 * 현재 유저가 리뷰, 댓글 작성 가능한 상태인 검증하는 Validator입니다.
 */
@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateCanPost(User user) {
        switch (user.getStatus()) {
            case WARNED -> {
                // 기능 제한은 없음. 프론트에서 경고 창 1회 팝업.
            }
            case RESTRICTED -> {
                // 일시 이용 제한
                throw new AccessDeniedException("일시 이용제한 상태입니다. 리뷰 및 댓글 작성이 제한됩니다.");
            }
            case BANNED -> {
                // 강제 탈퇴 처리
                user.setStatus(UserStatus.WITHDRAWN); // soft delete
                userRepository.save(user);
                throw new AccessDeniedException("서비스 이용이 영구 제한되었습니다.");
            }
            case WITHDRAWN -> {
                // 이미 탈퇴한 회원
                throw new AccessDeniedException("탈퇴한 회원은 작성이 불가능합니다.");
            }
            case ACTIVE -> {
                // 정상 이용
            }
        }
    }
}

