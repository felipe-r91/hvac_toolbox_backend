package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MachineTimelineItemResponse;
import com.tech.hvac_backend.service.MachineReportsQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/machines")
public class MachineReportsController {

    private final MachineReportsQueryService machineReportsQueryService;

    public MachineReportsController(MachineReportsQueryService machineReportsQueryService) {
        this.machineReportsQueryService = machineReportsQueryService;
    }

    @GetMapping("/{machineId}/preventive")
    public ResponseEntity<List<MachineTimelineItemResponse>> getPreventiveReports(
            @PathVariable String machineId
    ) {
        return ResponseEntity.ok(
                machineReportsQueryService.getPreventiveReportsByMachineId(machineId)
        );
    }

    @GetMapping("/{machineId}/corrective")
    public ResponseEntity<List<MachineTimelineItemResponse>> getCorrectiveReports(
            @PathVariable String machineId
    ) {
        return ResponseEntity.ok(
                machineReportsQueryService.getCorrectiveReportsByMachineId(machineId)
        );
    }
}