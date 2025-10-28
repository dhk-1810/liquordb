package com.liquordb;

import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    /**
     * 사용자가 댓글이나 리뷰를 작성할 수 있는 상태인지 검증
     */
    public void validateCanPost(User user) {
        if (user == null) {
            throw new IllegalArgumentException("로그인이 필요한 기능입니다.");
        }
        if (user.getStatus().equals(UserStatus.RESTRICTED) || user.getStatus().equals(UserStatus.BANNED)) {
            throw new IllegalStateException("신고 접수로 인해 작성이 제한되었습니다.");
        }
    }


}
