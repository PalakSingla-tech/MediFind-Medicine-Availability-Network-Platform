package com.medifind.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_user_id", nullable = false, unique = true)
    private Long authUserId;

    private String firstName;
    private String lastName;

    private String phoneNumber;

    private String address;
    private String city;

    private String profileImage;

    // Location coordinates for patient searches & SOS
    private Double latitude;
    private Double longitude;
    private String locationLabel;

    // Emergency & health details
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bloodGroup;
    private String allergies;
}
