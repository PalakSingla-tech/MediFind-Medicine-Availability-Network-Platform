package com.medifind.user_service.client;

import com.medifind.user_service.client.dto.EmergencyRequestDTO;
import com.medifind.user_service.client.dto.EmergencyResponseDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "emergency-service")
public interface EmergencyClient {

    @PostMapping("/api/emergency")
    EmergencyResponseDTO createEmergencyRequest(@Valid @RequestBody EmergencyRequestDTO dto);

    @GetMapping("/api/emergency/patient/{patientId}")
    List<EmergencyResponseDTO> getRequestsByPatient(@PathVariable("patientId") Long patientId);

    @PostMapping("/api/emergency/{id}/accept/{responseId}")
    EmergencyResponseDTO acceptResponse(
            @PathVariable("id") Long id,
            @PathVariable("responseId") Long responseId
    );
}
