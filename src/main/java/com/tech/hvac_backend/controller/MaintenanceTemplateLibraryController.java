package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.request.TemplatePlanRequest;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryItemResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.service.MaintenanceTemplateLibraryCommandService;
import com.tech.hvac_backend.service.MaintenanceTemplateLibraryQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet")
public class MaintenanceTemplateLibraryController {

    private final MaintenanceTemplateLibraryQueryService maintenanceTemplateLibraryQueryService;
    private final MaintenanceTemplateLibraryCommandService maintenanceTemplateLibraryCommandService;

    public MaintenanceTemplateLibraryController(
            MaintenanceTemplateLibraryQueryService maintenanceTemplateLibraryQueryService,
            MaintenanceTemplateLibraryCommandService maintenanceTemplateLibraryCommandService
    ) {
        this.maintenanceTemplateLibraryQueryService = maintenanceTemplateLibraryQueryService;
        this.maintenanceTemplateLibraryCommandService = maintenanceTemplateLibraryCommandService;
    }

    @GetMapping("/template-library")
    public ResponseEntity<MaintenanceTemplateLibraryResponse> getTemplateLibrary() {
        return ResponseEntity.ok(maintenanceTemplateLibraryQueryService.getTemplateLibrary());
    }

    @PostMapping("/template-library")
    public ResponseEntity<MaintenanceTemplateLibraryItemResponse> createTemplate(
            @Valid @RequestBody TemplatePlanRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(maintenanceTemplateLibraryCommandService.createTemplate(request));
    }

    @PutMapping("/template-library/{code}")
    public ResponseEntity<MaintenanceTemplateLibraryItemResponse> updateTemplate(
            @PathVariable String code,
            @Valid @RequestBody TemplatePlanRequest request
    ) {
        return ResponseEntity.ok(maintenanceTemplateLibraryCommandService.updateTemplate(code, request));
    }

    @DeleteMapping("/template-library/{code}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String code) {
        maintenanceTemplateLibraryCommandService.deleteTemplate(code);
        return ResponseEntity.noContent().build();
    }
}
