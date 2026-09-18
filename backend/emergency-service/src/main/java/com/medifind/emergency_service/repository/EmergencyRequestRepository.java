package com.medifind.emergency_service.repository;

import com.medifind.emergency_service.entity.EmergencyRequest;
import com.medifind.emergency_service.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {

    List<EmergencyRequest> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<EmergencyRequest> findByStatusInOrderByCreatedAtDesc(List<RequestStatus> statuses);

    @Query("SELECT r FROM EmergencyRequest r WHERE r.status IN (com.medifind.emergency_service.entity.RequestStatus.PENDING, com.medifind.emergency_service.entity.RequestStatus.RESPONDED) ORDER BY r.createdAt DESC")
    List<EmergencyRequest> findActiveRequests();

    List<EmergencyRequest> findAllByOrderByCreatedAtDesc();
}
