package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyAverageRatingDTO {

    private Long pharmacyId;
    private Double averageRating;
    private long totalRatings;
}
