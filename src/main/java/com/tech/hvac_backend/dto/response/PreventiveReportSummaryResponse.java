package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class PreventiveReportSummaryResponse {

    private String id;
    private String vesselName;
    private String machineTag;
    private String machineModel;
    private String machineLocation;
    private String completedAt;
    private String overallStatus;
    private Integer faultCount;
    private Integer skippedCount;

    public PreventiveReportSummaryResponse(
            String id,
            String vesselName,
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
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.completedAt = completedAt;
        this.overallStatus = overallStatus;
        this.faultCount = faultCount;
        this.skippedCount = skippedCount;
    }

}