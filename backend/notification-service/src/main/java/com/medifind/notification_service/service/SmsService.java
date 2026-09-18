package com.medifind.notification_service.service;

import com.medifind.notification_service.config.TwilioConfig;
import com.medifind.notification_service.model.NotificationStatus;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    private final TwilioConfig twilioConfig;
    private boolean initialized = false;

    @Getter
    public static class DeliveryResult {
        private final NotificationStatus status;
        private final String errorMessage;

        public DeliveryResult(NotificationStatus status, String errorMessage) {
            this.status = status;
            this.errorMessage = errorMessage;
        }
    }

    @PostConstruct
    public void init() {
        if (twilioConfig.isConfigured()) {
            try {
                Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
                initialized = true;
                log.info("Twilio SDK initialized successfully with Account SID: {}...",
                        twilioConfig.getAccountSid().substring(0, Math.min(8, twilioConfig.getAccountSid().length())));
            } catch (Exception e) {
                log.warn("Could not initialize Twilio SDK: {}. Operating in mock/simulation mode.", e.getMessage());
            }
        } else {
            log.info("Twilio is not configured with live credentials. Operating in mock/simulation mode.");
        }
    }

    public DeliveryResult sendSms(String toPhoneNumber, String messageBody) {
        log.info("Attempting to send SMS to: [{}]", toPhoneNumber);

        if (!initialized || !twilioConfig.isConfigured()) {
            log.info("[TWILIO SMS SIMULATION] To: {}\nFrom: {}\nMessage: {}",
                    toPhoneNumber, twilioConfig.getPhoneNumber(), messageBody);
            return new DeliveryResult(NotificationStatus.SIMULATED, "Simulated SMS: Twilio credentials not configured");
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    messageBody
            ).create();

            log.info("Twilio SMS sent successfully. Message SID: {}, Status: {}", message.getSid(), message.getStatus());
            return new DeliveryResult(NotificationStatus.SENT, null);
        } catch (Exception e) {
            log.error("Failed to send real SMS via Twilio: {}. Falling back to simulation.", e.getMessage());
            log.info("[TWILIO SMS FALLBACK SIMULATION] To: {}\nFrom: {}\nMessage: {}",
                    toPhoneNumber, twilioConfig.getPhoneNumber(), messageBody);
            return new DeliveryResult(NotificationStatus.SIMULATED, "SMS simulated due to Twilio error: " + e.getMessage());
        }
    }
}
