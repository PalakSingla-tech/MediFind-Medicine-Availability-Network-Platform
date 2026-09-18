package com.medifind.notification_service.dto;

import com.medifind.notification_service.model.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    private String userId;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotNull(message = "Notification type is required (EMAIL, SMS, IN_APP)")
    private NotificationType type;

    private String subject;

    @NotBlank(message = "Message content is required")
    private String message;

    private String sourceService;
}
