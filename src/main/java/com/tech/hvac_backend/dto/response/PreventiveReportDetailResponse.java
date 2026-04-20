package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PreventiveReportDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineType;
    private final String machineLocation;
    private final String machineStarterType;
    private final String completedAt;
    private final String overallStatus;
    private final String downtimeReason;
    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String failureNotes;
    private final String linkedCorrectiveDraftId;
    private final Integer faultCount;
    private final Integer skippedCount;
    private final Boolean synced;
    private final List<PreventiveReportTaskDetailResponse> tasks;
    private final String reportCategory;

    public PreventiveReportDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String machineId,
            String machineTag,
            String machineModel,
            String machineType,
            String machineLocation,
            String machineStarterType,
            String completedAt,
            String overallStatus,
            String downtimeReason,
            String failureComponent,
            String failureMode,
            String failureCode,
            String failureNotes,
            String linkedCorrectiveDraftId,
            Integer faultCount,
            Integer skippedCount,
            Boolean synced,
            List<PreventiveReportTaskDetailResponse> tasks,
            String reportCategory

    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineType = machineType;
        this.machineLocation = machineLocation;
        this.machineStarterType = machineStarterType;
        this.completedAt = completedAt;
        this.overallStatus = overallStatus;
        this.downtimeReason = downtimeReason;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.failureNotes = failureNotes;
        this.linkedCorrectiveDraftId = linkedCorrectiveDraftId;
        this.faultCount = faultCount;
        this.skippedCount = skippedCount;
        this.synced = synced;
        this.tasks = tasks;
        this.reportCategory = reportCategory;
    }

}