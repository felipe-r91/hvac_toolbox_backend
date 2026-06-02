package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.service.HealthCheckTemplateLibraryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet")
public class HealthCheckTemplateLibraryController {

    private final HealthCheckTemplateLibraryQueryService healthCheckTemplateLibraryQueryService;

    public HealthCheckTemplateLibraryController(
            HealthCheckTemplateLibraryQueryService healthCheckTemplateLibraryQueryService
    ) {
        this.healthCheckTemplateLibraryQueryService = healthCheckTemplateLibraryQueryService;
    }

    @GetMapping("/health-check-template-library")
    public ResponseEntity<MaintenanceTemplateLibraryResponse> getTemplateLibrary() {
        return ResponseEntity.ok(healthCheckTemplateLibraryQueryService.getTemplateLibrary());
    }
}
