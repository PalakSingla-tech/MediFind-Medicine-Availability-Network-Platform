package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineDTO {

    @JsonAlias({"id", "medId", "medicineId"})
    private Long medId;

    private String name;

    @JsonAlias({"generic_name", "genericName"})
    private String genericName;

    private String manufacturer;

    private String strength;

    @JsonAlias({"dosageForm", "dosage_form", "form"})
    private String form;
}
