package com.tech.hvac_backend.dto.sync;

import com.tech.hvac_backend.dto.PreventiveTaskDto;
import lombok.Getter;

import java.util.List;

@Getter
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
    private Integer faultCount;
    private Integer skippedCount;
    private List<PreventiveTaskDto> tasks;

    public PreventiveSyncRequest() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public void setMachineTag(String machineTag) {
        this.machineTag = machineTag;
    }

    public void setMachineModel(String machineModel) {
        this.machineModel = machineModel;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public void setMachineLocation(String machineLocation) {
        this.machineLocation = machineLocation;
    }

    public void setMachineStarterType(String machineStarterType) {
        this.machineStarterType = machineStarterType;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public void setDowntimeReason(String downtimeReason) {
        this.downtimeReason = downtimeReason;
    }

    public void setFailureComponent(String failureComponent) {
        this.failureComponent = failureComponent;
    }

    public void setFailureMode(String failureMode) {
        this.failureMode = failureMode;
    }

    public void setFailureNotes(String failureNotes) {
        this.failureNotes = failureNotes;
    }

    public void setFaultCount(Integer faultCount) {
        this.faultCount = faultCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public void setTasks(List<PreventiveTaskDto> tasks) {
        this.tasks = tasks;
    }

}