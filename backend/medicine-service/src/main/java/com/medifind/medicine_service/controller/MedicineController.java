package com.medifind.medicine_service.controller;

import com.medifind.medicine_service.dto.AlternativeWithStockDTO;
import com.medifind.medicine_service.dto.MedicineRequestDTO;
import com.medifind.medicine_service.dto.MedicineResponseDTO;
import com.medifind.medicine_service.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    /**
     * GET /api/medicines/{id}
     * Purpose: Fetches pure medicine details (Used by both Patients and Pharmacists).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(@PathVariable Long id)
    {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    /**
     * GET /api/medicines/search?name={searchTerm}
     * Purpose: Used by patients and pharmacists to search the master list by text.
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicineResponseDTO>> searchMedicines(@RequestParam String name)
    {
        return ResponseEntity.ok(medicineService.searchMedicines(name));
    }

    /**
     * GET /api/medicines/{id}/alternatives
     * Purpose: Finds other brands that share the exact same generic chemical compound.
     */
    @GetMapping("/{id}/alternatives")
    public ResponseEntity<List<MedicineResponseDTO>> getGenericAlternatives(@PathVariable Long id)
    {
        return ResponseEntity.ok(medicineService.getGenericAlternatives(id));
    }

    /**
     * GET /api/medicines/{id}/alternatives-with-stock?lat={lat}&lng={lng}&radius={radius}
     * Purpose: Finds generic alternative medicines enriched with nearby pharmacy stock information.
     */
    @GetMapping("/{id}/alternatives-with-stock")
    public ResponseEntity<List<AlternativeWithStockDTO>> getGenericAlternativesWithStock(
            @PathVariable Long id,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double radius)
    {
        return ResponseEntity.ok(medicineService.getGenericAlternativesWithStock(id, lat, lng, radius));
    }

    /**
     * POST /api/medicines
     * Purpose: Restrained endpoint to add a new medicine to the master catalog.(ADMIN ONLY)
     */
    @PostMapping
    public ResponseEntity<MedicineResponseDTO> addNewMedicine(@RequestBody MedicineRequestDTO requestDto) {
        MedicineResponseDTO savedMedicine = medicineService.createMedicine(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMedicine);
    }

}
