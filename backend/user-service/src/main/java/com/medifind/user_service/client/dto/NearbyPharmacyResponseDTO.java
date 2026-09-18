package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NearbyPharmacyResponseDTO {
    private Long pharmacyId;
    private String name;
    private String pharmacyType;

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

    private Long medicineId;
    private double price;
    private int quantity;
    private int quantityAvailable;

    private double distance;
    private Double averageRating;
    private Long totalRatings;
}
