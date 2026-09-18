package com.medifind.pharmacy_service.dto;

import com.medifind.pharmacy_service.entity.Pharmacy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NearbyPharmacyResponseDTO {

    private Long pharmacyId;

    private String name;

    private Pharmacy.PharmacyType pharmacyType;

    private String contactPersonName;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    private boolean isVerified;

    private String description;

    // Medicine & inventory details
    private Long medicineId;

    private double price;

    private int quantity;

    private int quantityAvailable;

    // Distance in kilometers (nearest first)
    private double distance;

    // Rating details
    private Double averageRating;
    private Long totalRatings;
}
