package com.medifind.pharmacy_service.controller;

import com.medifind.pharmacy_service.dto.*;
import com.medifind.pharmacy_service.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pharmacy")
@CrossOrigin(origins = "*")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    /**
     * POST /api/pharmacy
     * Purpose: Register a new pharmacy.
     */
    @PostMapping
    public ResponseEntity<String> registerPharmacy(@RequestHeader("X-User-Id") Long ownerId,
                                                   @RequestHeader("X-User-Role") String role,
                                                   @RequestBody PharmacyRequestDTO dto)
    {
        return ResponseEntity.ok(pharmacyService.registerPharmacy(ownerId, role, dto));
    }

    /**
     * GET /api/pharmacy/{id}
     * Purpose: Fetch a particular pharmacy details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyResponseDTO> getPharmacyById(@RequestHeader("X-User-Id") Long ownerId,
                                                               @RequestHeader("X-User-Role") String role,
                                                               @PathVariable Long id)
    {
        return ResponseEntity.ok(pharmacyService.getPharmacyById(ownerId, role, id));
    }

    /**
     * GET /api/pharmacy/owner/{ownerId}
     * Purpose: Fetch a pharmacy details of a particular owner.
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<PharmacyResponseDTO> getPharmacyByOwnerId(@RequestHeader("X-User-Id") Long ownerId,
                                                                    @RequestHeader("X-User-Role") String role)
    {
        return ResponseEntity.ok(pharmacyService.getPharmacyByOwnerId(ownerId, role));
    }

    /**
     * PUT /api/pharmacy/{id}
     * Purpose: Edit the existing pharmacy details
     */
    @PutMapping("/{id}")
    public ResponseEntity<PharmacyResponseDTO> updatePharmacyDetailsById(@RequestHeader("X-User-Id") Long ownerId,
                                                                         @RequestHeader("X-User-Role") String role,
                                                                         @PathVariable Long id,
                                                                         @RequestBody PharmacyUpdateDTO dto)
    {
        return ResponseEntity.ok(pharmacyService.updatePharmacyDetailsById(ownerId, role, id, dto));
    }

    /**
     * GET /api/pharmacy/city/{city}
     * Purpose: Get Pharmacies by city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<PharmacyResponseDTO>> getPharmaciesByCity(@PathVariable String city)
    {
        return ResponseEntity.ok(pharmacyService.getPharmaciesByCity(city));
    }

    /**
     * GET /api/pharmacy/inventory/medicine/{medicineId}
     * Purpose: Get all the pharmacies having the particular medicine
     */
    @GetMapping("/inventory/medicine/{medicineId}")
    public ResponseEntity<List<PharmacyResponseDTO>> getPharmaciesHavingMedicine(@PathVariable Long medicineId)
    {
        return ResponseEntity.ok(pharmacyService.getPharmaciesHavingMedicine(medicineId));
    }

    /**
     * GET /api/pharmacy/nearby?lat={latitude}&lng={longitude}&medicineId={medicineId}&radius={km}
     * Purpose: Find pharmacies having medicine in stock within radius (default 5km), sorted by nearest first.
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyPharmacyResponseDTO>> getNearbyPharmacies(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam Long medicineId,
            @RequestParam(required = false, defaultValue = "5.0") double radius
    ) {
        return ResponseEntity.ok(pharmacyService.getNearbyPharmacies(lat, lng, medicineId, radius));
    }

    /**
     * PUT /api/pharmacy/{id}/verify
     * Purpose: Admin verifies pharmacy (is_verified = true)
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<PharmacyResponseDTO> verifyPharmacy(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role
    ) {
        return ResponseEntity.ok(pharmacyService.verifyPharmacy(id, role));
    }

    /**
     * POST /api/pharmacy/{id}/rating
     * Purpose: Patient rates pharmacy (rating 1-5, comment)
     */
    @PostMapping("/{id}/rating")
    public ResponseEntity<PharmacyRatingResponseDTO> ratePharmacy(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long patientId,
            @Valid @RequestBody PharmacyRatingRequestDTO dto
    ) {
        return ResponseEntity.ok(pharmacyService.ratePharmacy(id, patientId, dto));
    }

    /**
     * GET /api/pharmacy/{id}/ratings
     * Purpose: Get pharmacy's ratings
     */
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<PharmacyRatingResponseDTO>> getPharmacyRatings(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyRatings(id));
    }

    /**
     * GET /api/pharmacy/{id}/average-rating
     * Purpose: Get pharmacy's average rating and total ratings count
     */
    @GetMapping("/{id}/average-rating")
    public ResponseEntity<PharmacyAverageRatingDTO> getAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getAverageRating(id));
    }

}
