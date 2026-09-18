package com.medifind.notification_service.service;

import com.medifind.notification_service.dto.EmailNotificationRequest;
import com.medifind.notification_service.dto.NotificationResponse;
import com.medifind.notification_service.dto.SendNotificationRequest;
import com.medifind.notification_service.dto.SmsNotificationRequest;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(SendNotificationRequest request);

    NotificationResponse sendEmail(EmailNotificationRequest request);

    NotificationResponse sendSms(SmsNotificationRequest request);

    List<NotificationResponse> getNotificationHistoryByUser(String userId);

    NotificationResponse markAsRead(Long notificationId);
}
