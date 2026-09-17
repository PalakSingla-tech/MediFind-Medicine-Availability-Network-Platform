package com.medifind.auth_service.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponseDTO {
    private Long userId;
    private String username;
}
