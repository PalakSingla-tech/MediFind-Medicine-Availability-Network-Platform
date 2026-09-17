package com.medifind.medicine_service.mapper;

import com.medifind.medicine_service.dto.MedicineRequestDTO;
import com.medifind.medicine_service.dto.MedicineResponseDTO;
import com.medifind.medicine_service.entity.Medicines;
import org.springframework.stereotype.Component;

@Component
public class MedicineMapper {
    public MedicineResponseDTO toResponseDTO(Medicines md)
    {
        return MedicineResponseDTO.builder()
                .medId(md.getMedId())
                .name(md.getName())
                .genericName(md.getGenericName())
                .manufacturer(md.getManufacturer())
                .form(md.getForm())
                .barcode(md.getBarcode())
                .strength(md.getStrength())
                .requiresPrescription(md.getRequiresPrescription())
                .search_keywords(md.getSearch_keywords())
                .category(md.getCategory())
                .status(md.getStatus())
                .build();
    }

    public Medicines toEntity(MedicineRequestDTO dto)
    {
        return Medicines.builder()
                .name(dto.getName())
                .genericName(dto.getGenericName())
                .manufacturer(dto.getManufacturer())
                .form(dto.getForm())
                .strength(dto.getStrength())
                .barcode(dto.getBarcode())
                .requiresPrescription(dto.getRequiresPrescription())
                .search_keywords(dto.getSearch_keywords())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .build();
    }
}
