package com.medifind.pharmacy_service.mapper;

import com.medifind.pharmacy_service.dto.PharmacyRequestDTO;
import com.medifind.pharmacy_service.dto.PharmacyResponseDTO;
import com.medifind.pharmacy_service.dto.PharmacyUpdateDTO;
import com.medifind.pharmacy_service.entity.Pharmacy;
import org.springframework.stereotype.Component;

@Component
public class PharmacyMapper {
    public Pharmacy toEntity(Long ownerId, PharmacyRequestDTO dto)
    {
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
                .build();
    }

    public PharmacyResponseDTO toResponseDto(Pharmacy ph)
    {
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
                .build();
    }

    public void updateEntity(Pharmacy ph, PharmacyUpdateDTO dto) {

        ph.setName(dto.getName());
        ph.setPharmacyType(dto.getPharmacyType());
        ph.setContactPersonName(dto.getContactPersonName());
        ph.setEmail(dto.getEmail());
        ph.setPhone(dto.getPhone());
        ph.setAddress(dto.getAddress());
        ph.setCity(dto.getCity());
        ph.setState(dto.getState());
        ph.setPincode(dto.getPincode());
        ph.setLatitude(dto.getLatitude());
        ph.setLongitude(dto.getLongitude());
        ph.setGstin(dto.getGstin());
        ph.setPharmacistName(dto.getPharmacistName());
        ph.setDescription(dto.getDescription());
        ph.setPharmacistRegistrationNumber(
                dto.getPharmacistRegistrationNumber()
        );
    }
}
