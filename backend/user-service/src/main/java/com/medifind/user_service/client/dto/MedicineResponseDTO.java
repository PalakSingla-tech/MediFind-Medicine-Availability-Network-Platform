package com.medifind.user_service.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineResponseDTO {
    private Long medId;
    private String name;
    private String genericName;
    private String manufacturer;
    private String strength;
    private String form;
    private int barcode;
    private String requiresPrescription;
    private ArrayList<String> search_keywords;
    private String category;
    private String status;
}
