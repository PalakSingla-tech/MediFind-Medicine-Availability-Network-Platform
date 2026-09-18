package com.medifind.emergency_service.client;

import com.medifind.emergency_service.dto.PharmacyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "pharmacy-service"
)
public interface PharmacyClient {

    @GetMapping("/api/pharmacy/{id}")
    PharmacyDTO getPharmacyById(@PathVariable("id") Long id);

    @GetMapping("/api/pharmacy/city/{city}")
    List<PharmacyDTO> getPharmaciesByCity(@PathVariable("city") String city);
}
