package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyResponseDetailsDTO {

    private Long id;
    private Long requestId;
    private Long pharmacyId;
    private String pharmacyName;
    private String pharmacyPhone;
    private String message;
    private String estimatedTime;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
