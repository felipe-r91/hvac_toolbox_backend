package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
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

    public PhotoController(
            PhotoStorageService storageService,
            PhotoRecordRepository photoRepository
    ) {
        this.storageService = storageService;
        this.photoRepository = photoRepository;
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

        try {

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
            return ResponseEntity.ok(photo);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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

        if (ownerType == PhotoOwnerType.PREVENTIVE_TASK && (taskId == null || taskId.isBlank())) {
            throw new IllegalArgumentException("taskId is required for PREVENTIVE_TASK photos.");
        }

        if (ownerType != PhotoOwnerType.PREVENTIVE_TASK && taskId != null && !taskId.isBlank()) {
            throw new IllegalArgumentException("taskId must only be sent for PREVENTIVE_TASK photos.");
        }
    }
}