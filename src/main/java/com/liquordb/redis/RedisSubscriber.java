package com.liquordb.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquordb.SseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class RedisSubscriber implements MessageListener {

    public static final String USER_TOPIC_PREFIX = "sse:user:";

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisMessageListenerContainer redisContainer;

    public void subscribe(UUID userId) {
        ChannelTopic topic = new ChannelTopic(USER_TOPIC_PREFIX + userId);
        redisContainer.addMessageListener(this, topic);
        log.info("Redis 유저 채널 동적 구독 완료: {}{}", USER_TOPIC_PREFIX, userId);
    }

    public void unsubscribe(UUID userId) {
        ChannelTopic topic = new ChannelTopic(USER_TOPIC_PREFIX + userId);
        redisContainer.removeMessageListener(this, topic);
        log.info("Redis 유저 채널 동적 구독 해제 완료: {}{}", USER_TOPIC_PREFIX, userId);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 역직렬화 후 내부 이벤트로 발행 (SseService 직접 의존 제거)
            SseMessage sseMessage = objectMapper.readValue(message.getBody(), SseMessage.class);
            eventPublisher.publishEvent(sseMessage);
        } catch (IOException e) {
            log.error("Redis 메시지 역직렬화 실패", e);
        }
    }
}
