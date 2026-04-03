package com.liquordb.event.listener;

import com.liquordb.LiquorActivityManager;
import com.liquordb.SseMessage;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.entity.Notification;
import com.liquordb.enums.PeriodType;
import com.liquordb.event.CommentLikeEvent;
import com.liquordb.event.LiquorLikeEvent;
import com.liquordb.event.ReviewLikeEvent;
import com.liquordb.repository.NotificationRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class LikeEventListener {

    private static final String EVENT_NAME = "notifications";
    private static final String REVIEW_LIKE_MESSAGE_SUFFIX = "님이 내 리뷰를 좋아합니다.";
    private static final String COMMENT_LIKE_MESSAGE_SUFFIX = "님이 내 댓글을 좋아합니다.";
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";

    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final LiquorActivityManager liquorActivityManager;
    private final SseService sseService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LiquorLikeEvent event) {

        int delta = event.isLiked() ? 1 : -1;
        liquorRepository.updateLikeCount(event.liquorId(), delta);

        // 인기 주류 집계를 위해 ID 캐싱 - 3시간, 일간, 주간 모든 후보군 Set에 ID 추가
        if (delta == 1) {
            liquorActivityManager.trackActivity(event.liquorId());
        }
    }

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ReviewLikeEvent event) {

        int delta = event.isLiked() ? 1 : -1;
        reviewRepository.updateLikeCount(event.reviewId(), delta);
        saveAndSendNotification(event.isLiked(), event.receiverId(), event.username(), REVIEW_LIKE_MESSAGE_SUFFIX, event);
    }

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentLikeEvent event) {

        int delta = event.isLiked() ? 1 : -1;
        commentRepository.updateLikeCount(event.commentId(), delta);
        saveAndSendNotification(event.isLiked(), event.receiverId(), event.username(), COMMENT_LIKE_MESSAGE_SUFFIX, event);
    }

    private void saveAndSendNotification(boolean liked, UUID uuid, String username, String reviewLikeMessageSuffix, Object event) {
        if (liked) {
            Notification notification = Notification.create(
                    uuid,
                    username + reviewLikeMessageSuffix,
                    null
            );
            notificationRepository.save(notification);

            NotificationResponseDto response = NotificationResponseDto.toDto(notification);
            SseMessage message = SseMessage.create(uuid, "notification", response);
            redisTemplate.convertAndSend("sse-notifications", message);
        }
    }
}
