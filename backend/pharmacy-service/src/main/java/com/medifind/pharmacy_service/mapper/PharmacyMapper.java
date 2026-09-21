package com.medifind.pharmacy_service.mapper;

import com.medifind.pharmacy_service.dto.PharmacyRequestDTO;
import com.medifind.pharmacy_service.dto.PharmacyResponseDTO;
import com.medifind.pharmacy_service.dto.PharmacyUpdateDTO;
import com.medifind.pharmacy_service.entity.Pharmacy;
import com.medifind.pharmacy_service.entity.VerificationStatus;
import org.springframework.stereotype.Component;

@Component
public class PharmacyMapper {

    public Pharmacy toEntity(Long ownerId, PharmacyRequestDTO dto) {
        return Pharmacy.builder()
                .ownerId(ownerId)
                .name(dto.getName())
                .pharmacyType(dto.getPharmacyType())
                .licenseNumber(dto.getLicenseNumber())
                .licenseDocumentUrl(dto.getLicenseDocumentUrl())
                .licenseExpiryDate(dto.getLicenseExpiryDate())
                .contactPersonName(dto.getContactPersonName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .gstin(dto.getGstin())
                .pharmacistName(dto.getPharmacistName())
                .description(dto.getDescription())
                .pharmacistRegistrationNumber(dto.getPharmacistRegistrationNumber())
                .verificationStatus(VerificationStatus.PENDING)
                .isVerified(false)
                .build();
    }

    public PharmacyResponseDTO toResponseDto(Pharmacy ph) {
        return PharmacyResponseDTO.builder()
                .pharmacyId(ph.getPharmacyId())
                .name(ph.getName())
                .pharmacyType(ph.getPharmacyType())
                .licenseNumber(ph.getLicenseNumber())
                .licenseDocumentUrl(ph.getLicenseDocumentUrl())
                .licenseExpiryDate(ph.getLicenseExpiryDate())
                .contactPersonName(ph.getContactPersonName())
                .email(ph.getEmail())
                .phone(ph.getPhone())
                .address(ph.getAddress())
                .city(ph.getCity())
                .state(ph.getState())
                .pincode(ph.getPincode())
                .latitude(ph.getLatitude())
                .longitude(ph.getLongitude())
                .gstin(ph.getGstin())
                .pharmacistName(ph.getPharmacistName())
                .description(ph.getDescription())
                .pharmacistRegistrationNumber(ph.getPharmacistRegistrationNumber())
                .isVerified(ph.isVerified())
                .verificationStatus(ph.getVerificationStatus())
                .rejectionReason(ph.getRejectionReason())
                .build();
    }

    public void updateEntity(Pharmacy ph, PharmacyUpdateDTO dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) ph.setName(dto.getName().trim());
        if (dto.getPharmacyType() != null) ph.setPharmacyType(dto.getPharmacyType());
        if (dto.getContactPersonName() != null && !dto.getContactPersonName().isBlank()) ph.setContactPersonName(dto.getContactPersonName().trim());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) ph.setEmail(dto.getEmail().trim());
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) ph.setPhone(dto.getPhone().trim());
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) ph.setAddress(dto.getAddress().trim());
        if (dto.getCity() != null && !dto.getCity().isBlank()) ph.setCity(dto.getCity().trim());
        if (dto.getState() != null && !dto.getState().isBlank()) ph.setState(dto.getState().trim());
        if (dto.getPincode() != null && !dto.getPincode().isBlank()) ph.setPincode(dto.getPincode().trim());
        if (dto.getLatitude() != null && !dto.getLatitude().isNaN() && dto.getLatitude() != 0.0) ph.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null && !dto.getLongitude().isNaN() && dto.getLongitude() != 0.0) ph.setLongitude(dto.getLongitude());
        if (dto.getGstin() != null && !dto.getGstin().isBlank()) ph.setGstin(dto.getGstin().trim());
        if (dto.getPharmacistName() != null && !dto.getPharmacistName().isBlank()) ph.setPharmacistName(dto.getPharmacistName().trim());
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) ph.setDescription(dto.getDescription().trim());
        if (dto.getPharmacistRegistrationNumber() != null && !dto.getPharmacistRegistrationNumber().isBlank()) ph.setPharmacistRegistrationNumber(dto.getPharmacistRegistrationNumber().trim());
        if (dto.getLicenseDocumentUrl() != null && !dto.getLicenseDocumentUrl().isBlank()) ph.setLicenseDocumentUrl(dto.getLicenseDocumentUrl().trim());
        if (dto.getLicenseExpiryDate() != null) ph.setLicenseExpiryDate(dto.getLicenseExpiryDate());
    }
}
