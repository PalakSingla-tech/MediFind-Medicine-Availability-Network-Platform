package com.medifind.pharmacy_service.service;

import com.medifind.pharmacy_service.dto.NearbyPharmacyResponseDTO;
import com.medifind.pharmacy_service.dto.PharmacyAverageRatingDTO;
import com.medifind.pharmacy_service.dto.PharmacyRatingRequestDTO;
import com.medifind.pharmacy_service.dto.PharmacyRatingResponseDTO;
import com.medifind.pharmacy_service.dto.PharmacyRequestDTO;
import com.medifind.pharmacy_service.dto.PharmacyResponseDTO;
import com.medifind.pharmacy_service.dto.PharmacyUpdateDTO;
import com.medifind.pharmacy_service.entity.Pharmacy;
import com.medifind.pharmacy_service.entity.PharmacyRating;
import com.medifind.pharmacy_service.entity.VerificationStatus;
import com.medifind.pharmacy_service.mapper.PharmacyMapper;
import com.medifind.pharmacy_service.repository.PharmacyRatingRepository;
import com.medifind.pharmacy_service.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PharmacyMapper pharmacyMapper;
    private final PharmacyRatingRepository pharmacyRatingRepository;

    public String registerPharmacy(Long ownerId, String role, PharmacyRequestDTO dto) {
        if (!"PHARMACY_OWNER".equalsIgnoreCase(role) && !"ROLE_PHARMACY_OWNER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Pharmacy Owners can register a pharmacy!");
        }

        Pharmacy ph = pharmacyMapper.toEntity(ownerId, dto);
        ph.setVerificationStatus(VerificationStatus.PENDING);
        ph.setVerified(false);
        pharmacyRepository.save(ph);

        return "Pharmacy registered successfully! Awaiting admin verification.";
    }

    public PharmacyResponseDTO getPharmacyById(Long ownerId, String role, Long pharmacyId) {
        if (!"PHARMACY_OWNER".equalsIgnoreCase(role) && !"ROLE_PHARMACY_OWNER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Pharmacy Owners or Admin can access this pharmacy!");
        }

        Pharmacy ph = pharmacyRepository.findById(pharmacyId).orElseThrow(
                () -> new RuntimeException("Pharmacy doesn't exist")
        );

        if (!"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role) && !ph.getOwnerId().equals(ownerId)) {
            throw new RuntimeException(
                    "You are not authorized to access this pharmacy"
            );
        }

        PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(ph);
        enrichWithRating(dto, ph.getPharmacyId());
        return dto;
    }

    public PharmacyResponseDTO getPharmacyByOwnerId(Long ownerId, String role) {
        if (!"PHARMACY_OWNER".equalsIgnoreCase(role) && !"ROLE_PHARMACY_OWNER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Pharmacy Owners can access their pharmacy!");
        }

        Pharmacy pharmacy = pharmacyRepository.findByOwnerId(ownerId).orElseThrow(
                () -> new RuntimeException("Pharmacy doesn't exist. Please register one!")
        );

        PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(pharmacy);
        enrichWithRating(dto, pharmacy.getPharmacyId());
        return dto;
    }

    public PharmacyResponseDTO updatePharmacyDetailsById(Long ownerId, String role, Long pharmacyId, PharmacyUpdateDTO dto) {
        if (!"PHARMACY_OWNER".equalsIgnoreCase(role) && !"ROLE_PHARMACY_OWNER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException(
                    "Only Pharmacy Owners or Admin can update a pharmacy"
            );
        }

        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() ->
                        new RuntimeException("Pharmacy doesn't exist")
                );

        if (!"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role) && !pharmacy.getOwnerId().equals(ownerId)) {
            throw new RuntimeException(
                    "You are not authorized to update this pharmacy"
            );
        }

        pharmacyMapper.updateEntity(pharmacy, dto);

        pharmacyRepository.save(pharmacy);

        PharmacyResponseDTO responseDTO = pharmacyMapper.toResponseDto(pharmacy);
        enrichWithRating(responseDTO, pharmacy.getPharmacyId());
        return responseDTO;
    }

    public List<PharmacyResponseDTO> getPharmaciesByCity(String city) {
        List<Pharmacy> pharmacyList = pharmacyRepository.findByCityIgnoreCaseAndVerificationStatus(city, VerificationStatus.VERIFIED);

        return pharmacyList.stream()
                .map(ph -> {
                    PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(ph);
                    enrichWithRating(dto, ph.getPharmacyId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PharmacyResponseDTO> getPharmaciesHavingMedicine(Long medicineId) {
        List<Pharmacy> pharmacyList = pharmacyRepository.findPharmaciesHavingMedicine(medicineId);
        return pharmacyList.stream()
                .filter(ph -> ph.getVerificationStatus() == VerificationStatus.VERIFIED)
                .map(ph -> {
                    PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(ph);
                    enrichWithRating(dto, ph.getPharmacyId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<NearbyPharmacyResponseDTO> getNearbyPharmacies(double lat, double lng, Long medicineId, Double radius) {
        List<NearbyPharmacyResponseDTO> nearbyPharmacies = new ArrayList<>();
        double effectiveRadius = (radius != null && radius > 0) ? radius : 5.0;

        if (medicineId != null) {
            List<Object[]> results = pharmacyRepository.findNearbyPharmacies(medicineId);
            if (results == null || results.isEmpty()) {
                results = pharmacyRepository.findPharmaciesWithInventoryHavingMedicine(medicineId);
            }
            if (results != null) {
                for (Object[] row : results) {
                    Pharmacy pharmacy = (Pharmacy) row[0];
                    double price = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
                    int quantity = row[2] != null ? ((Number) row[2]).intValue() : 0;

                    if (pharmacy.getLatitude() == null || pharmacy.getLongitude() == null) {
                        continue;
                    }

                    // Exclude unverified pharmacies
                    if (pharmacy.getVerificationStatus() != null && pharmacy.getVerificationStatus() != VerificationStatus.VERIFIED) {
                        continue;
                    }

                    double distance = calculateDistance(lat, lng, pharmacy.getLatitude(), pharmacy.getLongitude());

                    if (distance <= effectiveRadius) {
                        double roundedDistance = Math.round(distance * 100.0) / 100.0;

                        Double avg = pharmacyRatingRepository.getAverageRatingByPharmacyId(pharmacy.getPharmacyId());
                        Long count = pharmacyRatingRepository.countRatingsByPharmacyId(pharmacy.getPharmacyId());

                        NearbyPharmacyResponseDTO dto = NearbyPharmacyResponseDTO.builder()
                                .pharmacyId(pharmacy.getPharmacyId())
                                .name(pharmacy.getName())
                                .pharmacyType(pharmacy.getPharmacyType())
                                .contactPersonName(pharmacy.getContactPersonName())
                                .phone(pharmacy.getPhone())
                                .email(pharmacy.getEmail())
                                .address(pharmacy.getAddress())
                                .city(pharmacy.getCity())
                                .state(pharmacy.getState())
                                .pincode(pharmacy.getPincode())
                                .latitude(pharmacy.getLatitude())
                                .longitude(pharmacy.getLongitude())
                                .isVerified(pharmacy.isVerified())
                                .verificationStatus(pharmacy.getVerificationStatus())
                                .description(pharmacy.getDescription())
                                .medicineId(medicineId)
                                .price(price)
                                .quantity(quantity)
                                .quantityAvailable(quantity)
                                .distance(roundedDistance)
                                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                                .totalRatings(count != null ? count : 0L)
                                .build();

                        nearbyPharmacies.add(dto);
                    }
                }
            }
        } else {
            // medicineId is null: return all nearby verified pharmacies
            List<Pharmacy> verifiedPharmacies = pharmacyRepository.findByVerificationStatus(VerificationStatus.VERIFIED);
            for (Pharmacy pharmacy : verifiedPharmacies) {
                if (pharmacy.getLatitude() == null || pharmacy.getLongitude() == null) {
                    continue;
                }
                double distance = calculateDistance(lat, lng, pharmacy.getLatitude(), pharmacy.getLongitude());
                if (distance <= effectiveRadius) {
                    double roundedDistance = Math.round(distance * 100.0) / 100.0;

                    Double avg = pharmacyRatingRepository.getAverageRatingByPharmacyId(pharmacy.getPharmacyId());
                    Long count = pharmacyRatingRepository.countRatingsByPharmacyId(pharmacy.getPharmacyId());

                    NearbyPharmacyResponseDTO dto = NearbyPharmacyResponseDTO.builder()
                            .pharmacyId(pharmacy.getPharmacyId())
                            .name(pharmacy.getName())
                            .pharmacyType(pharmacy.getPharmacyType())
                            .contactPersonName(pharmacy.getContactPersonName())
                            .phone(pharmacy.getPhone())
                            .email(pharmacy.getEmail())
                            .address(pharmacy.getAddress())
                            .city(pharmacy.getCity())
                            .state(pharmacy.getState())
                            .pincode(pharmacy.getPincode())
                            .latitude(pharmacy.getLatitude())
                            .longitude(pharmacy.getLongitude())
                            .isVerified(pharmacy.isVerified())
                            .verificationStatus(pharmacy.getVerificationStatus())
                            .description(pharmacy.getDescription())
                            .medicineId(null)
                            .price(0.0)
                            .quantity(0)
                            .quantityAvailable(0)
                            .distance(roundedDistance)
                            .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                            .totalRatings(count != null ? count : 0L)
                            .build();

                    nearbyPharmacies.add(dto);
                }
            }
        }

        nearbyPharmacies.sort(Comparator.comparingDouble(NearbyPharmacyResponseDTO::getDistance));
        return nearbyPharmacies;
    }

    public List<PharmacyResponseDTO> getPendingPharmacies() {
        return pharmacyRepository.findByVerificationStatusOrderByPharmacyIdDesc(VerificationStatus.PENDING)
                .stream()
                .map(ph -> {
                    PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(ph);
                    enrichWithRating(dto, ph.getPharmacyId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PharmacyResponseDTO verifyPharmacyByAdmin(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        pharmacy.setVerificationStatus(VerificationStatus.VERIFIED);
        pharmacy.setVerified(true);
        pharmacy.setRejectionReason(null);
        Pharmacy saved = pharmacyRepository.save(pharmacy);

        PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(saved);
        enrichWithRating(dto, pharmacyId);
        return dto;
    }

    public PharmacyResponseDTO rejectPharmacy(Long pharmacyId, String rejectionReason) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        pharmacy.setVerificationStatus(VerificationStatus.REJECTED);
        pharmacy.setVerified(false);
        pharmacy.setRejectionReason(rejectionReason);
        Pharmacy saved = pharmacyRepository.save(pharmacy);

        PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(saved);
        enrichWithRating(dto, pharmacyId);
        return dto;
    }

    public List<PharmacyResponseDTO> getVerifiedPharmacies() {
        return pharmacyRepository.findByVerificationStatusOrderByPharmacyIdDesc(VerificationStatus.VERIFIED)
                .stream()
                .map(ph -> {
                    PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(ph);
                    enrichWithRating(dto, ph.getPharmacyId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PharmacyResponseDTO verifyPharmacy(Long pharmacyId, String role) {
        if (!"ADMIN".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Admin can verify pharmacies!");
        }

        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        pharmacy.setVerificationStatus(VerificationStatus.VERIFIED);
        pharmacy.setVerified(true);
        pharmacy.setRejectionReason(null);
        Pharmacy saved = pharmacyRepository.save(pharmacy);

        PharmacyResponseDTO dto = pharmacyMapper.toResponseDto(saved);
        enrichWithRating(dto, pharmacyId);
        return dto;
    }

    public PharmacyRatingResponseDTO ratePharmacy(Long pharmacyId, Long patientId, PharmacyRatingRequestDTO dto) {
        if (patientId == null) {
            throw new RuntimeException("Patient ID is required to rate a pharmacy!");
        }
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5!");
        }

        pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        PharmacyRating rating = pharmacyRatingRepository
                .findByPharmacyIdAndPatientId(pharmacyId, patientId)
                .orElse(PharmacyRating.builder()
                        .pharmacyId(pharmacyId)
                        .patientId(patientId)
                        .createdAt(LocalDateTime.now())
                        .build());

        rating.setRating(dto.getRating());
        rating.setComment(dto.getComment());
        rating.setCreatedAt(LocalDateTime.now());

        PharmacyRating saved = pharmacyRatingRepository.save(rating);

        return toRatingResponseDTO(saved);
    }

    public List<PharmacyRatingResponseDTO> getPharmacyRatings(Long pharmacyId) {
        pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        return pharmacyRatingRepository.findByPharmacyIdOrderByCreatedAtDesc(pharmacyId)
                .stream()
                .map(this::toRatingResponseDTO)
                .collect(Collectors.toList());
    }

    public PharmacyAverageRatingDTO getAverageRating(Long pharmacyId) {
        pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy doesn't exist"));

        Double avg = pharmacyRatingRepository.getAverageRatingByPharmacyId(pharmacyId);
        Long count = pharmacyRatingRepository.countRatingsByPharmacyId(pharmacyId);

        double roundedAvg = (avg != null) ? Math.round(avg * 10.0) / 10.0 : 0.0;
        long totalRatings = (count != null) ? count : 0L;

        return PharmacyAverageRatingDTO.builder()
                .pharmacyId(pharmacyId)
                .averageRating(roundedAvg)
                .totalRatings(totalRatings)
                .build();
    }

    private PharmacyRatingResponseDTO toRatingResponseDTO(PharmacyRating r) {
        return PharmacyRatingResponseDTO.builder()
                .id(r.getId())
                .pharmacyId(r.getPharmacyId())
                .patientId(r.getPatientId())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private void enrichWithRating(PharmacyResponseDTO dto, Long pharmacyId) {
        Double avg = pharmacyRatingRepository.getAverageRatingByPharmacyId(pharmacyId);
        Long count = pharmacyRatingRepository.countRatingsByPharmacyId(pharmacyId);
        dto.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        dto.setTotalRatings(count != null ? count : 0L);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
