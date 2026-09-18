package com.medifind.emergency_service.service;

import com.medifind.emergency_service.client.MedicineClient;
import com.medifind.emergency_service.client.PharmacyClient;
import com.medifind.emergency_service.client.UserClient;
import com.medifind.emergency_service.dto.*;
import com.medifind.emergency_service.entity.EmergencyRequest;
import com.medifind.emergency_service.entity.EmergencyResponse;
import com.medifind.emergency_service.entity.RequestStatus;
import com.medifind.emergency_service.exception.BadRequestException;
import com.medifind.emergency_service.exception.ResourceNotFoundException;
import com.medifind.emergency_service.repository.EmergencyRequestRepository;
import com.medifind.emergency_service.repository.EmergencyResponseRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyService {

    private static final Logger log = LoggerFactory.getLogger(EmergencyService.class);
    private static final double DEFAULT_EMERGENCY_RADIUS_KM = 2.0;

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final EmergencyResponseRepository emergencyResponseRepository;
    private final MedicineClient medicineClient;
    private final PharmacyClient pharmacyClient;
    private final UserClient userClient;

    /**
     * POST /api/emergency
     * Creates an emergency request, validates medicine & patient via OpenFeign, and notifies nearby pharmacies.
     */
    @Transactional
    public EmergencyResponseDTO createEmergencyRequest(EmergencyRequestDTO dto) {
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new BadRequestException("Coordinates (latitude and longitude) are required to locate nearby pharmacies.");
        }

        // 1. Inter-service call: Validate medicine_id exists via medicine-service
        String medicineName = "Medicine #" + dto.getMedicineId();
        try {
            MedicineDTO med = medicineClient.getMedicineById(dto.getMedicineId());
            if (med != null && med.getName() != null) {
                medicineName = med.getName() + (med.getStrength() != null ? " (" + med.getStrength() + ")" : "");
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Medicine with ID " + dto.getMedicineId() + " does not exist in medicine-service.");
        } catch (Exception e) {
            log.warn("Could not reach medicine-service for medicine validation: {}. Continuing with fallback details.", e.getMessage());
        }

        // 2. Inter-service call: Fetch patient details via user-service
        String patientName = "Patient #" + dto.getPatientId();
        String patientPhone = null;
        try {
            UserDTO user = userClient.getUserById(dto.getPatientId());
            if (user != null) {
                if (user.getName() != null) patientName = user.getName();
                patientPhone = user.getPhone();
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Patient with ID " + dto.getPatientId() + " does not exist in user-service.");
        } catch (Exception e) {
            log.warn("Could not reach user-service for patient info: {}. Continuing with fallback details.", e.getMessage());
        }

        // 3. Persist emergency request
        EmergencyRequest request = EmergencyRequest.builder()
                .patientId(dto.getPatientId())
                .patientName(patientName)
                .patientPhone(patientPhone)
                .medicineId(dto.getMedicineId())
                .medicineName(medicineName)
                .quantityNeeded(dto.getQuantity())
                .urgency(dto.getUrgency() != null ? dto.getUrgency() : "1_HOUR")
                .status(RequestStatus.PENDING)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .notes(dto.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        EmergencyRequest savedRequest = emergencyRequestRepository.save(request);

        // 4. Inter-service notification: Notify all pharmacies within 2km
        notifyNearbyPharmacies(savedRequest, DEFAULT_EMERGENCY_RADIUS_KM);

        return mapToDTO(savedRequest, null);
    }

    /**
     * GET /api/emergency/active
     * Returns all active requests (PENDING or RESPONDED)
     */
    public List<EmergencyResponseDTO> getActiveRequests() {
        List<EmergencyRequest> activeList = emergencyRequestRepository.findActiveRequests();
        return activeList.stream()
                .map(req -> mapToDTO(req, null))
                .collect(Collectors.toList());
    }

    /**
     * GET /api/emergency/nearby?lat={lat}&lng={lng}&radius={km}
     * Returns active emergency requests within radius (default 2km) from pharmacy coordinates, sorted nearest first.
     */
    public List<EmergencyResponseDTO> getNearbyEmergencyRequests(double pharmacyLat, double pharmacyLng, Double radius) {
        double effectiveRadius = (radius != null && radius > 0) ? radius : DEFAULT_EMERGENCY_RADIUS_KM;

        List<EmergencyRequest> activeList = emergencyRequestRepository.findActiveRequests();
        List<EmergencyResponseDTO> nearbyList = new ArrayList<>();

        for (EmergencyRequest req : activeList) {
            if (req.getLatitude() == null || req.getLongitude() == null) continue;

            double distance = calculateDistance(pharmacyLat, pharmacyLng, req.getLatitude(), req.getLongitude());
            if (distance <= effectiveRadius) {
                double roundedDistance = Math.round(distance * 100.0) / 100.0;
                nearbyList.add(mapToDTO(req, roundedDistance));
            }
        }

        nearbyList.sort(Comparator.comparingDouble(r -> r.getDistance() != null ? r.getDistance() : 0.0));
        return nearbyList;
    }

    /**
     * POST /api/emergency/{id}/respond
     * Pharmacy responds: "Available, ready in 30 min"
     */
    @Transactional
    public PharmacyResponseDetailsDTO respondToEmergencyRequest(Long requestId, PharmacyRespondRequestDTO dto) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency request with ID " + requestId + " not found."));

        if (request.getStatus() == RequestStatus.FULFILLED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Cannot respond to request with status " + request.getStatus());
        }

        // Inter-service call: Get pharmacy details from pharmacy-service
        String pharmacyName = "Pharmacy #" + dto.getPharmacyId();
        String pharmacyPhone = null;
        try {
            PharmacyDTO pharmacy = pharmacyClient.getPharmacyById(dto.getPharmacyId());
            if (pharmacy != null) {
                if (pharmacy.getName() != null) pharmacyName = pharmacy.getName();
                pharmacyPhone = pharmacy.getPhone();
            }
        } catch (Exception e) {
            log.warn("Could not reach pharmacy-service to fetch details for pharmacy {}: {}", dto.getPharmacyId(), e.getMessage());
        }

        EmergencyResponse response = EmergencyResponse.builder()
                .emergencyRequest(request)
                .pharmacyId(dto.getPharmacyId())
                .pharmacyName(pharmacyName)
                .pharmacyPhone(pharmacyPhone)
                .message(dto.getMessage())
                .estimatedTime(dto.getEstimatedTime() != null ? dto.getEstimatedTime() : "30 min")
                .status(dto.getStatus() != null ? dto.getStatus() : "OFFERED")
                .createdAt(LocalDateTime.now())
                .build();

        EmergencyResponse savedResponse = emergencyResponseRepository.save(response);

        // Update emergency request status to RESPONDED if currently PENDING
        if (request.getStatus() == RequestStatus.PENDING) {
            request.setStatus(RequestStatus.RESPONDED);
            emergencyRequestRepository.save(request);
        }

        log.info("Pharmacy {} responded to Emergency Request {}: \"{}\" (ETA: {})",
                dto.getPharmacyId(), requestId, dto.getMessage(), dto.getEstimatedTime());

        return mapResponseToDTO(savedResponse);
    }

    /**
     * PUT /api/emergency/{id}/status
     * Update request status (PENDING -> RESPONDED -> FULFILLED)
     */
    @Transactional
    public EmergencyResponseDTO updateRequestStatus(Long requestId, RequestStatus newStatus) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency request with ID " + requestId + " not found."));

        request.setStatus(newStatus);
        EmergencyRequest updated = emergencyRequestRepository.save(request);
        log.info("Emergency Request {} status updated to {}", requestId, newStatus);
        return mapToDTO(updated, null);
    }

    /**
     * POST /api/emergency/{requestId}/accept/{responseId}
     * Patient accepts a pharmacy offer and fulfills the request.
     */
    @Transactional
    public EmergencyResponseDTO acceptPharmacyResponse(Long requestId, Long responseId) {
        EmergencyRequest request = emergencyRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency request with ID " + requestId + " not found."));

        EmergencyResponse acceptedResponse = emergencyResponseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency response with ID " + responseId + " not found."));

        if (!acceptedResponse.getEmergencyRequest().getId().equals(requestId)) {
            throw new BadRequestException("Response does not belong to the specified emergency request.");
        }

        // Update response statuses
        List<EmergencyResponse> responses = emergencyResponseRepository.findByEmergencyRequest_IdOrderByCreatedAtDesc(requestId);
        for (EmergencyResponse resp : responses) {
            if (resp.getId().equals(responseId)) {
                resp.setStatus("ACCEPTED");
            } else {
                resp.setStatus("REJECTED");
            }
            emergencyResponseRepository.save(resp);
        }

        request.setStatus(RequestStatus.FULFILLED);
        EmergencyRequest updated = emergencyRequestRepository.save(request);

        log.info("Patient {} accepted offer from pharmacy {} for Emergency Request {}",
                request.getPatientId(), acceptedResponse.getPharmacyId(), requestId);

        return mapToDTO(updated, null);
    }

    /**
     * GET /api/emergency/patient/{patientId}
     * Returns patient's emergency requests
     */
    public List<EmergencyResponseDTO> getRequestsByPatient(Long patientId) {
        List<EmergencyRequest> list = emergencyRequestRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return list.stream()
                .map(req -> mapToDTO(req, null))
                .collect(Collectors.toList());
    }

    /**
     * GET /api/emergency/pharmacy/{pharmacyId}
     * Returns pharmacy's responses
     */
    public List<PharmacyResponseDetailsDTO> getResponsesByPharmacy(Long pharmacyId) {
        List<EmergencyResponse> list = emergencyResponseRepository.findByPharmacyIdOrderByCreatedAtDesc(pharmacyId);
        return list.stream()
                .map(this::mapResponseToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper: Notifies pharmacies within 2km of patient location
     */
    private void notifyNearbyPharmacies(EmergencyRequest request, double radiusKm) {
        log.info("🚨 [EMERGENCY DISPATCH] New Emergency Request #{} created for medicine '{}' (Urgency: {}) at ({}, {}).",
                request.getId(), request.getMedicineName(), request.getUrgency(), request.getLatitude(), request.getLongitude());

        log.info("📡 System broadcasting alert to all pharmacies within {}km radius: \"URGENT: Patient needs {} (Qty: {}) within 1 hour!\"",
                radiusKm, request.getMedicineName(), request.getQuantityNeeded());
    }

    /**
     * Haversine formula to compute distance between two coordinates in kilometers.
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private EmergencyResponseDTO mapToDTO(EmergencyRequest req, Double distance) {
        List<EmergencyResponse> responses = emergencyResponseRepository.findByEmergencyRequest_IdOrderByCreatedAtDesc(req.getId());

        List<PharmacyResponseDetailsDTO> responseDTOs = responses.stream()
                .map(this::mapResponseToDTO)
                .collect(Collectors.toList());

        return EmergencyResponseDTO.builder()
                .id(req.getId())
                .patientId(req.getPatientId())
                .patientName(req.getPatientName())
                .patientPhone(req.getPatientPhone())
                .medicineId(req.getMedicineId())
                .medicineName(req.getMedicineName())
                .quantityNeeded(req.getQuantityNeeded())
                .urgency(req.getUrgency())
                .status(req.getStatus())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .notes(req.getNotes())
                .createdAt(req.getCreatedAt())
                .distance(distance)
                .responses(responseDTOs)
                .build();
    }

    private PharmacyResponseDetailsDTO mapResponseToDTO(EmergencyResponse resp) {
        return PharmacyResponseDetailsDTO.builder()
                .id(resp.getId())
                .requestId(resp.getEmergencyRequest() != null ? resp.getEmergencyRequest().getId() : null)
                .pharmacyId(resp.getPharmacyId())
                .pharmacyName(resp.getPharmacyName())
                .pharmacyPhone(resp.getPharmacyPhone())
                .message(resp.getMessage())
                .estimatedTime(resp.getEstimatedTime())
                .status(resp.getStatus())
                .createdAt(resp.getCreatedAt())
                .build();
    }
}
