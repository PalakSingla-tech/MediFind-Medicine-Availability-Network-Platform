package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyRespondRequestDTO {

    @NotNull(message = "Pharmacy ID is required")
    @JsonAlias({"pharmacy_id", "pharmacyId"})
    private Long pharmacyId;

    @NotBlank(message = "Message is required")
    private String message;

    @JsonAlias({"estimated_time", "estimatedTime", "eta"})
    private String estimatedTime;

    @Builder.Default
    private String status = "OFFERED";
}
