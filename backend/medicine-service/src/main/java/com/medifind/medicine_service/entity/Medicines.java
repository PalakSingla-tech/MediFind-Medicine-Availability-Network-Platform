package com.medifind.medicine_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medicine_search_keywords", joinColumns = @JoinColumn(name = "med_id"))
    @Column(name = "keyword")
    @Builder.Default
    private List<String> search_keywords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
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