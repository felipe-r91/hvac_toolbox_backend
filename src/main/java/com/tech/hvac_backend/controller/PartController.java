package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.request.PartRequest;
import com.tech.hvac_backend.dto.response.PartResponse;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.service.PartService;
import com.tech.hvac_backend.service.PhotoUploadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartController {

    private final PartService partService;
    private final PhotoUploadService photoUploadService;

    public PartController(PartService partService, PhotoUploadService photoUploadService) {
        this.partService = partService;
        this.photoUploadService = photoUploadService;
    }

    @GetMapping
    public ResponseEntity<List<PartResponse>> getParts() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPart(@PathVariable String id) {
        return ResponseEntity.ok(partService.getPartById(id));
    }

    @PostMapping
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(partService.createPart(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable String id,
            @Valid @RequestBody PartRequest request
    ) {
        return ResponseEntity.ok(partService.updatePart(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable String id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{id}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PartResponse> uploadPicture(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        photoUploadService.uploadPhoto(
                PhotoOwnerType.PART_PROFILE,
                id,
                null,
                id,
                null,
                "",
                file
        );
        return ResponseEntity.ok(partService.getPartById(id));
    }
}
