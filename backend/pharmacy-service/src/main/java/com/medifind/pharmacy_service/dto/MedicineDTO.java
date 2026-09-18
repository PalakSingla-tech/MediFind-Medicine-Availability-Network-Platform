package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicineDTO {
    private Long medId;

    private String name;

    private String genericName;

    private String manufacturer;

    private String strength;

    private String form;
}
