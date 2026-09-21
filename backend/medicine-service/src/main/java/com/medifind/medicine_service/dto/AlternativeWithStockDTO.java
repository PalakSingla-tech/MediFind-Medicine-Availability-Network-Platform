package com.medifind.medicine_service.dto;

import com.medifind.medicine_service.entity.Medicines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlternativeWithStockDTO {

    // Flat medicine details matching MedicineResponseDTO for convenience
    private Long medId;

    private String name;

    private String genericName;

    private String manufacturer;

    private String strength;

    private String form;

    private int barcode;

    private String requiresPrescription;

    private ArrayList<String> search_keywords;

    private Medicines.MedicineCategory category;

    private Medicines.Status status;

    // Nested medicine DTO for clients accessing via alt.getMedicine()
    private MedicineResponseDTO medicine;

    // Stock & availability details
    private boolean inStock;

    private int totalAvailableStock;

    private Double minPrice;

    private List<NearbyPharmacyStockDTO> pharmacies;
}
