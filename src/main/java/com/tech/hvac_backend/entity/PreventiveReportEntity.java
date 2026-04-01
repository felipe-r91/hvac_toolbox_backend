package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "preventive_reports")
public class PreventiveReportEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String vesselId;

    @Column(nullable = false)
    private String vesselName;

    @Column(nullable = false)
    private String machineId;

    @Column(nullable = false)
    private String machineTag;

    @Column(nullable = false)
    private String machineModel;

    @Column(nullable = false)
    private String machineType;

    @Column(nullable = false)
    private String machineLocation;

    @Column(nullable = false)
    private String machineStarterType;

    @Column(nullable = false)
    private String completedAt;

    @Column(nullable = false)
    private String overallStatus;

    private String downtimeReason;
    private String failureComponent;
    private String failureMode;

    @Column(length = 4000)
    private String failureNotes;

    private Integer faultCount;
    private Integer skippedCount;
    private Boolean synced;

    public PreventiveReportEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineTag() {
        return machineTag;
    }

    public void setMachineTag(String machineTag) {
        this.machineTag = machineTag;
    }

    public String getMachineModel() {
        return machineModel;
    }

    public void setMachineModel(String machineModel) {
        this.machineModel = machineModel;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getMachineLocation() {
        return machineLocation;
    }

    public void setMachineLocation(String machineLocation) {
        this.machineLocation = machineLocation;
    }

    public String getMachineStarterType() {
        return machineStarterType;
    }

    public void setMachineStarterType(String machineStarterType) {
        this.machineStarterType = machineStarterType;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getDowntimeReason() {
        return downtimeReason;
    }

    public void setDowntimeReason(String downtimeReason) {
        this.downtimeReason = downtimeReason;
    }

    public String getFailureComponent() {
        return failureComponent;
    }

    public void setFailureComponent(String failureComponent) {
        this.failureComponent = failureComponent;
    }

    public String getFailureMode() {
        return failureMode;
    }

    public void setFailureMode(String failureMode) {
        this.failureMode = failureMode;
    }

    public String getFailureNotes() {
        return failureNotes;
    }

    public void setFailureNotes(String failureNotes) {
        this.failureNotes = failureNotes;
    }

    public Integer getFaultCount() {
        return faultCount;
    }

    public void setFaultCount(Integer faultCount) {
        this.faultCount = faultCount;
    }

    public Integer getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Integer skippedCount) {
        this.skippedCount = skippedCount;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }
}