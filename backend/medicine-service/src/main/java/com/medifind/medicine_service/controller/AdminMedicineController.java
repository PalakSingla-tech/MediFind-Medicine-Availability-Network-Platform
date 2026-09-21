package com.medifind.medicine_service.controller;

import com.medifind.medicine_service.dto.MedicineRequestDTO;
import com.medifind.medicine_service.dto.MedicineResponseDTO;
import com.medifind.medicine_service.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/medicines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMedicineController {

    private final MedicineService medicineService;

    /**
     * POST /api/admin/medicines
     * Purpose: Add new medicine to master catalog (ADMIN ONLY)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponseDTO> addMedicine(@Valid @RequestBody MedicineRequestDTO requestDTO) {
        MedicineResponseDTO created = medicineService.createAdminMedicine(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/admin/medicines
     * Purpose: List all medicines with pagination (ADMIN ONLY)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MedicineResponseDTO>> getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "medId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(medicineService.getAllMedicinesPaginated(pageable));
    }

    /**
     * PUT /api/admin/medicines/{id}
     * Purpose: Update medicine by ID (ADMIN ONLY)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequestDTO requestDTO
    ) {
        MedicineResponseDTO updated = medicineService.updateMedicine(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/admin/medicines/{id}
     * Purpose: Delete medicine if not referenced in inventory (ADMIN ONLY)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok(Map.of("message", "Medicine deleted successfully"));
    }
}
