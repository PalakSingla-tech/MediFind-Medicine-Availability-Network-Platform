package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponseDTO {
    private Long inventoryId;

    private MedicineDTO medicine;

    private int quantity;

    private double price;

    private int minAlertThreshold;

    private LocalDateTime lastUpdated;

    private List<MedicineDTO> genericAlternatives;
}
