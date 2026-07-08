package com.liquordb.event.listener;

import com.liquordb.LiquorActivityManager;
import com.liquordb.SseMessage;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.entity.Notification;
import com.liquordb.event.CommentLikeEvent;
import com.liquordb.event.LiquorLikeEvent;
import com.liquordb.event.ReviewLikeEvent;
import com.liquordb.repository.NotificationRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.service.SseService;
import lombok.RequiredArgsConstructor;
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

        // 인기 주류 집계를 위해 ID 캐싱 - ZSet에 활동 점수(좋아요: 5점) 가산
        if (delta == 1) {
            liquorActivityManager.trackActivity(event.liquorId(), 5);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ReviewLikeEvent event) {
        if (!event.isLiked()) return;

        // 본인의 좋아요에 대해서는 알림을 생성하지 않음
        if (event.senderId().equals(event.receiverId())) {
            return;
        }

        saveAndSendNotification(event.receiverId(), event.senderUsername(), REVIEW_LIKE_MESSAGE_SUFFIX);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentLikeEvent event) {
        if (!event.isLiked()) return;

        // 본인의 좋아요에 대해서는 알림을 생성하지 않음
        if (event.senderId().equals(event.receiverId())) {
            return;
        }

        saveAndSendNotification(event.receiverId(), event.senderUsername(), COMMENT_LIKE_MESSAGE_SUFFIX);
    }

    private void saveAndSendNotification(UUID receiverId, String senderUsername, String messageSuffix) {
        Notification notification = Notification.create(
                receiverId,
                senderUsername + messageSuffix,
                null
        );
        notificationRepository.save(notification);

        NotificationResponseDto response = NotificationResponseDto.toDto(notification);
        SseMessage message = SseMessage.create(receiverId, "notification", response);
        redisTemplate.convertAndSend("sse-notifications", message);
    }
}
