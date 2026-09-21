package com.medifind.pharmacy_service.controller;

import com.medifind.pharmacy_service.dto.PharmacyRejectRequestDTO;
import com.medifind.pharmacy_service.dto.PharmacyResponseDTO;
import com.medifind.pharmacy_service.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pharmacies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminPharmacyController {

    private final PharmacyService pharmacyService;

    private void checkAdmin(String role) {
        if (role == null || (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("ROLE_ADMIN"))) {
            throw new RuntimeException("Access Denied: Only Admin can access this resource");
        }
    }

    /**
     * GET /api/admin/pharmacies/pending
     * Purpose: Get pending verification requests
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PharmacyResponseDTO>> getPendingPharmacies(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        checkAdmin(role);
        return ResponseEntity.ok(pharmacyService.getPendingPharmacies());
    }

    /**
     * PUT /api/admin/pharmacies/{id}/verify
     * Purpose: Approve pharmacy
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<PharmacyResponseDTO> verifyPharmacy(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long id) {
        checkAdmin(role);
        return ResponseEntity.ok(pharmacyService.verifyPharmacyByAdmin(id));
    }

    /**
     * PUT /api/admin/pharmacies/{id}/reject
     * Purpose: Reject pharmacy (with reason)
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<PharmacyResponseDTO> rejectPharmacy(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @PathVariable Long id,
            @RequestBody(required = false) PharmacyRejectRequestDTO dto,
            @RequestParam(required = false) String reason
    ) {
        checkAdmin(role);
        String rejectionReason = null;
        if (dto != null && dto.getEffectiveReason() != null) {
            rejectionReason = dto.getEffectiveReason();
        } else if (reason != null && !reason.trim().isEmpty()) {
            rejectionReason = reason.trim();
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            rejectionReason = "Rejected by admin";
        }

        return ResponseEntity.ok(pharmacyService.rejectPharmacy(id, rejectionReason));
    }

    /**
     * GET /api/admin/pharmacies/verified
     * Purpose: Get all verified pharmacies
     */
    @GetMapping("/verified")
    public ResponseEntity<List<PharmacyResponseDTO>> getVerifiedPharmacies(
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        checkAdmin(role);
        return ResponseEntity.ok(pharmacyService.getVerifiedPharmacies());
    }
}
