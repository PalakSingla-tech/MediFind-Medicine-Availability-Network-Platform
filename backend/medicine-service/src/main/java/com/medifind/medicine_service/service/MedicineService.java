package com.medifind.medicine_service.service;

import com.medifind.medicine_service.client.PharmacyClient;
import com.medifind.medicine_service.dto.AlternativeWithStockDTO;
import com.medifind.medicine_service.dto.MedicineRequestDTO;
import com.medifind.medicine_service.dto.MedicineResponseDTO;
import com.medifind.medicine_service.dto.NearbyPharmacyStockDTO;
import com.medifind.medicine_service.entity.Medicines;
import com.medifind.medicine_service.exception.MedicineAlreadyExistsException;
import com.medifind.medicine_service.exception.MedicineInUseException;
import com.medifind.medicine_service.exception.MedicineNotFoundException;
import com.medifind.medicine_service.mapper.MedicineMapper;
import com.medifind.medicine_service.repository.MedicineRepository;
import com.medifind.medicine_service.specification.MedicineSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;
    private final PharmacyClient pharmacyClient;

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

        if (alternativeMedicines.isEmpty()) {
            alternativeMedicines = medicineRepository.findAlternativesByGenericName(md.getGenericName(), id);
        }

        return alternativeMedicines.stream()
                .map(medicineMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AlternativeWithStockDTO> getGenericAlternativesWithStock(Long id, double lat, double lng, Double radius) {
        Medicines md = medicineRepository.findById(id).orElseThrow(
                () -> new MedicineNotFoundException("Medicine doesn't exist")
        );

        List<Medicines> alternativeMedicines = medicineRepository.findAlternatives(md.getGenericName(), md.getStrength(), md.getForm(), id);

        if (alternativeMedicines.isEmpty()) {
            alternativeMedicines = medicineRepository.findAlternativesByGenericName(md.getGenericName(), id);
        }

        Double effectiveRadius = (radius != null && radius > 0) ? radius : 5.0;

        List<AlternativeWithStockDTO> results = new ArrayList<>();

        for (Medicines alt : alternativeMedicines) {
            MedicineResponseDTO responseDTO = medicineMapper.toResponseDTO(alt);

            List<NearbyPharmacyStockDTO> pharmacies = Collections.emptyList();
            try {
                pharmacies = pharmacyClient.getNearbyPharmacies(lat, lng, alt.getMedId(), effectiveRadius);
                if (pharmacies == null) {
                    pharmacies = Collections.emptyList();
                }
            } catch (Exception e) {
                log.error("Failed to query stock from pharmacy-service for medicine id {}: {}", alt.getMedId(), e.getMessage());
            }

            boolean inStock = pharmacies.stream().anyMatch(p -> p.getQuantityAvailable() > 0 || p.getQuantity() > 0);
            int totalStock = pharmacies.stream()
                    .mapToInt(p -> p.getQuantityAvailable() > 0 ? p.getQuantityAvailable() : Math.max(p.getQuantity(), 0))
                    .sum();
            Double minPrice = pharmacies.stream()
                    .map(NearbyPharmacyStockDTO::getPrice)
                    .filter(price -> price > 0.0)
                    .min(Double::compare)
                    .orElse(null);

            AlternativeWithStockDTO dto = AlternativeWithStockDTO.builder()
                    .medId(responseDTO.getMedId())
                    .name(responseDTO.getName())
                    .genericName(responseDTO.getGenericName())
                    .manufacturer(responseDTO.getManufacturer())
                    .strength(responseDTO.getStrength())
                    .form(responseDTO.getForm())
                    .barcode(responseDTO.getBarcode())
                    .requiresPrescription(responseDTO.getRequiresPrescription())
                    .search_keywords(responseDTO.getSearch_keywords())
                    .category(responseDTO.getCategory())
                    .status(responseDTO.getStatus())
                    .medicine(responseDTO)
                    .inStock(inStock)
                    .totalAvailableStock(totalStock)
                    .minPrice(minPrice)
                    .pharmacies(pharmacies)
                    .build();

            results.add(dto);
        }

        // Sort alternatives: available in stock first, then by min price
        results.sort((a, b) -> {
            if (a.isInStock() != b.isInStock()) {
                return Boolean.compare(b.isInStock(), a.isInStock());
            }
            if (a.getMinPrice() != null && b.getMinPrice() != null) {
                return Double.compare(a.getMinPrice(), b.getMinPrice());
            }
            return 0;
        });

        return results;
    }

    public MedicineResponseDTO createMedicine(MedicineRequestDTO requestDto) {
        Medicines md = medicineMapper.toEntity(requestDto);
        medicineRepository.save(md);
        return medicineMapper.toResponseDTO(md);
    }

    public MedicineResponseDTO createAdminMedicine(MedicineRequestDTO requestDto) {
        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name is required");
        }
        if (requestDto.getGenericName() == null || requestDto.getGenericName().trim().isEmpty()) {
            throw new IllegalArgumentException("Generic name is required");
        }
        if (requestDto.getStrength() == null || requestDto.getStrength().trim().isEmpty()) {
            throw new IllegalArgumentException("Strength is required");
        }
        if (requestDto.getForm() == null || requestDto.getForm().trim().isEmpty()) {
            throw new IllegalArgumentException("Form is required");
        }

        // Duplicate check 1: barcode
        if (requestDto.getBarcode() > 0 && medicineRepository.existsByBarcode(requestDto.getBarcode())) {
            throw new MedicineAlreadyExistsException("A medicine with barcode " + requestDto.getBarcode() + " already exists.");
        }

        // Duplicate check 2: name + strength + form
        if (medicineRepository.existsByNameIgnoreCaseAndStrengthIgnoreCaseAndFormIgnoreCase(
                requestDto.getName().trim(),
                requestDto.getStrength().trim(),
                requestDto.getForm().trim())) {
            throw new MedicineAlreadyExistsException("A medicine with name '" + requestDto.getName().trim()
                    + "', strength '" + requestDto.getStrength().trim()
                    + "', and form '" + requestDto.getForm().trim() + "' already exists.");
        }

        Medicines md = medicineMapper.toEntity(requestDto);
        if (md.getStatus() == null) {
            md.setStatus(Medicines.Status.ACTIVE);
        }
        Medicines saved = medicineRepository.save(md);
        return medicineMapper.toResponseDTO(saved);
    }

    public Page<MedicineResponseDTO> getAllMedicinesPaginated(Pageable pageable) {
        Page<Medicines> medicinesPage = medicineRepository.findAll(pageable);
        return medicinesPage.map(medicineMapper::toResponseDTO);
    }

    public MedicineResponseDTO updateMedicine(Long id, MedicineRequestDTO requestDto) {
        Medicines md = medicineRepository.findById(id).orElseThrow(
                () -> new MedicineNotFoundException("Medicine doesn't exist with id: " + id)
        );

        if (requestDto.getName() == null || requestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name is required");
        }
        if (requestDto.getGenericName() == null || requestDto.getGenericName().trim().isEmpty()) {
            throw new IllegalArgumentException("Generic name is required");
        }
        if (requestDto.getStrength() == null || requestDto.getStrength().trim().isEmpty()) {
            throw new IllegalArgumentException("Strength is required");
        }
        if (requestDto.getForm() == null || requestDto.getForm().trim().isEmpty()) {
            throw new IllegalArgumentException("Form is required");
        }

        // Check if barcode conflicts with another medicine
        if (requestDto.getBarcode() > 0 && requestDto.getBarcode() != md.getBarcode()) {
            if (medicineRepository.existsByBarcodeAndMedIdNot(requestDto.getBarcode(), id)) {
                throw new MedicineAlreadyExistsException("Another medicine with barcode " + requestDto.getBarcode() + " already exists.");
            }
        }

        // Check if name + strength + form conflicts with another medicine
        if (medicineRepository.existsByNameIgnoreCaseAndStrengthIgnoreCaseAndFormIgnoreCaseAndMedIdNot(
                requestDto.getName().trim(),
                requestDto.getStrength().trim(),
                requestDto.getForm().trim(),
                id)) {
            throw new MedicineAlreadyExistsException("Another medicine with name '" + requestDto.getName().trim()
                    + "', strength '" + requestDto.getStrength().trim()
                    + "', and form '" + requestDto.getForm().trim() + "' already exists.");
        }

        md.setName(requestDto.getName().trim());
        md.setGenericName(requestDto.getGenericName().trim());
        md.setManufacturer(requestDto.getManufacturer() != null ? requestDto.getManufacturer().trim() : md.getManufacturer());
        md.setStrength(requestDto.getStrength().trim());
        md.setForm(requestDto.getForm().trim());
        md.setBarcode(requestDto.getBarcode());
        if (requestDto.getRequiresPrescription() != null) {
            md.setRequiresPrescription(requestDto.getRequiresPrescription());
        }
        if (requestDto.getSearch_keywords() != null) {
            md.setSearch_keywords(new ArrayList<>(requestDto.getSearch_keywords()));
        }
        if (requestDto.getCategory() != null) {
            md.setCategory(requestDto.getCategory());
        }
        if (requestDto.getStatus() != null) {
            md.setStatus(requestDto.getStatus());
        }

        Medicines saved = medicineRepository.save(md);
        return medicineMapper.toResponseDTO(saved);
    }

    public void deleteMedicine(Long id) {
        Medicines md = medicineRepository.findById(id).orElseThrow(
                () -> new MedicineNotFoundException("Medicine doesn't exist with id: " + id)
        );

        // Check if referenced in inventory
        boolean inInventory = false;
        try {
            List<Object> pharmacies = pharmacyClient.getPharmaciesHavingMedicine(id);
            if (pharmacies != null && !pharmacies.isEmpty()) {
                inInventory = true;
            }
        } catch (Exception e) {
            log.warn("Could not check inventory status for medicine id {} from pharmacy-service: {}", id, e.getMessage());
        }

        if (inInventory) {
            throw new MedicineInUseException("Cannot delete medicine with id " + id + ": It is currently referenced in pharmacy inventory.");
        }

        medicineRepository.delete(md);
    }
}

