package com.medifind.emergency_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_responses", indexes = {
        @Index(name = "idx_resp_request", columnList = "request_id"),
        @Index(name = "idx_resp_pharmacy", columnList = "pharmacy_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnore
    private EmergencyRequest emergencyRequest;

    @Column(name = "pharmacy_id", nullable = false)
    private Long pharmacyId;

    private String pharmacyName;

    private String pharmacyPhone;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "estimated_time")
    private String estimatedTime;

    @Column(nullable = false)
    @Builder.Default
    private String status = "OFFERED";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "OFFERED";
        }
    }
}
