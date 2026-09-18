package com.medifind.user_service.service;

import com.medifind.user_service.client.MedicineClient;
import com.medifind.user_service.client.PharmacyClient;
import com.medifind.user_service.client.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPharmacyInteractionService {

    private final PharmacyClient pharmacyClient;
    private final MedicineClient medicineClient;

    public PharmacyRatingResponseDTO ratePharmacy(Long authUserId, Long pharmacyId, PharmacyRatingRequestDTO dto) {
        log.info("User {} rating pharmacy {} with score {}", authUserId, pharmacyId, dto.getRating());
        return pharmacyClient.ratePharmacy(pharmacyId, authUserId, dto);
    }

    public List<PharmacyRatingResponseDTO> getPharmacyRatings(Long pharmacyId) {
        return pharmacyClient.getPharmacyRatings(pharmacyId);
    }

    public PharmacyResponseDTO getPharmacyDetails(Long pharmacyId) {
        return pharmacyClient.getPharmacyById(null, "PATIENT", pharmacyId);
    }

    public List<NearbyPharmacyResponseDTO> findNearbyPharmacies(double lat, double lng, Long medicineId, double radius) {
        log.info("User searching nearby pharmacies for medicine {} at ({}, {}) within {}km", medicineId, lat, lng, radius);
        return pharmacyClient.getNearbyPharmacies(lat, lng, medicineId, radius);
    }

    public List<MedicineResponseDTO> searchMedicines(String name) {
        log.info("User searching medicines with keyword: {}", name);
        return medicineClient.searchMedicines(name);
    }

    public List<AlternativeWithStockDTO> getGenericAlternativesWithStock(Long medicineId, double lat, double lng, Double radius) {
        log.info("User fetching generic alternatives with stock for medicine {} at ({}, {}) within {}km", medicineId, lat, lng, radius);
        return medicineClient.getGenericAlternativesWithStock(medicineId, lat, lng, radius);
    }
}
