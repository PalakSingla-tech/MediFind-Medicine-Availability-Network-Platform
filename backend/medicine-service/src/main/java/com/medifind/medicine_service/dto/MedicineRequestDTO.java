package com.medifind.medicine_service.dto;

import com.medifind.medicine_service.entity.Medicines;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotBlank(message = "Brand name is required")
    private String name;

    @NotBlank(message = "Generic name is required")
    private String genericName;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Strength is required")
    private String strength;

    @NotBlank(message = "Form is required")
    private String form;

    @PositiveOrZero(message = "Barcode must be a positive number or zero")
    private int barcode;

    @NotBlank(message = "Required Prescription or not is required")
    private String requiresPrescription;

    @Builder.Default
    private ArrayList<String> search_keywords = new ArrayList<>();

    private Medicines.MedicineCategory category;

    private Medicines.Status status;
}
