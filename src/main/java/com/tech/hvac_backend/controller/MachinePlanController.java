package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MachinePlanResponse;
import com.tech.hvac_backend.service.MachinePlanQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fleet/machines")
public class MachinePlanController {

    private final MachinePlanQueryService machinePlanQueryService;

    public MachinePlanController(MachinePlanQueryService machinePlanQueryService) {
        this.machinePlanQueryService = machinePlanQueryService;
    }

    @GetMapping("/{id}/plan")
    public ResponseEntity<MachinePlanResponse> getMachinePlan(@PathVariable String id) {
        return ResponseEntity.ok(machinePlanQueryService.getMachinePlan(id));
    }
}