package com.tech.hvac_backend.dto.sync;

import java.util.List;

public class PreventiveBatchSyncRequest {

    private List<PreventiveSyncRequest> reports;

    public PreventiveBatchSyncRequest() {
    }

    public List<PreventiveSyncRequest> getReports() {
        return reports;
    }

    public void setReports(List<PreventiveSyncRequest> reports) {
        this.reports = reports;
    }
}