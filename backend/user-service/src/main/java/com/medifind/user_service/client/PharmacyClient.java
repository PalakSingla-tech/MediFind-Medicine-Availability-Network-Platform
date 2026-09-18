package com.medifind.user_service.client;

import com.medifind.user_service.client.dto.NearbyPharmacyResponseDTO;
import com.medifind.user_service.client.dto.PharmacyRatingRequestDTO;
import com.medifind.user_service.client.dto.PharmacyRatingResponseDTO;
import com.medifind.user_service.client.dto.PharmacyResponseDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "pharmacy-service")
public interface PharmacyClient {

    @GetMapping("/api/pharmacy/{id}")
    PharmacyResponseDTO getPharmacyById(
            @RequestHeader(value = "X-User-Id", required = false) Long ownerId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable("id") Long id
    );

    @GetMapping("/api/pharmacy/city/{city}")
    List<PharmacyResponseDTO> getPharmaciesByCity(@PathVariable("city") String city);

    @GetMapping("/api/pharmacy/nearby")
    List<NearbyPharmacyResponseDTO> getNearbyPharmacies(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam("medicineId") Long medicineId,
            @RequestParam(value = "radius", required = false, defaultValue = "5.0") double radius
    );

    @PostMapping("/api/pharmacy/{id}/rating")
    PharmacyRatingResponseDTO ratePharmacy(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long patientId,
            @Valid @RequestBody PharmacyRatingRequestDTO dto
    );

    @GetMapping("/api/pharmacy/{id}/ratings")
    List<PharmacyRatingResponseDTO> getPharmacyRatings(@PathVariable("id") Long id);
}
