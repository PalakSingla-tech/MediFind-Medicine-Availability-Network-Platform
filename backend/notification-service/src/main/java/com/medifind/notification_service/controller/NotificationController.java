package com.medifind.notification_service.controller;

import com.medifind.notification_service.dto.EmailNotificationRequest;
import com.medifind.notification_service.dto.NotificationResponse;
import com.medifind.notification_service.dto.SendNotificationRequest;
import com.medifind.notification_service.dto.SmsNotificationRequest;
import com.medifind.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST /api/notifications/send
     * Generalized notification endpoint supporting EMAIL, SMS, and IN_APP.
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        log.info("Received POST /api/notifications/send for type: {}, recipient: {}", request.getType(), request.getRecipient());
        NotificationResponse response = notificationService.sendNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/notifications/email
     * Send email notification (e.g. low stock alert from pharmacy-service).
     */
    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(@Valid @RequestBody EmailNotificationRequest request) {
        log.info("Received POST /api/notifications/email for recipient: {}", request.getRecipientEmail());
        NotificationResponse response = notificationService.sendEmail(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/notifications/sms
     * Send SMS notification (e.g. emergency alert to pharmacies from emergency-service).
     */
    @PostMapping("/sms")
    public ResponseEntity<NotificationResponse> sendSms(@Valid @RequestBody SmsNotificationRequest request) {
        log.info("Received POST /api/notifications/sms for recipient: {}", request.getRecipientPhone());
        NotificationResponse response = notificationService.sendSms(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/notifications/user?userId=...
     * Retrieve notification history for a user.
     */
    @GetMapping("/user")
    public ResponseEntity<List<NotificationResponse>> getNotificationHistory(@RequestParam(name = "userId", required = false) String userId) {
        log.info("Received GET /api/notifications/user for userId: {}", userId);
        List<NotificationResponse> history = notificationService.getNotificationHistoryByUser(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * POST /api/notifications/emergency/pharmacy-sms
     * Dedicated convenience endpoint for emergency-service to notify pharmacies via SMS.
     */
    @PostMapping("/emergency/pharmacy-sms")
    public ResponseEntity<NotificationResponse> sendEmergencyPharmacySms(@Valid @RequestBody SmsNotificationRequest request) {
        if (request.getSourceService() == null || request.getSourceService().isBlank()) {
            request.setSourceService("emergency-service");
        }
        log.info("Received inter-service call: emergency-service -> notification-service (SMS to {})", request.getRecipientPhone());
        NotificationResponse response = notificationService.sendSms(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/notifications/pharmacy/low-stock
     * Dedicated convenience endpoint for pharmacy-service to send low stock alert emails.
     */
    @PostMapping("/pharmacy/low-stock")
    public ResponseEntity<NotificationResponse> sendPharmacyLowStockEmail(@Valid @RequestBody EmailNotificationRequest request) {
        if (request.getSourceService() == null || request.getSourceService().isBlank()) {
            request.setSourceService("pharmacy-service");
        }
        log.info("Received inter-service call: pharmacy-service -> notification-service (Email to {})", request.getRecipientEmail());
        NotificationResponse response = notificationService.sendEmail(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PATCH /api/notifications/{id}/read
     * Mark notification as read.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable("id") Long id) {
        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }
}
