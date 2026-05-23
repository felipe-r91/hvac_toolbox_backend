package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class CorrectiveDraftSummaryResponse {

    private final String id;
    private final String vesselName;
    private final String vesselImo;
    private final String machineTag;
    private final String machineModel;
    private final String machineLocation;
    private final String createdAt;
    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String problemSummary;
    private final String machineReturnedToService;
    private final String reportCategory;

    public CorrectiveDraftSummaryResponse(
            String id,
            String vesselName,
            String vesselImo,
            String machineTag,
            String machineModel,
            String machineLocation,
            String createdAt,
            String failureComponent,
            String failureMode,
            String failureCode,
            String problemSummary,
            String machineReturnedToService, String reportCategory
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.problemSummary = problemSummary;
        this.machineReturnedToService = machineReturnedToService;
        this.reportCategory = reportCategory;
    }

}
