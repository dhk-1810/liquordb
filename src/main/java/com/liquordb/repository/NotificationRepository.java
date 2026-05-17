package com.liquordb.repository;

import com.liquordb.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    void deleteAllByReceiverId(UUID receiverId);

    @Query("""
        SELECT n FROM Notification n
        WHERE n.receiverId = :userId
        AND (:lastId IS NULL OR n.id < :lastId)
        ORDER BY n.id DESC
    """)
    List<Notification> findAll(
            @Param("userId") UUID userId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
