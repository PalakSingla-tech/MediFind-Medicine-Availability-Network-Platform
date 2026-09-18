package com.medifind.emergency_service.controller;

import com.medifind.emergency_service.dto.*;
import com.medifind.emergency_service.entity.RequestStatus;
import com.medifind.emergency_service.service.EmergencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmergencyController {

    private final EmergencyService emergencyService;

    /**
     * POST /api/emergency
     * Create emergency request (patient_id, medicine_id, quantity, urgency, location, notes)
     */
    @PostMapping
    public ResponseEntity<EmergencyResponseDTO> createEmergencyRequest(@Valid @RequestBody EmergencyRequestDTO dto) {
        EmergencyResponseDTO created = emergencyService.createEmergencyRequest(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * GET /api/emergency/active
     * Get active emergency requests (for pharmacies to see)
     */
    @GetMapping("/active")
    public ResponseEntity<List<EmergencyResponseDTO>> getActiveRequests() {
        return ResponseEntity.ok(emergencyService.getActiveRequests());
    }

    /**
     * GET /api/emergency/nearby?lat={lat}&lng={lng}&radius={km}
     * Get emergency requests near pharmacy (default 2km, sorted nearest first)
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<EmergencyResponseDTO>> getNearbyRequests(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "2.0") double radius
    ) {
        return ResponseEntity.ok(emergencyService.getNearbyEmergencyRequests(lat, lng, radius));
    }

    /**
     * POST /api/emergency/{id}/respond
     * Pharmacy responds to emergency request ("Available, ready in 30 min")
     */
    @PostMapping("/{id}/respond")
    public ResponseEntity<PharmacyResponseDetailsDTO> respondToRequest(
            @PathVariable("id") Long id,
            @Valid @RequestBody PharmacyRespondRequestDTO dto
    ) {
        PharmacyResponseDetailsDTO response = emergencyService.respondToEmergencyRequest(id, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * PUT /api/emergency/{id}/status
     * Update request status (PENDING -> RESPONDED -> FULFILLED)
     * Supports both JSON body: { "status": "FULFILLED" } or query param: ?status=FULFILLED
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<EmergencyResponseDTO> updateRequestStatus(
            @PathVariable("id") Long id,
            @RequestBody(required = false) StatusUpdateDTO dto,
            @RequestParam(required = false) RequestStatus status
    ) {
        RequestStatus newStatus = (dto != null && dto.getStatus() != null) ? dto.getStatus() : status;
        if (newStatus == null) {
            throw new com.medifind.emergency_service.exception.BadRequestException("Status must be provided in body or query param.");
        }
        return ResponseEntity.ok(emergencyService.updateRequestStatus(id, newStatus));
    }

    /**
     * POST /api/emergency/{id}/accept/{responseId}
     * Patient accepts a pharmacy's response, marking request FULFILLED
     */
    @PostMapping("/{id}/accept/{responseId}")
    public ResponseEntity<EmergencyResponseDTO> acceptResponse(
            @PathVariable("id") Long id,
            @PathVariable("responseId") Long responseId
    ) {
        return ResponseEntity.ok(emergencyService.acceptPharmacyResponse(id, responseId));
    }

    /**
     * GET /api/emergency/patient/{patientId}
     * Get patient's emergency requests
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EmergencyResponseDTO>> getRequestsByPatient(
            @PathVariable("patientId") Long patientId
    ) {
        return ResponseEntity.ok(emergencyService.getRequestsByPatient(patientId));
    }

    /**
     * GET /api/emergency/pharmacy/{pharmacyId}
     * Get pharmacy's responses
     */
    @GetMapping("/pharmacy/{pharmacyId}")
    public ResponseEntity<List<PharmacyResponseDetailsDTO>> getResponsesByPharmacy(
            @PathVariable("pharmacyId") Long pharmacyId
    ) {
        return ResponseEntity.ok(emergencyService.getResponsesByPharmacy(pharmacyId));
    }
}
