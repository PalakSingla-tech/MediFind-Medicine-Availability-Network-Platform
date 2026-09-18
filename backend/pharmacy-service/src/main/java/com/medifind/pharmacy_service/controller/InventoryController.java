package com.medifind.pharmacy_service.controller;

import com.medifind.pharmacy_service.dto.InventoryRequestDTO;
import com.medifind.pharmacy_service.dto.InventoryResponseDTO;
import com.medifind.pharmacy_service.dto.InventoryUpdateDTO;
import com.medifind.pharmacy_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pharmacy")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * GET /api/pharmacy/{pharmacyId}/inventory
     * Purpose: Find all the inventory records related to the particular pharmacy id.
     */
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryResponseDTO>> getPharmacyInventory(@RequestHeader("X-User-Id") Long ownerUserId)
    {
        return ResponseEntity.ok(inventoryService.getPharmacyInventory(ownerUserId));
    }

    /**
     * PUT /api/pharmacy/inventory/{inventoryId}
     * Purpose: Update quantity and price of a particular inventory record
     */
    @PutMapping("/inventory/{inventoryId}")
    public ResponseEntity<InventoryResponseDTO> updateInventoryRecord(@RequestHeader("X-User-Id") Long ownerUserId,
                                                                      @RequestHeader("X-User-Role") String role,
                                                                      @PathVariable Long inventoryId,
                                                                      @RequestBody InventoryUpdateDTO dto)
    {
        return ResponseEntity.ok(inventoryService.updateInventoryRecord(ownerUserId, role, inventoryId, dto));
    }

    /**
     * DELETE /api/pharmacy/inventory/{inventoryId}
     * Purpose: Deleting a particular inventory
     */
    @DeleteMapping("/inventory/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@RequestHeader("X-User-Id") Long ownerUserId,
                                                @RequestHeader("X-User-Role") String role,
                                                @PathVariable Long inventoryId)
    {
        inventoryService.deleteInventory(ownerUserId, role, inventoryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/pharmacy/{pharmacyId}/inventory
     * Purpose: Add new inventory record to the particular pharmacy's inventory
     */
    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponseDTO> addInventory(@RequestHeader("X-User-Id") Long ownerUserId,
                                                             @RequestHeader("X-User-Role") String role,
                                                             @RequestBody InventoryRequestDTO dto)
    {
        return ResponseEntity.ok(inventoryService.addInventory(ownerUserId, role, dto));
    }

}
