package com.liquordb.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.SseMessage;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SseService sseService; // 인메모리 SseEmitter 저장소

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 온 JSON 메시지를 DTO로 변환
            SseMessage sseMessage = objectMapper.readValue(message.getBody(), SseMessage.class);

            NotificationResponseDto notification = objectMapper.convertValue(
                    sseMessage.data(),
                    NotificationResponseDto.class
            );

            // 내 서버에 연결된 사용자라면 SSE 전송 (없으면 무시)
            sseService.send(notification, sseMessage.eventName(), notification.receiverId());
        } catch (IOException e) {
            log.error("Redis 메시지 역직렬화 실패", e);
        }
    }
}
