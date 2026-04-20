package com.tech.hvac_backend.dto.sync;

import com.tech.hvac_backend.dto.PreventiveTaskDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PreventiveSyncRequest {

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
    private String failureCode;
    private String failureNotes;
    private String linkedCorrectiveDraftId;
    private Integer faultCount;
    private Integer skippedCount;
    private List<PreventiveTaskDto> tasks;
    private String reportCategory;

    public PreventiveSyncRequest() {
    }

}