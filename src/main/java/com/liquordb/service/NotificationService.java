package com.liquordb.service;

import com.liquordb.SseMessage;
import com.liquordb.dto.NotificationListGetRequest;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.entity.Notification;
import com.liquordb.exception.notification.NotificationAccessDeniedException;
import com.liquordb.exception.notification.NotificationNotFoundException;
import com.liquordb.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {

    private static final String SSE_SESSION_PREFIX = "sse:session:";

    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public void sendNotification(UUID receiverId, String content) {
        Notification notification = Notification.create(receiverId, content, null);
        notificationRepository.save(notification);

        // 1. 대상 유저가 접속 중인 서버 ID 조회
        String targetServerId = stringRedisTemplate.opsForValue().get(SSE_SESSION_PREFIX + receiverId);
        if (targetServerId == null) {
            log.debug("사용자({}) 오프라인 상태. SSE 전송 생략.", receiverId);
            return;
        }

        // 2. 대상 서버의 전용 토픽으로만 Direct 발행
        try {
            NotificationResponseDto response = NotificationResponseDto.toDto(notification);
            SseMessage message = SseMessage.create(receiverId, "notification", response);
            redisTemplate.convertAndSend("sse:server:" + targetServerId, message);
        } catch (Exception e) {
            log.warn("Redis 알림 전송 실패: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> get(NotificationListGetRequest request, UUID userId) {

        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> notifications = notificationRepository.findAll(userId, request.cursor(), pageable);
        return notifications.stream().map(NotificationResponseDto::toDto).toList();
    }

    @Transactional
    public void read(Long id, UUID userId) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        if (!userId.equals(notification.getReceiverId())){
            throw new NotificationAccessDeniedException(id, userId);
        }
        notification.read();
        notificationRepository.save(notification);
    }

    @Transactional
    public void delete(Long id, UUID userId) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        if (!userId.equals(notification.getReceiverId())){
            throw new NotificationAccessDeniedException(id, userId);
        }
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void clear(UUID userId){
        notificationRepository.deleteAllByReceiverId(userId);
    }

}
