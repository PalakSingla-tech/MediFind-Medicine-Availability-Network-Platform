package com.medifind.user_service.client;

import com.medifind.user_service.client.dto.AlternativeWithStockDTO;
import com.medifind.user_service.client.dto.MedicineResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "medicine-service")
public interface MedicineClient {

    @GetMapping("/api/medicines/{id}")
    MedicineResponseDTO getMedicineById(@PathVariable("id") Long id);

    @GetMapping("/api/medicines/search")
    List<MedicineResponseDTO> searchMedicines(@RequestParam("name") String name);

    @GetMapping("/api/medicines/{id}/alternatives")
    List<MedicineResponseDTO> getGenericAlternatives(@PathVariable("id") Long id);

    @GetMapping("/api/medicines/{id}/alternatives-with-stock")
    List<AlternativeWithStockDTO> getGenericAlternativesWithStock(
            @PathVariable("id") Long id,
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam(value = "radius", required = false, defaultValue = "5.0") Double radius
    );
}
