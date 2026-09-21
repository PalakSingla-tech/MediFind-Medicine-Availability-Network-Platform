package com.medifind.pharmacy_service.dto;

import com.medifind.pharmacy_service.entity.Pharmacy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PharmacyResponseDTO {
    private Long pharmacyId;
    private String name;
    private Pharmacy.PharmacyType pharmacyType;
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
    private com.medifind.pharmacy_service.entity.VerificationStatus verificationStatus;
    private String rejectionReason;

    private Double averageRating;
    private Long totalRatings;
}
