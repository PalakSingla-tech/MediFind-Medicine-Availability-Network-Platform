package com.medifind.user_service.controller;

import com.medifind.user_service.dto.CreateUserProfileRequestDTO;
import com.medifind.user_service.dto.UpdateUserProfileRequestDTO;
import com.medifind.user_service.dto.UserProfileResponseDTO;
import com.medifind.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@RequestHeader("X-User-Id") Long authUserId)
    {
        return ResponseEntity.ok(userProfileService.getUserProfile(authUserId));
    }

    @PostMapping("/profile")
    public ResponseEntity<String> createUserProfile(@RequestHeader("X-User-Id") Long authUserId,
                                                    @RequestBody CreateUserProfileRequestDTO dto)
    {
        return ResponseEntity.ok(userProfileService.createUserProfile(authUserId, dto));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(@RequestHeader("X-User-Id") Long authUserId,
                                                                    @RequestBody UpdateUserProfileRequestDTO dto)
    {
        return ResponseEntity.ok(userProfileService.updateUserProfile(authUserId, dto));
    }

}
