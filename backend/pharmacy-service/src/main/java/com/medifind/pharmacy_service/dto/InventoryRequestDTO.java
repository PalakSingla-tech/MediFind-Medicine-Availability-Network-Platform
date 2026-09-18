package com.medifind.pharmacy_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryRequestDTO {
    private Long medicineId;
    private int quantity;

    @Positive
    private double price;

    @Min(0)
    private int minAlertThreshold;

}
