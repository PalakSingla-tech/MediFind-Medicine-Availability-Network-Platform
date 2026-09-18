package com.medifind.user_service.controller;

import com.medifind.user_service.client.dto.*;
import com.medifind.user_service.service.UserPharmacyInteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserPharmacyInteractionController {

    private final UserPharmacyInteractionService interactionService;

    @PostMapping("/pharmacies/{pharmacyId}/reviews")
    public ResponseEntity<PharmacyRatingResponseDTO> ratePharmacy(
            @RequestHeader("X-User-Id") Long authUserId,
            @PathVariable Long pharmacyId,
            @Valid @RequestBody PharmacyRatingRequestDTO dto
    ) {
        return ResponseEntity.ok(interactionService.ratePharmacy(authUserId, pharmacyId, dto));
    }

    @GetMapping("/pharmacies/{pharmacyId}/reviews")
    public ResponseEntity<List<PharmacyRatingResponseDTO>> getPharmacyRatings(
            @PathVariable Long pharmacyId
    ) {
        return ResponseEntity.ok(interactionService.getPharmacyRatings(pharmacyId));
    }

    @GetMapping("/pharmacies/{pharmacyId}")
    public ResponseEntity<PharmacyResponseDTO> getPharmacyDetails(
            @PathVariable Long pharmacyId
    ) {
        return ResponseEntity.ok(interactionService.getPharmacyDetails(pharmacyId));
    }

    @GetMapping("/pharmacies/nearby")
    public ResponseEntity<List<NearbyPharmacyResponseDTO>> findNearbyPharmacies(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam Long medicineId,
            @RequestParam(required = false, defaultValue = "5.0") double radius
    ) {
        return ResponseEntity.ok(interactionService.findNearbyPharmacies(lat, lng, medicineId, radius));
    }

    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineResponseDTO>> searchMedicines(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(interactionService.searchMedicines(name));
    }

    @GetMapping("/medicines/{medicineId}/alternatives-with-stock")
    public ResponseEntity<List<AlternativeWithStockDTO>> getGenericAlternativesWithStock(
            @PathVariable Long medicineId,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "5.0") Double radius
    ) {
        return ResponseEntity.ok(interactionService.getGenericAlternativesWithStock(medicineId, lat, lng, radius));
    }
}
