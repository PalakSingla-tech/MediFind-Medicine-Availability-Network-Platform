package com.medifind.medicine_service.dto;

import com.medifind.medicine_service.entity.Medicines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicineRequestDTO {
    private String name;

    private String genericName;

    private String manufacturer;

    private String strength;

    private String form;

    private int barcode;

    private String requiresPrescription;

    ArrayList<String> search_keywords;

    private Medicines.MedicineCategory category;

    private Medicines.Status status;
}
