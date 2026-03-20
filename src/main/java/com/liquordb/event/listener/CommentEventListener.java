package com.liquordb.event.listener;

import com.liquordb.entity.Notification;
import com.liquordb.event.CommentCreatedEvent;
import com.liquordb.repository.NotificationRepository;
import com.liquordb.repository.notice.NoticeRepository;
import lombok.RequiredArgsConstructor;
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

    @Async("eventTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(CommentCreatedEvent event) {
        Notification notification = Notification.create(
                event.receiverId(),
                event.writerUsername() + MESSAGE_SUFFIX,
                null
        );
        notificationRepository.save(notification);

    }

}
