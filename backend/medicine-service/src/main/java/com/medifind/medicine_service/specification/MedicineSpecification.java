package com.medifind.medicine_service.specification;

import com.medifind.medicine_service.entity.Medicines;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class MedicineSpecification {
    public static Specification<Medicines> searchByKeyword(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            if (query != null) {
                query.distinct(true);
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            Join<Medicines, String> keywordsJoin = root.join("search_keywords", JoinType.LEFT);

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("genericName")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("strength")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("form")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(keywordsJoin), pattern)
            );
        };
    }
}