package com.liquordb.event.listener;

import com.liquordb.event.CommentLikeEvent;
import com.liquordb.event.LiquorLikeEvent;
import com.liquordb.event.ReviewLikeEvent;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class LikeEventListener {

    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(LiquorLikeEvent event) {
        int delta = event.isLiked() ? 1 : -1;
        liquorRepository.updateLikeCount(event.liquorId(), delta);
    }

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ReviewLikeEvent event) {
        int delta = event.isLiked() ? 1 : -1;
        reviewRepository.updateLikeCount(event.reviewId(), delta);
    }

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentLikeEvent event) {
        int delta = event.isLiked() ? 1 : -1;
        commentRepository.updateLikeCount(event.commentId(), delta);
    }
}
