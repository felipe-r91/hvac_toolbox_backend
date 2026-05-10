package com.tech.hvac_backend.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class DailyDraftDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineSerialNumber;
    private final String machineType;
    private final String machineStarterType;
    private final String machineLocation;
    private final String machinePhotoId;
    private final String machinePhotoPreviewUrl;
    private final String createdAt;

    private final Boolean alarmPresent;
    private final String reportCategory;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;
    private final String failureNotes;

    private final String workConductedToday;
    private final String furtherActions;

    private final Boolean synced;
    private final List<CorrectivePhotoDetailResponse> photos;

    public DailyDraftDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String machineId,
            String machineTag,
            String machineModel,
            String machineSerialNumber,
            String machineType,
            String machineStarterType,
            String machineLocation,
            String machinePhotoId,
            String machinePhotoPreviewUrl,
            String createdAt,
            Boolean alarmPresent,
            String reportCategory,
            String failureComponent,
            String failureMode,
            String failureCode,
            String failureNotes,
            String workConductedToday,
            String furtherActions,
            Boolean synced,
            List<CorrectivePhotoDetailResponse> photos
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineSerialNumber = machineSerialNumber;
        this.machineType = machineType;
        this.machineStarterType = machineStarterType;
        this.machineLocation = machineLocation;
        this.machinePhotoId = machinePhotoId;
        this.machinePhotoPreviewUrl = machinePhotoPreviewUrl;
        this.createdAt = createdAt;
        this.alarmPresent = alarmPresent;
        this.reportCategory = reportCategory;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.failureNotes = failureNotes;
        this.workConductedToday = workConductedToday;
        this.furtherActions = furtherActions;
        this.synced = synced;
        this.photos = photos;
    }
}
