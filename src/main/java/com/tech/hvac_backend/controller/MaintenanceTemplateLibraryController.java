package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.service.MaintenanceTemplateLibraryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet")
public class MaintenanceTemplateLibraryController {

    private final MaintenanceTemplateLibraryQueryService maintenanceTemplateLibraryQueryService;

    public MaintenanceTemplateLibraryController(
            MaintenanceTemplateLibraryQueryService maintenanceTemplateLibraryQueryService
    ) {
        this.maintenanceTemplateLibraryQueryService = maintenanceTemplateLibraryQueryService;
    }

    @GetMapping("/template-library")
    public ResponseEntity<MaintenanceTemplateLibraryResponse> getTemplateLibrary() {
        return ResponseEntity.ok(maintenanceTemplateLibraryQueryService.getTemplateLibrary());
    }
}