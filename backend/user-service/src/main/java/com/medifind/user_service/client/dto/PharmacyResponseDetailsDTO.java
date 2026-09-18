package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PharmacyResponseDetailsDTO {
    private Long id;
    private Long emergencyRequestId;
    private Long pharmacyId;
    private String pharmacyName;
    private String pharmacyPhone;
    private String pharmacyAddress;
    private Double price;
    private String responseMessage;
    private Integer readyInMinutes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime respondedAt;
}
