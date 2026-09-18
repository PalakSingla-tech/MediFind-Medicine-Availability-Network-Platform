package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medifind.emergency_service.entity.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private String patientPhone;

    private Long medicineId;
    private String medicineName;

    private Integer quantityNeeded;
    private String urgency;
    private RequestStatus status;

    private Double latitude;
    private Double longitude;
    private String notes;

    private Double distance; // in km, populated when querying nearby

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder.Default
    private List<PharmacyResponseDetailsDTO> responses = new ArrayList<>();
}
