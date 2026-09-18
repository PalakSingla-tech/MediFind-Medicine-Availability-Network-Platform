package com.medifind.pharmacy_service.repository;

import com.medifind.pharmacy_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByPharmacyId(Long pharmacyId);

    List<Inventory> findByMedicineId(Long medicineId);

    boolean existsByPharmacyIdAndMedicineId(Long pharmacyId, Long medicineId);
}
