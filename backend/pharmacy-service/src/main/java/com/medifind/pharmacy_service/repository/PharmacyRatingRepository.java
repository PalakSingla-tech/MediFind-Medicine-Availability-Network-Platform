package com.medifind.pharmacy_service.repository;

import com.medifind.pharmacy_service.entity.PharmacyRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRatingRepository extends JpaRepository<PharmacyRating, Long> {

    List<PharmacyRating> findByPharmacyIdOrderByCreatedAtDesc(Long pharmacyId);

    Optional<PharmacyRating> findByPharmacyIdAndPatientId(Long pharmacyId, Long patientId);

    @Query("SELECT AVG(r.rating) FROM PharmacyRating r WHERE r.pharmacyId = :pharmacyId")
    Double getAverageRatingByPharmacyId(@Param("pharmacyId") Long pharmacyId);

    @Query("SELECT COUNT(r) FROM PharmacyRating r WHERE r.pharmacyId = :pharmacyId")
    Long countRatingsByPharmacyId(@Param("pharmacyId") Long pharmacyId);
}
