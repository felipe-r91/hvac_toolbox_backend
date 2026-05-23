package com.tech.hvac_backend.dto.response;

import lombok.Getter;

@Getter
public class DailyDraftSummaryResponse {

    private final String id;
    private final String vesselName;
    private final String vesselImo;
    private final String machineTag;
    private final String machineModel;
    private final String machineLocation;
    private final String createdAt;
    private final Boolean alarmPresent;
    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String failureNotes;
    private final String workConductedToday;
    private final String reportCategory;

    public DailyDraftSummaryResponse(
            String id,
            String vesselName,
            String vesselImo,
            String machineTag,
            String machineModel,
            String machineLocation,
            String createdAt,
            Boolean alarmPresent,
            String failureComponent,
            String failureMode,
            String failureCode,
            String failureNotes,
            String workConductedToday,
            String reportCategory
    ) {
        this.id = id;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.alarmPresent = alarmPresent;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.failureNotes = failureNotes;
        this.workConductedToday = workConductedToday;
        this.reportCategory = reportCategory;
    }
}
