package com.medifind.emergency_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emergency_requests", indexes = {
        @Index(name = "idx_req_patient", columnList = "patient_id"),
        @Index(name = "idx_req_medicine", columnList = "medicine_id"),
        @Index(name = "idx_req_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    private String patientName;

    private String patientPhone;

    @Column(name = "medicine_id", nullable = false)
    private Long medicineId;

    private String medicineName;

    @Column(name = "quantity_needed", nullable = false)
    private Integer quantityNeeded;

    @Column(nullable = false)
    @Builder.Default
    private String urgency = "EMERGENCY";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "emergencyRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmergencyResponse> responses = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RequestStatus.PENDING;
        }
        if (this.urgency == null) {
            this.urgency = "EMERGENCY";
        }
    }
}
