package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.service.PhotoStorageService;
import com.tech.hvac_backend.service.PhotoUploadService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    private final PhotoStorageService storageService;
    private final PhotoUploadService photoUploadService;
    private final PhotoRecordRepository photoRepository;

    public PhotoController(
            PhotoStorageService storageService,
            PhotoUploadService photoUploadService,
            PhotoRecordRepository photoRepository
    ) {
        this.storageService = storageService;
        this.photoUploadService = photoUploadService;
        this.photoRepository = photoRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoRecordEntity> uploadPhoto(
            @RequestParam("ownerType") PhotoOwnerType ownerType,
            @RequestParam("ownerId") String ownerId,
            @RequestParam(value = "machineId", required = false) String machineId,
            @RequestParam(value = "partId", required = false) String partId,
            @RequestParam(value = "taskId", required = false) String taskId,
            @RequestParam(value = "caption", required = false, defaultValue = "") String caption,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(photoUploadService.uploadPhoto(
                ownerType,
                ownerId,
                machineId,
                partId,
                taskId,
                caption,
                file
        ));
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

}
