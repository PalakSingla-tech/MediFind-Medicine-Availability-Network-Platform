package com.medifind.medicine_service.service;

import com.medifind.medicine_service.dto.MedicineRequestDTO;
import com.medifind.medicine_service.dto.MedicineResponseDTO;
import com.medifind.medicine_service.entity.Medicines;
import com.medifind.medicine_service.exception.MedicineNotFoundException;
import com.medifind.medicine_service.mapper.MedicineMapper;
import com.medifind.medicine_service.repository.MedicineRepository;
import com.medifind.medicine_service.specification.MedicineSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    public  MedicineResponseDTO getMedicineById(Long id) {
        Medicines md = medicineRepository.findById(id).orElseThrow(
                () -> new MedicineNotFoundException("Medicine doesn't exist"));

        return medicineMapper.toResponseDTO(md);
    }

    public List<MedicineResponseDTO> searchMedicines(String searchTerm) {
        Specification<Medicines> spec = MedicineSpecification.searchByKeyword(searchTerm);

        List<Medicines> medicines = medicineRepository.findAll(spec);

        return medicines.stream()
                .map(medicineMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<MedicineResponseDTO> getGenericAlternatives(Long id) {
        Medicines md = medicineRepository.findById(id).orElseThrow(
                () -> new MedicineNotFoundException("Medicine doesn't exist")
        );

        List<Medicines> alternativeMedicines = medicineRepository.findAlternatives(md.getGenericName(), md.getStrength(), md.getForm(), id);

        return alternativeMedicines.stream()
                .map(medicineMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public MedicineResponseDTO createMedicine(MedicineRequestDTO requestDto) {
        Medicines md = medicineMapper.toEntity(requestDto);
        medicineRepository.save(md);
        return medicineMapper.toResponseDTO(md);
    }
}
