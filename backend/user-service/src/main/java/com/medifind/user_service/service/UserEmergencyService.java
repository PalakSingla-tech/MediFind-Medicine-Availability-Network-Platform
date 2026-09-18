package com.medifind.user_service.service;

import com.medifind.user_service.client.EmergencyClient;
import com.medifind.user_service.client.dto.EmergencyRequestDTO;
import com.medifind.user_service.client.dto.EmergencyResponseDTO;
import com.medifind.user_service.entity.UserProfile;
import com.medifind.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEmergencyService {

    private final EmergencyClient emergencyClient;
    private final UserProfileRepository userProfileRepository;

    public EmergencyResponseDTO createEmergencyRequest(Long authUserId, EmergencyRequestDTO dto) {
        dto.setPatientId(authUserId);

        // Enrich with patient profile location if not provided
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            userProfileRepository.findByAuthUserId(authUserId).ifPresent(profile -> {
                if (dto.getLatitude() == null && profile.getLatitude() != null) {
                    dto.setLatitude(profile.getLatitude());
                }
                if (dto.getLongitude() == null && profile.getLongitude() != null) {
                    dto.setLongitude(profile.getLongitude());
                }
            });
        }

        log.info("Dispatching emergency SOS request for user ID: {} for medicine ID: {}", authUserId, dto.getMedicineId());
        return emergencyClient.createEmergencyRequest(dto);
    }

    public List<EmergencyResponseDTO> getMyEmergencyRequests(Long authUserId) {
        log.info("Fetching emergency requests for user ID: {}", authUserId);
        return emergencyClient.getRequestsByPatient(authUserId);
    }

    public EmergencyResponseDTO acceptPharmacyResponse(Long authUserId, Long requestId, Long responseId) {
        log.info("User {} accepting response {} for emergency request {}", authUserId, responseId, requestId);
        return emergencyClient.acceptResponse(requestId, responseId);
    }
}
