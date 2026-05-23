package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class PreventiveReportSummaryResponse {

    private final String id;
    private final String vesselName;
    private final String vesselImo;
    private final String machineTag;
    private final String machineModel;
    private final String machineLocation;
    private final String completedAt;
    private final String overallStatus;
    private final Integer faultCount;
    private final Integer skippedCount;

    public PreventiveReportSummaryResponse(
            String id,
            String vesselName,
            String vesselImo,
            String machineTag,
            String machineModel,
            String machineLocation,
            String completedAt,
            String overallStatus,
            Integer faultCount,
            Integer skippedCount
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.completedAt = completedAt;
        this.overallStatus = overallStatus;
        this.faultCount = faultCount;
        this.skippedCount = skippedCount;
    }

}
