package com.medifind.notification_service.service.impl;

import com.medifind.notification_service.dto.EmailNotificationRequest;
import com.medifind.notification_service.dto.NotificationResponse;
import com.medifind.notification_service.dto.SendNotificationRequest;
import com.medifind.notification_service.dto.SmsNotificationRequest;
import com.medifind.notification_service.model.Notification;
import com.medifind.notification_service.model.NotificationStatus;
import com.medifind.notification_service.model.NotificationType;
import com.medifind.notification_service.repository.NotificationRepository;
import com.medifind.notification_service.service.EmailService;
import com.medifind.notification_service.service.NotificationService;
import com.medifind.notification_service.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    @Transactional
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Processing generic notification request of type: [{}] for recipient: [{}]",
                request.getType(), request.getRecipient());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .type(request.getType())
                .subject(request.getSubject())
                .message(request.getMessage())
                .sourceService(request.getSourceService() != null ? request.getSourceService() : "direct")
                .status(NotificationStatus.PENDING)
                .read(false)
                .build();

        NotificationStatus deliveryStatus = NotificationStatus.SENT;
        String errorMessage = null;

        if (request.getType() == NotificationType.EMAIL) {
            EmailService.DeliveryResult result = emailService.sendEmail(
                    request.getRecipient(),
                    request.getSubject() != null ? request.getSubject() : "MediFind Notification",
                    request.getMessage()
            );
            deliveryStatus = result.getStatus();
            errorMessage = result.getErrorMessage();
        } else if (request.getType() == NotificationType.SMS) {
            SmsService.DeliveryResult result = smsService.sendSms(
                    request.getRecipient(),
                    request.getMessage()
            );
            deliveryStatus = result.getStatus();
            errorMessage = result.getErrorMessage();
        } else if (request.getType() == NotificationType.IN_APP) {
            deliveryStatus = NotificationStatus.SENT;
            log.info("In-app notification registered for user [{}]", request.getUserId());
        }

        notification.setStatus(deliveryStatus);
        notification.setErrorMessage(errorMessage);

        Notification saved = notificationRepository.save(notification);
        log.info("Notification #{} persisted with status [{}]", saved.getId(), saved.getStatus());

        return NotificationResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public NotificationResponse sendEmail(EmailNotificationRequest request) {
        log.info("Processing email notification for: [{}], Subject: [{}]",
                request.getRecipientEmail(), request.getSubject());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipientEmail())
                .type(NotificationType.EMAIL)
                .subject(request.getSubject())
                .message(request.getMessageBody())
                .sourceService(request.getSourceService() != null ? request.getSourceService() : "pharmacy-service")
                .status(NotificationStatus.PENDING)
                .read(false)
                .build();

        EmailService.DeliveryResult result = emailService.sendEmail(
                request.getRecipientEmail(),
                request.getSubject(),
                request.getMessageBody()
        );

        notification.setStatus(result.getStatus());
        notification.setErrorMessage(result.getErrorMessage());

        Notification saved = notificationRepository.save(notification);
        log.info("Email notification #{} persisted with status [{}]", saved.getId(), saved.getStatus());

        return NotificationResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public NotificationResponse sendSms(SmsNotificationRequest request) {
        log.info("Processing SMS notification for: [{}]", request.getRecipientPhone());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .recipient(request.getRecipientPhone())
                .type(NotificationType.SMS)
                .subject("SMS Notification")
                .message(request.getMessageContent())
                .sourceService(request.getSourceService() != null ? request.getSourceService() : "emergency-service")
                .status(NotificationStatus.PENDING)
                .read(false)
                .build();

        SmsService.DeliveryResult result = smsService.sendSms(
                request.getRecipientPhone(),
                request.getMessageContent()
        );

        notification.setStatus(result.getStatus());
        notification.setErrorMessage(result.getErrorMessage());

        Notification saved = notificationRepository.save(notification);
        log.info("SMS notification #{} persisted with status [{}]", saved.getId(), saved.getStatus());

        return NotificationResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationHistoryByUser(String userId) {
        log.info("Fetching notification history for userId: [{}]", userId);
        List<Notification> notifications;
        if (userId == null || userId.isBlank() || userId.equalsIgnoreCase("all")) {
            notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return notifications.stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(saved);
    }
}
