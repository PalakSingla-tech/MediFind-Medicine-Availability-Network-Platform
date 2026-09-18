package com.medifind.pharmacy_service.client;

import com.medifind.pharmacy_service.dto.MedicineDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "medicine-service"
)
public interface MedicineClient {

    @GetMapping("/api/medicines/{id}/alternatives")
    List<MedicineDTO> getGenericAlternatives(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/medicines/{id}")
    MedicineDTO getMedicineById(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/medicines/search")
    List<MedicineDTO> searchMedicines(@RequestParam String name);
}
