package com.liquordb.event.listener;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class LikeEventListener {

    private static final String EVENT_NAME = "notifications";
    private static final String REVIEW_LIKE_MESSAGE_SUFFIX = "님이 내 리뷰를 좋아합니다.";
    private static final String COMMENT_LIKE_MESSAGE_SUFFIX = "님이 내 댓글을 좋아합니다.";

    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final SseService sseService;

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
        if (event.isLiked()) {
            Notification notification = Notification.create(
                    event.receiverId(),
                    event.username() + REVIEW_LIKE_MESSAGE_SUFFIX,
                    null
            );
            notificationRepository.save(notification);
            sseService.send(NotificationResponseDto.toDto(notification), EVENT_NAME, event.receiverId());
        }
    }

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentLikeEvent event) {

        int delta = event.isLiked() ? 1 : -1;
        commentRepository.updateLikeCount(event.commentId(), delta);
        if (event.isLiked()) {
            Notification notification = Notification.create(
                    event.receiverId(),
                    event.username() + COMMENT_LIKE_MESSAGE_SUFFIX,
                    null
            );
            notificationRepository.save(notification);
            sseService.send(NotificationResponseDto.toDto(notification), EVENT_NAME, event.receiverId());
        }
    }
}
