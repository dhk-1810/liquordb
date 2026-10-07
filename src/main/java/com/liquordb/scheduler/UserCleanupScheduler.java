package com.liquordb.scheduler;

import com.liquordb.entity.User;
import com.liquordb.enums.UserStatus;
import com.liquordb.redis.RedisLockProvider;
import com.liquordb.repository.user.UserRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserCleanupScheduler {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final RedisLockProvider redisLockProvider;

    // 매일 새벽 3시에 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteWithdrawnUsers() {
        redisLockProvider.executeIfLockAcquired("user-cleanup:daily", 60L, this::deleteWithdrawnUsersInternal);
    }

    @Transactional
    public void deleteWithdrawnUsersInternal() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        List<User> usersToDelete = userRepository.findAllByStatusAndWithdrawnAtBefore(
                UserStatus.WITHDRAWN, weekAgo
        );

        if (!usersToDelete.isEmpty()) {
            List<UUID> userIds = usersToDelete.stream().map(User::getId).toList();
            reviewRepository.setNullUserByUserIds(userIds);
            commentRepository.setNullUserByUserIds(userIds);
            userRepository.deleteAll(usersToDelete);
        }
    }
}
