package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    @JsonAlias({"id", "userId", "user_id"})
    private Long id;

    @JsonAlias({"fullName", "full_name", "username"})
    private String name;

    private String email;

    @JsonAlias({"phoneNumber", "phone_number", "contactNumber"})
    private String phone;

    private String role;
}
