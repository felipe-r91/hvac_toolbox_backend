package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PreventiveReportDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String vesselImo;
    private final String vesselType;
    private final String ownerCustomer;
    private final String vesselContact;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineSerialNumber;
    private final String machineType;
    private final String machineLocation;
    private final String machineStarterType;
    private final String machineRefrigerant;
    private final String machineOilType;
    private final String machineControlSystem;
    private final String machineSoftwareVersion;
    private final String machineCompressorType;
    private final String machineMfg;
    private final String machinePhotoId;
    private final String machinePhotoPreviewUrl;
    private final String completedAt;
    private final String overallStatus;
    private final String downtimeReason;
    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String failureNotes;
    private final String linkedServiceReportDraftId;
    private final Integer faultCount;
    private final Integer skippedCount;
    private final Boolean synced;
    private final List<PreventiveReportTaskDetailResponse> tasks;
    private final String reportCategory;

    public PreventiveReportDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String vesselImo,
            String vesselType,
            String ownerCustomer,
            String vesselContact,
            String machineId,
            String machineTag,
            String machineModel,
            String machineSerialNumber,
            String machineType,
            String machineLocation,
            String machineStarterType,
            String machineRefrigerant,
            String machineOilType,
            String machineControlSystem,
            String machineSoftwareVersion,
            String machineCompressorType,
            String machineMfg,
            String machinePhotoId,
            String machinePhotoPreviewUrl,
            String completedAt,
            String overallStatus,
            String downtimeReason,
            String failureComponent,
            String failureMode,
            String failureCode,
            String failureNotes,
            String linkedServiceReportDraftId,
            Integer faultCount,
            Integer skippedCount,
            Boolean synced,
            List<PreventiveReportTaskDetailResponse> tasks,
            String reportCategory

    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.vesselImo = vesselImo;
        this.vesselType = vesselType;
        this.ownerCustomer = ownerCustomer;
        this.vesselContact = vesselContact;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineSerialNumber = machineSerialNumber;
        this.machineType = machineType;
        this.machineLocation = machineLocation;
        this.machineStarterType = machineStarterType;
        this.machineRefrigerant = machineRefrigerant;
        this.machineOilType = machineOilType;
        this.machineControlSystem = machineControlSystem;
        this.machineSoftwareVersion = machineSoftwareVersion;
        this.machineCompressorType = machineCompressorType;
        this.machineMfg = machineMfg;
        this.machinePhotoId = machinePhotoId;
        this.machinePhotoPreviewUrl = machinePhotoPreviewUrl;
        this.completedAt = completedAt;
        this.overallStatus = overallStatus;
        this.downtimeReason = downtimeReason;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.failureNotes = failureNotes;
        this.linkedServiceReportDraftId = linkedServiceReportDraftId;
        this.faultCount = faultCount;
        this.skippedCount = skippedCount;
        this.synced = synced;
        this.tasks = tasks;
        this.reportCategory = reportCategory;
    }

}
