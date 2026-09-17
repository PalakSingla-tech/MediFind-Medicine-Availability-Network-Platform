package com.medifind.medicine_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Medicines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medId;

    @NotNull(message = "Brand name is required")
    private String name;

    @NotNull(message = "Generic name is required")
    private String genericName;

    @NotNull(message = "Manufacturer is required")
    private String manufacturer;

    @NotNull(message = "Strength is required")
    private String strength;

    @NotNull(message = "Form is required")
    private String form;

    @NotNull(message = "Barcode number is required")
    private int barcode;

    @NotNull(message = "Required Prescription or not is required")
    private String requiresPrescription;

    ArrayList<String> search_keywords;

    private MedicineCategory category;

    public enum MedicineCategory{
        AnalgesicsAndAntipyretics,
        Antibiotics,
        CardiacAndHypertension,
        Gastrointestinal,
        DiabetesCare,
        Respiratory
    }

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status{
        ACTIVE,
        INACTIVE
    }
}
