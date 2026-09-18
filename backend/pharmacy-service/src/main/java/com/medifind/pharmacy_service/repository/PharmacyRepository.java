package com.medifind.pharmacy_service.repository;

import com.medifind.pharmacy_service.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    Optional<Pharmacy> findByOwnerId(Long ownerId);
    List<Pharmacy> findByCityIgnoreCase(String city);

    @Query("""
            SELECT DISTINCT p
            FROM Pharmacy p
            JOIN Inventory i ON i.pharmacyId = p.pharmacyId
            WHERE i.medicineId = :medicineId
            AND i.quantity > 0
            """)
    List<Pharmacy> findPharmaciesHavingMedicine(
            @Param("medicineId") Long medicineId
    );

    @Query("""
            SELECT p, i.price, i.quantity
            FROM Pharmacy p
            JOIN Inventory i ON i.pharmacyId = p.pharmacyId
            WHERE i.medicineId = :medicineId
            AND i.quantity > 0
            """)
    List<Object[]> findPharmaciesWithInventoryHavingMedicine(
            @Param("medicineId") Long medicineId
    );
}
