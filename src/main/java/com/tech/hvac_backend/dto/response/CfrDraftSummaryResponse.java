package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class CfrDraftSummaryResponse {

    private final String id;
    private final String vesselName;
    private final String machineTag;
    private final String machineModel;
    private final String machineLocation;
    private final String createdAt;
    private final String machineStatus;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;

    private final String conditionFound;
    private final String reportCategory;

    public CfrDraftSummaryResponse(
            String id,
            String vesselName,
            String machineTag,
            String machineModel,
            String machineLocation,
            String createdAt,
            String machineStatus,
            String failureComponent,
            String failureMode,
            String failureCode,
            String conditionFound,
            String reportCategory
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.machineStatus = machineStatus;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.conditionFound = conditionFound;
        this.reportCategory = reportCategory;
    }
}