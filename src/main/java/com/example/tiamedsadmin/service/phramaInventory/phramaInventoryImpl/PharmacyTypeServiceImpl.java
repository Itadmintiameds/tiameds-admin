package com.example.tiamedsadmin.service.phramaInventory.phramaInventoryImpl;

import com.example.tiamedsadmin.dto.PharmacyTypeDto;
import com.example.tiamedsadmin.entity.PharmacyType;
import com.example.tiamedsadmin.exception.ApplicationException;
import com.example.tiamedsadmin.exception.NotFoundException;
import com.example.tiamedsadmin.mapper.PharmacyTypeMapper;
import com.example.tiamedsadmin.repository.PharmacyTypeRepository;
import com.example.tiamedsadmin.service.phramaInventory.PharmacyTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyTypeServiceImpl implements PharmacyTypeService {

    private final PharmacyTypeRepository pharmacyTypeRepository;
    private final PharmacyTypeMapper pharmacyTypeMapper;

    @Override
    public List<PharmacyTypeDto> findAll() {
        List<PharmacyType> pharmacyTypes = pharmacyTypeRepository.findAll();
        if (pharmacyTypes.isEmpty()) {
            return List.of();
        }
        return pharmacyTypeMapper.toDtoList(pharmacyTypes);
    }

    @Override
    public PharmacyTypeDto findById(Long id) {
        PharmacyType pharmacyType = pharmacyTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy type not found with id: " + id));
        return pharmacyTypeMapper.toDto(pharmacyType);
    }

    @Override
    @Transactional
    public PharmacyTypeDto create(PharmacyTypeDto dto) {
        if (dto == null) {
            throw new ApplicationException("PharmacyTypeDto cannot be null");
        }
        PharmacyType pharmacyType = new PharmacyType();
        pharmacyType.setPharmacyTypeName(dto.getPharmacyTypeName());
        pharmacyType.setActive(true);
        pharmacyType.setCreatedDate(LocalDateTime.now());
        pharmacyType.setUpdatedDate(LocalDateTime.now());
        PharmacyType savedPharmacyType = pharmacyTypeRepository.save(pharmacyType);
        return pharmacyTypeMapper.toDto(savedPharmacyType);
    }

    @Override
    @Transactional
    public PharmacyTypeDto update(Long id, PharmacyTypeDto dto) {
        PharmacyType pharmacyType = pharmacyTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pharmacy type not found with id: " + id));
        pharmacyType.setPharmacyTypeName(dto.getPharmacyTypeName());
        pharmacyType.setUpdatedDate(LocalDateTime.now());
        PharmacyType updatedPharmacyType = pharmacyTypeRepository.save(pharmacyType);
        return pharmacyTypeMapper.toDto(updatedPharmacyType);
    }

    @Override
    public void delete(Long id) {
        if (!pharmacyTypeRepository.existsById(id)) {
            throw new NotFoundException("Pharmacy type not found with id: " + id);
        }
        pharmacyTypeRepository.deleteById(id);
    }
}
