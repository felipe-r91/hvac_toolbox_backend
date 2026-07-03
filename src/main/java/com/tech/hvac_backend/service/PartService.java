package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.request.PartRequest;
import com.tech.hvac_backend.dto.response.PartResponse;
import com.tech.hvac_backend.entity.PartEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.PartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    public List<PartResponse> getAllParts() {
        return partRepository.findAllByOrderByJciPartNumberAsc()
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    public PartResponse getPartById(String id) {
        return mapResponse(findPart(id));
    }

    @Transactional
    public PartResponse createPart(PartRequest request) {
        String jciPartNumber = request.getJciPartNumber().trim();
        if (shouldEnforceUniquePartNumber(jciPartNumber)
                && partRepository.existsByJciPartNumberIgnoreCase(jciPartNumber)) {
            throw new IllegalArgumentException("Part already exists: " + jciPartNumber);
        }

        PartEntity part = new PartEntity();
        part.setId(UUID.randomUUID().toString());
        applyRequest(part, request, jciPartNumber);
        return mapResponse(partRepository.save(part));
    }

    @Transactional
    public PartResponse updatePart(String id, PartRequest request) {
        PartEntity part = findPart(id);
        String jciPartNumber = request.getJciPartNumber().trim();
        if (shouldEnforceUniquePartNumber(jciPartNumber)
                && partRepository.existsByJciPartNumberIgnoreCaseAndIdNot(jciPartNumber, id)) {
            throw new IllegalArgumentException("Part already exists: " + jciPartNumber);
        }

        applyRequest(part, request, jciPartNumber);
        return mapResponse(partRepository.save(part));
    }

    @Transactional
    public void deletePart(String id) {
        PartEntity part = findPart(id);
        partRepository.delete(part);
    }

    private PartEntity findPart(String id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + id));
    }

    private void applyRequest(PartEntity part, PartRequest request, String jciPartNumber) {
        part.setJciPartNumber(jciPartNumber);
        part.setManufacturerModel(request.getManufacturerModel().trim());
        part.setManufacturerCode(request.getManufacturerCode().trim());
        part.setTag(request.getTag().trim());
        part.setMachinesModelHavingIt(request.getMachinesModelHavingIt()
                .stream()
                .map(String::trim)
                .distinct()
                .toList());
        part.setDescription(request.getDescription().trim());
    }

    private PartResponse mapResponse(PartEntity part) {
        return new PartResponse(
                part.getId(),
                part.getJciPartNumber(),
                part.getManufacturerModel(),
                part.getManufacturerCode(),
                part.getTag(),
                List.copyOf(part.getMachinesModelHavingIt()),
                part.getDescription(),
                part.getPartPhotoId(),
                part.getPartPhotoPreviewUrl()
        );
    }

    private boolean shouldEnforceUniquePartNumber(String jciPartNumber) {
        return !"unknown".equalsIgnoreCase(jciPartNumber);
    }
}
