package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MachineHealthCheckResponse;
import com.tech.hvac_backend.service.HealthCheckQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet/machines")
public class HealthCheckController {

    private final HealthCheckQueryService healthCheckQueryService;

    public HealthCheckController(HealthCheckQueryService healthCheckQueryService) {
        this.healthCheckQueryService = healthCheckQueryService;
    }

    @GetMapping("/{id}/health-check")
    public ResponseEntity<MachineHealthCheckResponse> getHealthCheck(@PathVariable String id) {
        return ResponseEntity.ok(healthCheckQueryService.getHealthCheck(id));
    }
}
