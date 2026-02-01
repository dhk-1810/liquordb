package com.liquordb;

import com.liquordb.entity.ReportableEntity;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReportManager {

    private final UserRepository userRepository;

    // 유저 제재
    public void processUserPenalty(User user) {
        if (user.getReportCount() >= 5) {
            user.ban();
        } else if (user.getReportCount() >= 3) {
            user.suspend(LocalDateTime.now().plusDays(7));
        }
        userRepository.save(user);
    }

    // 신고 누적 시 자동 숨기기
    public void hideIfOverThreshold(long count, int threshold, ReportableEntity target) {
        if (count >= threshold) {
            target.hide(LocalDateTime.now());
        }
    }
}
