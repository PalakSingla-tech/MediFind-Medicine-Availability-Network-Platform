package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyDTO {

    @JsonAlias({"id", "pharmacyId", "pharmacy_id"})
    private Long pharmacyId;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    @JsonAlias({"contactPersonName", "contact_person_name"})
    private String contactPersonName;

    @JsonAlias({"isVerified", "verified"})
    private Boolean isVerified;
}
