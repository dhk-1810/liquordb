package com.liquordb;

import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    // 매일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteWithdrawnUsers() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        List<User> usersToDelete = userRepository.findAllByStatusAndWithdrawnAtBefore(
                UserStatus.WITHDRAWN, weekAgo
        );

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
        }
    }
}
