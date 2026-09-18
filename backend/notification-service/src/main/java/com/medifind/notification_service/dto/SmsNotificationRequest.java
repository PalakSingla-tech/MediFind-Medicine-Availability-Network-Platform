package com.medifind.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsNotificationRequest {

    private String userId;

    @JsonAlias({"to", "recipient", "phone", "mobile"})
    @NotBlank(message = "Phone number ('phoneNumber' or 'to') is required")
    private String phoneNumber;

    @JsonAlias({"content", "body"})
    @NotBlank(message = "SMS message content is required")
    private String message;

    private String sourceService;

    public String getRecipientPhone() {
        return phoneNumber;
    }

    public String getMessageContent() {
        return message;
    }
}
