package com.medifind.user_service.mapper;

import com.medifind.user_service.dto.CreateUserProfileRequestDTO;
import com.medifind.user_service.dto.UpdateUserProfileRequestDTO;
import com.medifind.user_service.dto.UserProfileResponseDTO;
import com.medifind.user_service.entity.UserProfile;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfile toEntity(Long authUserId, CreateUserProfileRequestDTO dto) {
        return UserProfile.builder()
                .authUserId(authUserId)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .profileImage(dto.getProfileImage())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .locationLabel(dto.getLocationLabel())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyContactPhone(dto.getEmergencyContactPhone())
                .bloodGroup(dto.getBloodGroup())
                .allergies(dto.getAllergies())
                .build();
    }

    public UserProfileResponseDTO toDTO(UserProfile entity) {
        return UserProfileResponseDTO.builder()
                .id(entity.getId())
                .authUserId(entity.getAuthUserId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .city(entity.getCity())
                .profileImage(entity.getProfileImage())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .locationLabel(entity.getLocationLabel())
                .emergencyContactName(entity.getEmergencyContactName())
                .emergencyContactPhone(entity.getEmergencyContactPhone())
                .bloodGroup(entity.getBloodGroup())
                .allergies(entity.getAllergies())
                .build();
    }

    public void updateEntity(UserProfile entity, UpdateUserProfileRequestDTO dto) {
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) entity.setAddress(dto.getAddress());
        if (dto.getCity() != null) entity.setCity(dto.getCity());
        if (dto.getProfileImage() != null) entity.setProfileImage(dto.getProfileImage());
        if (dto.getLatitude() != null) entity.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) entity.setLongitude(dto.getLongitude());
        if (dto.getLocationLabel() != null) entity.setLocationLabel(dto.getLocationLabel());
        if (dto.getEmergencyContactName() != null) entity.setEmergencyContactName(dto.getEmergencyContactName());
        if (dto.getEmergencyContactPhone() != null) entity.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        if (dto.getBloodGroup() != null) entity.setBloodGroup(dto.getBloodGroup());
        if (dto.getAllergies() != null) entity.setAllergies(dto.getAllergies());
    }
}
