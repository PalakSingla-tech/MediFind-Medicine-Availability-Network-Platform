package com.medifind.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponseDTO {
    private Long id;
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String profileImage;

    private Double latitude;
    private Double longitude;
    private String locationLabel;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bloodGroup;
    private String allergies;
}
