package com.medifind.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryUpdateDTO {
    private int quantity;
    private double price;
    private int minAlertThreshold;
}
