package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PreventiveReportDetailResponse {

    private String id;
    private String vesselId;
    private String vesselName;
    private String machineId;
    private String machineTag;
    private String machineModel;
    private String machineType;
    private String machineLocation;
    private String machineStarterType;
    private String completedAt;
    private String overallStatus;
    private String downtimeReason;
    private String failureComponent;
    private String failureMode;
    private String failureNotes;
    private Integer faultCount;
    private Integer skippedCount;
    private Boolean synced;
    private List<PreventiveReportTaskDetailResponse> tasks;

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
            String failureNotes,
            Integer faultCount,
            Integer skippedCount,
            Boolean synced,
            List<PreventiveReportTaskDetailResponse> tasks
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
        this.failureNotes = failureNotes;
        this.faultCount = faultCount;
        this.skippedCount = skippedCount;
        this.synced = synced;
        this.tasks = tasks;
    }

}