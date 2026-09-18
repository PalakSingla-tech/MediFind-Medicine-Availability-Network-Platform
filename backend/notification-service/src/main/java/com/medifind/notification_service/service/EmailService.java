package com.medifind.notification_service.service;

import com.medifind.notification_service.model.NotificationStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Getter
    public static class DeliveryResult {
        private final NotificationStatus status;
        private final String errorMessage;

        public DeliveryResult(NotificationStatus status, String errorMessage) {
            this.status = status;
            this.errorMessage = errorMessage;
        }
    }

    public DeliveryResult sendEmail(String to, String subject, String body) {
        log.info("Attempting to send email to: [{}], Subject: [{}]", to, subject);

        // Check if JavaMailSender is configured and credentials provided
        if (mailSender == null || mailUsername == null || mailUsername.isBlank()) {
            log.warn("[EMAIL SIMULATION] JavaMailSender credentials not configured. Simulating email dispatch.");
            log.info("[EMAIL SIMULATION] To: {}\nSubject: {}\nBody: {}", to, subject, body);
            return new DeliveryResult(NotificationStatus.SIMULATED, "Simulated delivery: Mail sender credentials not configured");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email successfully sent via JavaMailSender to: {}", to);
            return new DeliveryResult(NotificationStatus.SENT, null);
        } catch (Exception e) {
            log.error("Failed to send real email via JavaMailSender: {}. Falling back to simulation.", e.getMessage());
            log.info("[EMAIL FALLBACK SIMULATION] To: {}\nSubject: {}\nBody: {}", to, subject, body);
            return new DeliveryResult(NotificationStatus.SIMULATED, "Email simulated due to SMTP delivery error: " + e.getMessage());
        }
    }
}
