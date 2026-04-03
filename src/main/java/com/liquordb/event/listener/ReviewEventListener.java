package com.liquordb.event.listener;

import com.liquordb.LiquorActivityManager;
import com.liquordb.enums.PeriodType;
import com.liquordb.event.ReviewCreatedEvent;
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
public class ReviewEventListener {

    private final StringRedisTemplate stringRedisTemplate;
    private final LiquorActivityManager liquorActivityManager;
    private static final String ACTIVE_KEY_PREFIX = "active:liquors:";

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ReviewCreatedEvent event) {

        liquorActivityManager.trackActivity(event.liquorId());
    }

}
