package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.request.TemplatePlanRequest;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryItemResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.service.HealthCheckTemplateLibraryCommandService;
import com.tech.hvac_backend.service.HealthCheckTemplateLibraryQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet")
public class HealthCheckTemplateLibraryController {

    private final HealthCheckTemplateLibraryQueryService healthCheckTemplateLibraryQueryService;
    private final HealthCheckTemplateLibraryCommandService healthCheckTemplateLibraryCommandService;

    public HealthCheckTemplateLibraryController(
            HealthCheckTemplateLibraryQueryService healthCheckTemplateLibraryQueryService,
            HealthCheckTemplateLibraryCommandService healthCheckTemplateLibraryCommandService
    ) {
        this.healthCheckTemplateLibraryQueryService = healthCheckTemplateLibraryQueryService;
        this.healthCheckTemplateLibraryCommandService = healthCheckTemplateLibraryCommandService;
    }

    @GetMapping("/health-check-template-library")
    public ResponseEntity<MaintenanceTemplateLibraryResponse> getTemplateLibrary() {
        return ResponseEntity.ok(healthCheckTemplateLibraryQueryService.getTemplateLibrary());
    }

    @PostMapping("/health-check-template-library")
    public ResponseEntity<MaintenanceTemplateLibraryItemResponse> createTemplate(
            @Valid @RequestBody TemplatePlanRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(healthCheckTemplateLibraryCommandService.createTemplate(request));
    }

    @PutMapping("/health-check-template-library/{code}")
    public ResponseEntity<MaintenanceTemplateLibraryItemResponse> updateTemplate(
            @PathVariable String code,
            @Valid @RequestBody TemplatePlanRequest request
    ) {
        return ResponseEntity.ok(healthCheckTemplateLibraryCommandService.updateTemplate(code, request));
    }

    @DeleteMapping("/health-check-template-library/{code}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String code) {
        healthCheckTemplateLibraryCommandService.deleteTemplate(code);
        return ResponseEntity.noContent().build();
    }
}
