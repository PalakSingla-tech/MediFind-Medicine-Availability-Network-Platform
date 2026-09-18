package com.medifind.notification_service.repository;

import com.medifind.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);

    List<Notification> findAllByOrderByCreatedAtDesc();
}
