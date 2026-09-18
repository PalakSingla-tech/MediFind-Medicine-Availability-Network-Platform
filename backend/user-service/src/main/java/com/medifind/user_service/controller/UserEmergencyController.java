package com.medifind.user_service.controller;

import com.medifind.user_service.client.dto.EmergencyRequestDTO;
import com.medifind.user_service.client.dto.EmergencyResponseDTO;
import com.medifind.user_service.service.UserEmergencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/emergency")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserEmergencyController {

    private final UserEmergencyService userEmergencyService;

    @PostMapping
    public ResponseEntity<EmergencyResponseDTO> createEmergencyRequest(
            @RequestHeader("X-User-Id") Long authUserId,
            @Valid @RequestBody EmergencyRequestDTO dto
    ) {
        EmergencyResponseDTO created = userEmergencyService.createEmergencyRequest(authUserId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmergencyResponseDTO>> getMyEmergencyRequests(
            @RequestHeader("X-User-Id") Long authUserId
    ) {
        return ResponseEntity.ok(userEmergencyService.getMyEmergencyRequests(authUserId));
    }

    @PostMapping("/{requestId}/accept/{responseId}")
    public ResponseEntity<EmergencyResponseDTO> acceptPharmacyResponse(
            @RequestHeader("X-User-Id") Long authUserId,
            @PathVariable Long requestId,
            @PathVariable Long responseId
    ) {
        return ResponseEntity.ok(userEmergencyService.acceptPharmacyResponse(authUserId, requestId, responseId));
    }
}
