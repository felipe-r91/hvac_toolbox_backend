package com.tech.hvac_backend.dto.response;

import lombok.Getter;
import java.util.List;

@Getter
public class CfrDraftDetailResponse {

    private final String id;
    private final String vesselId;
    private final String vesselName;
    private final String machineId;
    private final String machineTag;
    private final String machineModel;
    private final String machineType;
    private final String machineStarterType;
    private final String machineLocation;
    private final String createdAt;

    private final String machineStatus;
    private final String reportCategory;

    private final String failureComponent;
    private final String failureMode;
    private final String failureCode;

    private final String conditionFound;
    private final String symptomsObserved;
    private final String alarmsObserved;
    private final String operationalImpact;

    private final String preliminaryDiagnosis;
    private final String confirmedCause;

    private final String recommendations;
    private final String furtherActionRequired;

    private final Boolean synced;
    private final List<CorrectivePhotoDetailResponse> photos;

    public CfrDraftDetailResponse(
            String id,
            String vesselId,
            String vesselName,
            String machineId,
            String machineTag,
            String machineModel,
            String machineType,
            String machineStarterType,
            String machineLocation,
            String createdAt,
            String machineStatus,
            String reportCategory,
            String failureComponent,
            String failureMode,
            String failureCode,
            String conditionFound,
            String symptomsObserved,
            String alarmsObserved,
            String operationalImpact,
            String preliminaryDiagnosis,
            String confirmedCause,
            String recommendations,
            String furtherActionRequired,
            Boolean synced,
            List<CorrectivePhotoDetailResponse> photos
    ) {
        this.id = id;
        this.vesselId = vesselId;
        this.vesselName = vesselName;
        this.machineId = machineId;
        this.machineTag = machineTag;
        this.machineModel = machineModel;
        this.machineType = machineType;
        this.machineStarterType = machineStarterType;
        this.machineLocation = machineLocation;
        this.createdAt = createdAt;
        this.machineStatus = machineStatus;
        this.reportCategory = reportCategory;
        this.failureComponent = failureComponent;
        this.failureMode = failureMode;
        this.failureCode = failureCode;
        this.conditionFound = conditionFound;
        this.symptomsObserved = symptomsObserved;
        this.alarmsObserved = alarmsObserved;
        this.operationalImpact = operationalImpact;
        this.preliminaryDiagnosis = preliminaryDiagnosis;
        this.confirmedCause = confirmedCause;
        this.recommendations = recommendations;
        this.furtherActionRequired = furtherActionRequired;
        this.synced = synced;
        this.photos = photos;
    }
}