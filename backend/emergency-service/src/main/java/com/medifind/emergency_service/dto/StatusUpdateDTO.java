package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.medifind.emergency_service.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusUpdateDTO {

    @NotNull(message = "Status is required")
    private RequestStatus status;
}
