package com.medifind.notification_service.dto;

import com.medifind.notification_service.model.Notification;
import com.medifind.notification_service.model.NotificationStatus;
import com.medifind.notification_service.model.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String userId;
    private String recipient;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private String sourceService;
    private String errorMessage;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationResponse fromEntity(Notification entity) {
        if (entity == null) {
            return null;
        }
        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .recipient(entity.getRecipient())
                .type(entity.getType())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .sourceService(entity.getSourceService())
                .errorMessage(entity.getErrorMessage())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
