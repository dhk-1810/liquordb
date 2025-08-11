package com.liquordb.user;

import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserStatus;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;

/**
 * 현재 이용제한 상태인 유저인지 검증하는 Validator입니다.
 */
@Component
public class UserValidator {

    public void validateCanPost(User user) {
        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccessDeniedException("제한된 사용자입니다. 작성이 불가능합니다.");
        }
    }
}
