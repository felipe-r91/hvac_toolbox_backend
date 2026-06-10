package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PartEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PartRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@Service
public class PhotoUploadService {

    private final PhotoStorageService storageService;
    private final PhotoRecordRepository photoRepository;
    private final MachineRepository machineRepository;
    private final PartRepository partRepository;

    public PhotoUploadService(
            PhotoStorageService storageService,
            PhotoRecordRepository photoRepository,
            MachineRepository machineRepository,
            PartRepository partRepository
    ) {
        this.storageService = storageService;
        this.photoRepository = photoRepository;
        this.machineRepository = machineRepository;
        this.partRepository = partRepository;
    }

    public PhotoRecordEntity uploadPhoto(
            PhotoOwnerType ownerType,
            String ownerId,
            String machineId,
            String partId,
            String taskId,
            String caption,
            MultipartFile file
    ) throws Exception {
        validatePhotoOwnership(ownerType, ownerId, machineId, partId, taskId);

        MachineEntity machine = findProfileMachine(ownerType, machineId);
        PartEntity part = findProfilePart(ownerType, partId);

        String photoId = UUID.randomUUID().toString();
        String storageKey = storageService.storePhoto(file, ownerType.name(), ownerId);

        PhotoRecordEntity photo = new PhotoRecordEntity();
        photo.setId(photoId);
        photo.setOwnerType(ownerType);
        photo.setOwnerId(ownerId);
        photo.setMachineId(machineId);
        photo.setPartId(partId);
        photo.setTaskId(taskId);
        photo.setFilename(file.getOriginalFilename());
        photo.setStorageKey(storageKey);
        photo.setCaption(caption == null ? "" : caption);
        photo.setCreatedAt(Instant.now().toString());
        photo.setPreviewUrl(storageService.buildPreviewUrl(photoId));
        photoRepository.save(photo);

        if (machine != null) {
            machine.setMachinePhotoId(photo.getId());
            machine.setMachinePhotoPreviewUrl(photo.getPreviewUrl());
            machineRepository.save(machine);
        }

        if (part != null) {
            part.setPartPhotoId(photo.getId());
            part.setPartPhotoPreviewUrl(photo.getPreviewUrl());
            partRepository.save(part);
        }

        return photo;
    }

    private void validatePhotoOwnership(
            PhotoOwnerType ownerType,
            String ownerId,
            String machineId,
            String partId,
            String taskId
    ) {
        if (ownerType == null) {
            throw new IllegalArgumentException("ownerType is required.");
        }
        if (isBlank(ownerId)) {
            throw new IllegalArgumentException("ownerId is required.");
        }

        if (ownerType == PhotoOwnerType.PART_PROFILE) {
            if (isBlank(partId)) {
                throw new IllegalArgumentException("partId is required for PART_PROFILE photos.");
            }
            if (!ownerId.equals(partId)) {
                throw new IllegalArgumentException("For PART_PROFILE photos, ownerId must match partId.");
            }
            if (!isBlank(machineId)) {
                throw new IllegalArgumentException("machineId must not be sent for PART_PROFILE photos.");
            }
        } else {
            if (isBlank(machineId)) {
                throw new IllegalArgumentException("machineId is required.");
            }
            if (!isBlank(partId)) {
                throw new IllegalArgumentException("partId must only be sent for PART_PROFILE photos.");
            }
        }

        if (ownerType == PhotoOwnerType.MACHINE_PROFILE && !ownerId.equals(machineId)) {
            throw new IllegalArgumentException("For MACHINE_PROFILE photos, ownerId must match machineId.");
        }

        if (isTaskPhoto(ownerType) && isBlank(taskId)) {
            throw new IllegalArgumentException("taskId is required for task photos.");
        }

        if (!isTaskPhoto(ownerType) && !isBlank(taskId)) {
            throw new IllegalArgumentException("taskId must only be sent for task photos.");
        }
    }

    private MachineEntity findProfileMachine(PhotoOwnerType ownerType, String machineId) {
        if (ownerType != PhotoOwnerType.MACHINE_PROFILE) {
            return null;
        }

        return machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));
    }

    private PartEntity findProfilePart(PhotoOwnerType ownerType, String partId) {
        if (ownerType != PhotoOwnerType.PART_PROFILE) {
            return null;
        }

        return partRepository.findById(partId)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found: " + partId));
    }

    private boolean isTaskPhoto(PhotoOwnerType ownerType) {
        return ownerType == PhotoOwnerType.PREVENTIVE_TASK
                || ownerType == PhotoOwnerType.HEALTH_CHECK_TASK;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
