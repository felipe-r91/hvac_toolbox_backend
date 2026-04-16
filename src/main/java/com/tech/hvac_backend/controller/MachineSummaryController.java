package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MachineSummaryResponse;
import com.tech.hvac_backend.service.MachineSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/summary")
public class MachineSummaryController {

    private final MachineSummaryService machineSummaryService;

    public MachineSummaryController(MachineSummaryService machineSummaryService) {
        this.machineSummaryService = machineSummaryService;
    }

    @GetMapping("/machines")
    public ResponseEntity<List<MachineSummaryResponse>> getMachineSummaries() {
        return ResponseEntity.ok(machineSummaryService.getAllMachineSummaries());
    }
}