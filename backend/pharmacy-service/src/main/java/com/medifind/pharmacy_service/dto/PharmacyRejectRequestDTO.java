package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyRejectRequestDTO {
    private String reason;
    private String rejectionReason;

    public String getEffectiveReason() {
        if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
            return rejectionReason.trim();
        }
        if (reason != null && !reason.trim().isEmpty()) {
            return reason.trim();
        }
        return null;
    }
}
