package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyRatingResponseDTO {
    private Long id;
    private Long pharmacyId;
    private Long patientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
