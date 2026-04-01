package com.tech.hvac_backend.controller;

import com.tech.hvac_backend.dto.BatchSyncItemResponse;
import com.tech.hvac_backend.dto.sync.BatchSyncResponse;
import com.tech.hvac_backend.dto.sync.PreventiveBatchSyncRequest;
import com.tech.hvac_backend.dto.sync.PreventiveSyncRequest;
import com.tech.hvac_backend.dto.sync.CorrectiveSyncRequest;
import com.tech.hvac_backend.dto.sync.CorrectiveBatchSyncRequest;
import com.tech.hvac_backend.dto.SyncResponse;
import com.tech.hvac_backend.service.PreventiveSyncService;
import com.tech.hvac_backend.service.CorrectiveSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final PreventiveSyncService preventiveSyncService;
    private final CorrectiveSyncService correctiveSyncService;

    public SyncController(
            PreventiveSyncService preventiveSyncService,
            CorrectiveSyncService correctiveSyncService
    ) {
        this.preventiveSyncService = preventiveSyncService;
        this.correctiveSyncService = correctiveSyncService;
    }

    @PostMapping("/preventive")
    public ResponseEntity<SyncResponse> syncPreventive(
            @RequestBody PreventiveSyncRequest request
    ) {
        boolean created = preventiveSyncService.syncPreventiveReport(request);

        if (!created) {
            SyncResponse response = new SyncResponse(
                    "already_synced",
                    request.getId(),
                    "Preventive report was already synced previously."
            );

            return ResponseEntity.ok(response);
        }

        SyncResponse response = new SyncResponse(
                "success",
                request.getId(),
                "Preventive report synced successfully."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/preventive/batch")
    public ResponseEntity<BatchSyncResponse> syncPreventiveBatch(
            @RequestBody PreventiveBatchSyncRequest request
    ) {
        if (request == null || request.getReports() == null || request.getReports().isEmpty()) {
            BatchSyncResponse response = new BatchSyncResponse(
                    "invalid_request",
                    0,
                    0,
                    0,
                    0,
                    List.of()
            );

            return ResponseEntity.badRequest().body(response);
        }

        List<BatchSyncItemResponse> items = new ArrayList<>();

        int created = 0;
        int alreadySynced = 0;
        int failed = 0;

        for (PreventiveSyncRequest report : request.getReports()) {
            try {
                boolean inserted = preventiveSyncService.syncPreventiveReport(report);

                if (inserted) {
                    created++;
                    items.add(new BatchSyncItemResponse(
                            report.getId(),
                            "success",
                            "Preventive report synced successfully."
                    ));
                } else {
                    alreadySynced++;
                    items.add(new BatchSyncItemResponse(
                            report.getId(),
                            "already_synced",
                            "Preventive report was already synced previously."
                    ));
                }
            } catch (Exception ex) {
                failed++;
                items.add(new BatchSyncItemResponse(
                        report != null ? report.getId() : null,
                        "failed",
                        ex.getMessage()
                ));
            }
        }

        BatchSyncResponse response = new BatchSyncResponse(
                "completed",
                request.getReports().size(),
                created,
                alreadySynced,
                failed,
                items
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/corrective")
    public ResponseEntity<SyncResponse> syncCorrective(
            @RequestBody CorrectiveSyncRequest request
    ) {
        boolean created = correctiveSyncService.syncCorrectiveDraft(request);

        if (!created) {
            SyncResponse response = new SyncResponse(
                    "already_synced",
                    request.getId(),
                    "Corrective draft was already synced previously."
            );

            return ResponseEntity.ok(response);
        }

        SyncResponse response = new SyncResponse(
                "success",
                request.getId(),
                "Corrective draft synced successfully."
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/corrective/batch")
    public ResponseEntity<BatchSyncResponse> syncCorrectiveBatch(
            @RequestBody CorrectiveBatchSyncRequest request
    ) {
        if (request == null || request.getDrafts() == null || request.getDrafts().isEmpty()) {
            BatchSyncResponse response = new BatchSyncResponse(
                    "invalid_request",
                    0,
                    0,
                    0,
                    0,
                    List.of()
            );

            return ResponseEntity.badRequest().body(response);
        }

        List<BatchSyncItemResponse> items = new ArrayList<>();

        int created = 0;
        int alreadySynced = 0;
        int failed = 0;

        for (CorrectiveSyncRequest draft : request.getDrafts()) {
            try {
                boolean inserted = correctiveSyncService.syncCorrectiveDraft(draft);

                if (inserted) {
                    created++;
                    items.add(new BatchSyncItemResponse(
                            draft.getId(),
                            "success",
                            "Corrective draft synced successfully."
                    ));
                } else {
                    alreadySynced++;
                    items.add(new BatchSyncItemResponse(
                            draft.getId(),
                            "already_synced",
                            "Corrective draft was already synced previously."
                    ));
                }
            } catch (Exception ex) {
                failed++;
                items.add(new BatchSyncItemResponse(
                        draft != null ? draft.getId() : null,
                        "failed",
                        ex.getMessage()
                ));
            }
        }

        BatchSyncResponse response = new BatchSyncResponse(
                "completed",
                request.getDrafts().size(),
                created,
                alreadySynced,
                failed,
                items
        );

        return ResponseEntity.ok(response);
    }
}