package com.medifind.emergency_service.client;

import com.medifind.emergency_service.dto.MedicineDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "medicine-service"
)
public interface MedicineClient {

    @GetMapping("/api/medicines/{id}")
    MedicineDTO getMedicineById(@PathVariable("id") Long id);
}
