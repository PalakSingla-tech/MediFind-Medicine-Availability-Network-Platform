package com.medifind.medicine_service.client;

import com.medifind.medicine_service.dto.NearbyPharmacyStockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "pharmacy-service")
public interface PharmacyClient {

    @GetMapping("/api/pharmacy/nearby")
    List<NearbyPharmacyStockDTO> getNearbyPharmacies(
            @RequestParam("lat") double lat,
            @RequestParam("lng") double lng,
            @RequestParam("medicineId") Long medicineId,
            @RequestParam(value = "radius", required = false) Double radius
    );

    @GetMapping("/api/pharmacy/inventory/medicine/{medicineId}")
    List<Object> getPharmaciesHavingMedicine(@PathVariable("medicineId") Long medicineId);
}

