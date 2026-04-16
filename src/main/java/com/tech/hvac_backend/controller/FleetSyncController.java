package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.response.MachineResponse;
import com.tech.hvac_backend.dto.response.VesselResponse;
import com.tech.hvac_backend.dto.sync.FleetSyncRequest;
import com.tech.hvac_backend.service.FleetSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fleet")
public class FleetSyncController {

    private final FleetSyncService fleetSyncService;

    public FleetSyncController(FleetSyncService fleetSyncService) {
        this.fleetSyncService = fleetSyncService;
    }

    @PostMapping("/sync")
    public ResponseEntity<Void> syncFleet(@RequestBody FleetSyncRequest request) {
        fleetSyncService.syncFleet(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/vessels")
    public ResponseEntity<List<VesselResponse>> getVessels() {
        return ResponseEntity.ok(fleetSyncService.getAllVessels());
    }

    @GetMapping("/vessels/{id}")
    public ResponseEntity<VesselResponse> getVesselById(@PathVariable String id) {
        return ResponseEntity.ok(fleetSyncService.getVesselById(id));
    }

    @GetMapping("/machines")
    public ResponseEntity<List<MachineResponse>> getMachines() {
        return ResponseEntity.ok(fleetSyncService.getAllMachines());
    }
}