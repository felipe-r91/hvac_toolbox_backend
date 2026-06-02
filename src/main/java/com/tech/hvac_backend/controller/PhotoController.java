package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.service.PhotoStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoStorageService storageService;
    private final PhotoRecordRepository photoRepository;
    private final MachineRepository machineRepository;

    public PhotoController(
            PhotoStorageService storageService,
            PhotoRecordRepository photoRepository,
            MachineRepository machineRepository
    ) {
        this.storageService = storageService;
        this.photoRepository = photoRepository;
        this.machineRepository = machineRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoRecordEntity> uploadPhoto(
            @RequestParam("ownerType") PhotoOwnerType ownerType,
            @RequestParam("ownerId") String ownerId,
            @RequestParam("machineId") String machineId,
            @RequestParam(value = "taskId", required = false) String taskId,
            @RequestParam(value = "caption", required = false, defaultValue = "") String caption,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        validatePhotoOwnership(ownerType, ownerId, machineId, taskId);

        String photoId = UUID.randomUUID().toString();
        String storageKey = storageService.storePhoto(file, ownerType.name(), ownerId);

        PhotoRecordEntity photo = new PhotoRecordEntity();
        photo.setId(photoId);
        photo.setOwnerType(ownerType);
        photo.setOwnerId(ownerId);
        photo.setMachineId(machineId);
        photo.setTaskId(taskId);
        photo.setFilename(file.getOriginalFilename());
        photo.setStorageKey(storageKey);
        photo.setCaption(caption);
        photo.setCreatedAt(Instant.now().toString());
        photo.setPreviewUrl(storageService.buildPreviewUrl(photoId));

        photoRepository.save(photo);

        if (ownerType == PhotoOwnerType.MACHINE_PROFILE) {
            MachineEntity machine = machineRepository.findById(machineId)
                    .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

            machine.setMachinePhotoId(photo.getId());
            machine.setMachinePhotoPreviewUrl(photo.getPreviewUrl());

            machineRepository.save(machine);
        }

        return ResponseEntity.ok(photo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String id) throws Exception {
        PhotoRecordEntity photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found: " + id));

        Resource resource = storageService.loadPhotoAsResource(photo.getStorageKey());

        String contentType = storageService.detectContentType(resource, photo.getFilename());
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getFilename() + "\"")
                .body(resource);
    }

    private void validatePhotoOwnership(
            PhotoOwnerType ownerType,
            String ownerId,
            String machineId,
            String taskId
    ) {
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("ownerId is required.");
        }

        if (machineId == null || machineId.isBlank()) {
            throw new IllegalArgumentException("machineId is required.");
        }

        if (ownerType == PhotoOwnerType.MACHINE_PROFILE && !ownerId.equals(machineId)) {
            throw new IllegalArgumentException("For MACHINE_PROFILE photos, ownerId must match machineId.");
        }

        if (isTaskPhoto(ownerType) && (taskId == null || taskId.isBlank())) {
            throw new IllegalArgumentException("taskId is required for task photos.");
        }

        if (!isTaskPhoto(ownerType) && taskId != null && !taskId.isBlank()) {
            throw new IllegalArgumentException("taskId must only be sent for task photos.");
        }
    }

    private boolean isTaskPhoto(PhotoOwnerType ownerType) {
        return ownerType == PhotoOwnerType.PREVENTIVE_TASK
                || ownerType == PhotoOwnerType.HEALTH_CHECK_TASK;
    }
}
