package com.medifind.pharmacy_service.service;

import com.medifind.pharmacy_service.client.MedicineClient;
import com.medifind.pharmacy_service.dto.InventoryRequestDTO;
import com.medifind.pharmacy_service.dto.InventoryResponseDTO;
import com.medifind.pharmacy_service.dto.InventoryUpdateDTO;
import com.medifind.pharmacy_service.entity.Inventory;
import com.medifind.pharmacy_service.entity.Pharmacy;
import com.medifind.pharmacy_service.mapper.InventoryMapper;
import com.medifind.pharmacy_service.repository.InventoryRepository;
import com.medifind.pharmacy_service.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final PharmacyRepository pharmacyRepository;
    private final MedicineClient medicineClient;


    // =====================================================
    // GET PHARMACY INVENTORY
    // =====================================================

    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getPharmacyInventory(
            Long ownerUserId
    ) {

        Pharmacy pharmacy = getPharmacyByOwnerId(ownerUserId);

        Long pharmacyId = pharmacy.getPharmacyId();

        List<Inventory> inventoryList =
                inventoryRepository.findByPharmacyId(pharmacyId);

        return inventoryList.stream()
                .map(inventory -> {

                    InventoryResponseDTO dto =
                            inventoryMapper.toResponseDTO(inventory);

                    // Fetch centralized medicine details
                    dto.setMedicine(
                            medicineClient.getMedicineById(
                                    inventory.getMedicineId()
                            )
                    );

                    // Fetch generic alternatives
                    dto.setGenericAlternatives(
                            medicineClient.getGenericAlternatives(
                                    inventory.getMedicineId()
                            )
                    );

                    return dto;
                })
                .toList();
    }


    // =====================================================
    // ADD INVENTORY
    // =====================================================

    @Transactional
    public InventoryResponseDTO addInventory(
            Long ownerUserId,
            String role,
            InventoryRequestDTO dto
    ) {

        validatePharmacyOwner(role);

        Pharmacy pharmacy = getPharmacyByOwnerId(ownerUserId);

        Long pharmacyId = pharmacy.getPharmacyId();

        // Verify that the medicine exists in medicine-service
        medicineClient.getMedicineById(dto.getMedicineId());

        // Prevent duplicate medicine in the same pharmacy
        if (inventoryRepository.existsByPharmacyIdAndMedicineId(
                pharmacyId,
                dto.getMedicineId()
        )) {
            throw new RuntimeException(
                    "Medicine already exists in inventory!"
            );
        }

        Inventory inventory = inventoryMapper.toEntity(
                pharmacyId,
                dto
        );

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponseDTO(savedInventory);
    }


    // =====================================================
    // UPDATE INVENTORY
    // =====================================================

    @Transactional
    public InventoryResponseDTO updateInventoryRecord(Long ownerUserId, String role, Long inventoryId, InventoryUpdateDTO dto)
    {
        validatePharmacyOwner(role);

        Pharmacy pharmacy = getPharmacyByOwnerId(ownerUserId);

        Inventory inventory = inventoryRepository.findById(inventoryId).orElseThrow(
                () -> new RuntimeException("Inventory doesn't exist!")
        );

        validateInventoryOwnership(inventory, pharmacy);

        inventory.setQuantity(dto.getQuantity());
        inventory.setPrice(dto.getPrice());
        inventory.setMinAlertThreshold(
                dto.getMinAlertThreshold()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return inventoryMapper.toResponseDTO(updatedInventory);
    }


    // =====================================================
    // DELETE INVENTORY
    // =====================================================

    @Transactional
    public void deleteInventory(
            Long ownerUserId,
            String role,
            Long inventoryId
    ) {

        validatePharmacyOwner(role);

        Pharmacy pharmacy = getPharmacyByOwnerId(ownerUserId);

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory doesn't exist!"
                        )
                );

        validateInventoryOwnership(inventory, pharmacy);

        inventoryRepository.delete(inventory);
    }


    // =====================================================
    // HELPER METHODS
    // =====================================================

    private Pharmacy getPharmacyByOwnerId(Long ownerUserId) {

        return pharmacyRepository.findByOwnerId(ownerUserId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Pharmacy doesn't exist. Please register one!"
                        )
                );
    }


    private void validatePharmacyOwner(String role) {

        if (!"PHARMACY_OWNER".equals(role)) {
            throw new RuntimeException(
                    "You are not authorized to perform this operation"
            );
        }
    }


    private void validateInventoryOwnership(
            Inventory inventory,
            Pharmacy pharmacy
    ) {

        if (!inventory.getPharmacyId()
                .equals(pharmacy.getPharmacyId())) {

            throw new RuntimeException(
                    "You are not authorized to modify this inventory"
            );
        }
    }
}