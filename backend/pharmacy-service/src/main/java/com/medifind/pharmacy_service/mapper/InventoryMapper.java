package com.medifind.pharmacy_service.mapper;

import com.medifind.pharmacy_service.dto.InventoryRequestDTO;
import com.medifind.pharmacy_service.dto.InventoryResponseDTO;
import com.medifind.pharmacy_service.entity.Inventory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InventoryMapper {
    public InventoryResponseDTO toResponseDTO(Inventory inventory) {

        return InventoryResponseDTO.builder()
                .inventoryId(inventory.getInventoryId())
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice())
                .minAlertThreshold(inventory.getMinAlertThreshold())
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }

    public Inventory toEntity(Long pharmacyId, InventoryRequestDTO dto)
    {
        return Inventory.builder()
                .pharmacyId(pharmacyId)
                .medicineId(dto.getMedicineId())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .minAlertThreshold(dto.getMinAlertThreshold())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
