package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyRatingResponseDTO {

    private Long id;
    private Long pharmacyId;
    private Long patientId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
