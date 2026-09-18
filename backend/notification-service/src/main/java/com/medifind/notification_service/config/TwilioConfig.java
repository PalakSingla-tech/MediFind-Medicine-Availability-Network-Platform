package com.medifind.notification_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Getter
@Setter
public class TwilioConfig {

    private String accountSid;
    private String authToken;
    private String phoneNumber;
    private String mockMode = "auto";

    public boolean isConfigured() {
        return accountSid != null && !accountSid.isBlank() && !accountSid.startsWith("AC_MOCK")
                && authToken != null && !authToken.isBlank() && !authToken.equals("mock_auth_token")
                && phoneNumber != null && !phoneNumber.isBlank();
    }
}
