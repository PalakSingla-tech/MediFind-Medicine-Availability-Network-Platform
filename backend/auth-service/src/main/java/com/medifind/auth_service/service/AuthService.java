package com.medifind.auth_service.service;

import com.medifind.auth_service.dto.LoginRequestDTO;
import com.medifind.auth_service.dto.LoginResponseDTO;
import com.medifind.auth_service.dto.SignUpRequestDTO;
import com.medifind.auth_service.dto.SignUpResponseDTO;
import com.medifind.auth_service.entity.User;
import com.medifind.auth_service.repository.UserRepository;
import com.medifind.auth_service.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;

    // Normal USER registration
    public SignUpResponseDTO registerUser(SignUpRequestDTO dto) {
        return register(dto, User.Role.USER);
    }

    // PHARMACY_OWNER registration
    public SignUpResponseDTO registerPharmacyOwner(SignUpRequestDTO dto) {
        return register(dto, User.Role.PHARMACY_OWNER);
    }

    // Common registration logic
    private SignUpResponseDTO register(SignUpRequestDTO dto, User.Role role) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (!Objects.equals(dto.getPassword(), dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords don't match!");
        }

        User user = new User();

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setRole(role);

        user.setAccountStatus(User.AccountStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        return SignUpResponseDTO.builder()
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .build();
    }

    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO)
    {
        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User doesn't exist"));

        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword()))
        {
            throw new RuntimeException("Invalid username or Password");
        }

        String token = authUtil.generateAccessToken(user);

        return LoginResponseDTO.builder()
                .jwt(token)
                .userId(user.getUserId())
                .build();
    }

}
