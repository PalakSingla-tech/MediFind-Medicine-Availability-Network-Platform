package com.medifind.user_service.service;

import com.medifind.user_service.dto.CreateUserProfileRequestDTO;
import com.medifind.user_service.dto.UpdateUserProfileRequestDTO;
import com.medifind.user_service.dto.UserProfileResponseDTO;
import com.medifind.user_service.entity.UserProfile;
import com.medifind.user_service.exception.UserProfileAlreadyExistsException;
import com.medifind.user_service.exception.UserProfileNotFoundException;
import com.medifind.user_service.mapper.UserProfileMapper;
import com.medifind.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    // Getting user profile
    public UserProfileResponseDTO getUserProfile(Long authUserId)
    {
        UserProfile up = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User Profile doesn't exist! Kindly create one"));

        return userProfileMapper.toDTO(up);
    }

    // Creating user profile
    public String createUserProfile(Long authUserId, CreateUserProfileRequestDTO dto)
    {
        // Check if profile already exists
        if (userProfileRepository.findByAuthUserId(authUserId).isPresent()) {
            throw new UserProfileAlreadyExistsException(
                    "User Profile already exist!"
            );
        }

        UserProfile savedProfile = userProfileMapper.toEntity(authUserId, dto);
        userProfileRepository.save(savedProfile);

        return "User Profile Created Successfully!";
    }

    // Updating user profile
    public UserProfileResponseDTO updateUserProfile(Long authUserId, UpdateUserProfileRequestDTO dto)
    {
        UserProfile up = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User Profile doesn't exist! Kindly create one"));

        userProfileMapper.updateEntity(up, dto);

        UserProfile updatedProfile = userProfileRepository.save(up);

        return userProfileMapper.toDTO(updatedProfile);

    }
}
