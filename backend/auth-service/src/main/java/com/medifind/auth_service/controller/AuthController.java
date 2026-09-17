package com.medifind.auth_service.controller;

import com.medifind.auth_service.dto.LoginRequestDTO;
import com.medifind.auth_service.dto.LoginResponseDTO;
import com.medifind.auth_service.dto.SignUpRequestDTO;
import com.medifind.auth_service.dto.SignUpResponseDTO;
import com.medifind.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDTO> registerUser(@RequestBody SignUpRequestDTO signUpRequestDTO)
    {
        return ResponseEntity.ok(authService.registerUser(signUpRequestDTO));
    }

    @PostMapping("/pharmacy-owner/register")
    public ResponseEntity<SignUpResponseDTO> registerPharmacyOwner(
            @RequestBody SignUpRequestDTO signUpRequestDTO) {

        return ResponseEntity.ok(
                authService.registerPharmacyOwner(signUpRequestDTO)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequestDTO)
    {
        return ResponseEntity.ok(authService.loginUser(loginRequestDTO));
    }
}
