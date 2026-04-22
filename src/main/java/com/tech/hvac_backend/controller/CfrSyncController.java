package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.sync.CfrSyncRequest;
import com.tech.hvac_backend.dto.response.CfrSyncResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.service.CfrSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin
public class CfrSyncController {

    private final CfrSyncService cfrSyncService;

    public CfrSyncController(CfrSyncService cfrSyncService) {
        this.cfrSyncService = cfrSyncService;
    }

    @PostMapping("/cfr")
    public ResponseEntity<CfrSyncResponse> syncCfr(@RequestBody CfrSyncRequest request) {
        CfrDraftEntity saved = cfrSyncService.sync(request);
        return ResponseEntity.ok(new CfrSyncResponse(saved.getId(), "synced"));
    }
}