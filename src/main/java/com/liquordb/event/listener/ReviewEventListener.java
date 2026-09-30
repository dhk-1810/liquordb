package com.liquordb.event.listener;

import com.liquordb.LiquorActivityManager;
import com.liquordb.event.ReviewCreatedEvent;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewEventListener {

    private final LiquorRepository liquorRepository;
    private final LiquorActivityManager liquorActivityManager;

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ReviewCreatedEvent event) {

        liquorRepository.updateReviewStats(event.liquorId(), event.rating());

        liquorActivityManager.trackActivity(event.liquorId(), 10);
    }

}
