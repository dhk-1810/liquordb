package com.liquordb.event.listener;

import com.liquordb.LiquorActivityManager;
import com.liquordb.SseMessage;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.entity.Notification;
import com.liquordb.enums.PeriodType;
import com.liquordb.event.CommentCreatedEvent;
import com.liquordb.repository.NotificationRepository;
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

@RequiredArgsConstructor
@Component
public class CommentEventListener {

    private static final String MESSAGE_SUFFIX = "님이 리뷰에 댓글을 남겼습니다.";

    private final NotificationRepository notificationRepository;
    private final LiquorActivityManager liquorActivityManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";


    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentCreatedEvent event) {

        // 알림 목록에 추가
        Notification notification = Notification.create(
                event.receiverId(),
                event.writerUsername() + MESSAGE_SUFFIX,
                null
        );
        notificationRepository.save(notification);

        // 알림 발송
        NotificationResponseDto response = NotificationResponseDto.toDto(notification);
        SseMessage message = SseMessage.create(event.receiverId(), "notification", response);
        redisTemplate.convertAndSend("sse-notifications", message);

        // 인기 주류 집계를 위해 ID 캐싱
        liquorActivityManager.trackActivity(event.liquorId());
    }

}
