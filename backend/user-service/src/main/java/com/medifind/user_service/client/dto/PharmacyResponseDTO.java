package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyResponseDTO {
    private Long pharmacyId;
    private String name;
    private String pharmacyType;
    private String licenseNumber;
    private String licenseDocumentUrl;
    private LocalDate licenseExpiryDate;

    private String contactPersonName;
    private String phone;
    private String email;

    private String address;
    private String city;
    private String state;
    private String pincode;

    private Double latitude;
    private Double longitude;

    private String gstin;
    private String pharmacistName;
    private String pharmacistRegistrationNumber;
    private String description;
    private boolean isVerified;

    private Double averageRating;
    private Long totalRatings;
}
