package com.medifind.emergency_service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyRequestDTO {

    @NotNull(message = "Patient ID is required")
    @JsonAlias({"patient_id", "patientId", "userId"})
    private Long patientId;

    @NotNull(message = "Medicine ID is required")
    @JsonAlias({"medicine_id", "medicineId", "medId"})
    private Long medicineId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    @JsonAlias({"quantity", "quantity_needed", "quantityNeeded"})
    private Integer quantity;

    @Builder.Default
    private String urgency = "EMERGENCY"; // e.g. "EMERGENCY", "1_HOUR", "CRITICAL"

    @JsonAlias({"lat", "latitude"})
    private Double latitude;

    @JsonAlias({"lng", "lon", "longitude"})
    private Double longitude;

    private String notes;

    /**
     * Supports nested location object: e.g. { "location": { "lat": 12.97, "lng": 77.59 } }
     */
    @JsonAlias({"location"})
    public void setLocation(Object location) {
        if (location instanceof Map<?, ?> map) {
            Object latObj = map.get("lat") != null ? map.get("lat") : map.get("latitude");
            Object lngObj = map.get("lng") != null ? map.get("lng") : (map.get("lon") != null ? map.get("lon") : map.get("longitude"));

            if (latObj instanceof Number num) {
                this.latitude = num.doubleValue();
            } else if (latObj != null) {
                try {
                    this.latitude = Double.parseDouble(latObj.toString());
                } catch (NumberFormatException ignored) {}
            }

            if (lngObj instanceof Number num) {
                this.longitude = num.doubleValue();
            } else if (lngObj != null) {
                try {
                    this.longitude = Double.parseDouble(lngObj.toString());
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}
