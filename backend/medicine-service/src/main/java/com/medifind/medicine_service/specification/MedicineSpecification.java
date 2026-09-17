package com.medifind.medicine_service.specification;

import com.medifind.medicine_service.entity.Medicines;
import org.springframework.data.jpa.domain.Specification;

public class MedicineSpecification {
    public static Specification<Medicines> searchByKeyword(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // returns all if search is empty
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("genericName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("strength")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("form")), pattern)
            );
        };
    }
}
