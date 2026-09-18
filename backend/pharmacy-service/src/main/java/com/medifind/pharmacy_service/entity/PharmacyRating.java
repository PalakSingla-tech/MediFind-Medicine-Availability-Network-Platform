package com.medifind.pharmacy_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "pharmacy_ratings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pharmacy_patient_rating",
                        columnNames = {"pharmacyId", "patientId"}
                )
        },
        indexes = {
                @Index(name = "idx_rating_pharmacy", columnList = "pharmacyId"),
                @Index(name = "idx_rating_patient", columnList = "patientId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pharmacyId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
