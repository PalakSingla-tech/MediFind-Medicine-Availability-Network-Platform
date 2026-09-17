package com.medifind.medicine_service.repository;

import com.medifind.medicine_service.entity.Medicines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicines, Long>, JpaSpecificationExecutor<Medicines> {

    // Custom SQL/JPQL query to find matching chemical names, while ignoring the drug the user is already looking at
    @Query("""
    SELECT m
    FROM Medicines m
    WHERE m.genericName = :genericName
      AND m.strength = :strength
      AND m.form = :form
      AND m.medId != :currentId
""")
    List<Medicines> findAlternatives(
            @Param("genericName") String genericName,
            @Param("strength") String strength,
            @Param("form") String form,
            @Param("currentId") Long currentId
    );
}
