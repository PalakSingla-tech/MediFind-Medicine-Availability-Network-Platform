package com.medifind.notification_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {

    private String userId;

    @JsonAlias({"recipient", "recipientEmail", "email"})
    @NotBlank(message = "Recipient email ('to' or 'recipient') is required")
    private String to;

    @NotBlank(message = "Subject is required")
    private String subject;

    @JsonAlias({"message", "content"})
    @NotBlank(message = "Email body/message is required")
    private String body;

    private String sourceService;

    public String getRecipientEmail() {
        return to;
    }

    public String getMessageBody() {
        return body;
    }
}
