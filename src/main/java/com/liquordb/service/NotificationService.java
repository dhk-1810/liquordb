package com.liquordb.service;

import com.liquordb.dto.NotificationListGetRequest;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.entity.Notification;
import com.liquordb.exception.notification.NotificationAccessDeniedException;
import com.liquordb.exception.notification.NotificationNotFoundException;
import com.liquordb.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> get(NotificationListGetRequest request, UUID userId) {

        Pageable pageable = PageRequest.of(0, 10);
        List<Notification> notifications = notificationRepository.findAll(userId, request.cursor(), pageable);
        return notifications.stream().map(NotificationResponseDto::toDto).toList();
    }

    public void read(Long id, UUID userId) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        if (userId != notification.getReceiverId()){
            throw new NotificationAccessDeniedException(id, userId);
        }
        notification.read();
        notificationRepository.save(notification);
    }

    public void delete(Long id, UUID userId) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
        if (userId != notification.getReceiverId()){
            throw new NotificationAccessDeniedException(id, userId);
        }
        notificationRepository.deleteById(id);
    }

    public void clear(UUID userId){
        notificationRepository.deleteAllByReceiverId(userId);
    }

}
