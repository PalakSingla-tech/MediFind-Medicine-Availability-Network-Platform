package com.medifind.pharmacy_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "pharmacies")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pharmacyId;

    private Long ownerId;

    // Basic pharmacy details
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PharmacyType pharmacyType;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private String licenseDocumentUrl;

    private LocalDate licenseExpiryDate;

    // Contact details
    @Column(nullable = false)
    private String contactPersonName;

    @Column(nullable = false)
    private String phone;

    private String email;

    // Address
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pincode;

    // Location
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // Additional details
    private String gstin;

    private String pharmacistName;

    private String pharmacistRegistrationNumber;

    @Column(length = 1000)
    private String description;

    // Verification
    @Builder.Default
    private boolean isVerified = false;

    public enum PharmacyType {
        RETAIL,
        WHOLESALE,
        RETAIL_AND_WHOLESALE
    }
}
