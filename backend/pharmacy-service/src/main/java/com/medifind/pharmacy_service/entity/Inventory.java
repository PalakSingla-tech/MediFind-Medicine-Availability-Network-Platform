package com.medifind.pharmacy_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"pharmacyId", "medicineId"}
                )
        },
        indexes = {
                @Index(name = "idx_inventory_pharmacy", columnList = "pharmacyId"),
                @Index(
                        name = "idx_inventory_medicine",
                        columnList = "medicineId"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    private Long pharmacyId;

    private Long medicineId;

    private int quantity;

    private double price;

    private int minAlertThreshold;

    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
