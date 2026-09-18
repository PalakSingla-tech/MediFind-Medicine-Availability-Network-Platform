package com.medifind.emergency_service.repository;

import com.medifind.emergency_service.entity.EmergencyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyResponseRepository extends JpaRepository<EmergencyResponse, Long> {

    List<EmergencyResponse> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);

    List<EmergencyResponse> findByEmergencyRequest_IdOrderByCreatedAtDesc(Long emergencyRequestId);

    Optional<EmergencyResponse> findByEmergencyRequest_IdAndPharmacyId(Long emergencyRequestId, Long pharmacyId);
}
