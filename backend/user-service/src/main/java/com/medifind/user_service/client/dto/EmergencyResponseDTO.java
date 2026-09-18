package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyResponseDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;

    private Long medicineId;
    private String medicineName;

    private Integer quantityNeeded;
    private String urgency;
    private String status;

    private Double latitude;
    private Double longitude;
    private String notes;

    private Double distance;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder.Default
    private List<PharmacyResponseDetailsDTO> responses = new ArrayList<>();
}
