package com.tech.hvac_backend.dto.sync;

import com.tech.hvac_backend.dto.BatchSyncItemResponse;

import java.time.Instant;
import java.util.List;

public class BatchSyncResponse {

    private String status;
    private Instant timestamp;
    private int total;
    private int created;
    private int alreadySynced;
    private int failed;
    private List<BatchSyncItemResponse> items;

    public BatchSyncResponse(
            String status,
            int total,
            int created,
            int alreadySynced,
            int failed,
            List<BatchSyncItemResponse> items
    ) {
        this.status = status;
        this.timestamp = Instant.now();
        this.total = total;
        this.created = created;
        this.alreadySynced = alreadySynced;
        this.failed = failed;
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getTotal() {
        return total;
    }

    public int getCreated() {
        return created;
    }

    public int getAlreadySynced() {
        return alreadySynced;
    }

    public int getFailed() {
        return failed;
    }

    public List<BatchSyncItemResponse> getItems() {
        return items;
    }
}