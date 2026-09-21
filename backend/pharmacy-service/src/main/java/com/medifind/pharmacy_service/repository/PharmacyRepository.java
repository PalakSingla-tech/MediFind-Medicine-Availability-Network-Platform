package com.medifind.pharmacy_service.repository;

import com.medifind.pharmacy_service.entity.Pharmacy;
import com.medifind.pharmacy_service.entity.VerificationStatus;
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

    List<Pharmacy> findByCityIgnoreCaseAndVerificationStatus(String city, VerificationStatus verificationStatus);

    List<Pharmacy> findByVerificationStatus(VerificationStatus verificationStatus);

    List<Pharmacy> findByVerificationStatusOrderByPharmacyIdDesc(VerificationStatus verificationStatus);

    @Query("""
            SELECT DISTINCT p
            FROM Pharmacy p
            JOIN Inventory i ON i.pharmacyId = p.pharmacyId
            WHERE i.medicineId = :medicineId
            AND i.quantity > 0
            AND p.verificationStatus = com.medifind.pharmacy_service.entity.VerificationStatus.VERIFIED
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
            AND p.verificationStatus = com.medifind.pharmacy_service.entity.VerificationStatus.VERIFIED
            """)
    List<Object[]> findPharmaciesWithInventoryHavingMedicine(
            @Param("medicineId") Long medicineId
    );

    @Query("""
            SELECT p, i.price, i.quantity
            FROM Pharmacy p
            JOIN Inventory i ON i.pharmacyId = p.pharmacyId
            WHERE i.medicineId = :medicineId
            AND i.quantity > 0
            AND p.verificationStatus = com.medifind.pharmacy_service.entity.VerificationStatus.VERIFIED
            """)
    List<Object[]> findNearbyPharmacies(
            @Param("medicineId") Long medicineId
    );
}
