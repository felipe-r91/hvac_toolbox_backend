package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CfrSyncRequest {

    private String id;
    private String vesselId;
    private String vesselName;
    private String machineId;
    private String machineTag;
    private String machineModel;
    private String machineType;
    private String machineStarterType;
    private String machineLocation;
    private String createdAt;

    private String machineStatus;
    private String reportCategory;

    private String failureComponent;
    private String failureMode;
    private String failureCode;

    private String conditionFound;
    private String symptomsObserved;
    private String alarmsObserved;
    private String operationalImpact;
    private String preliminaryDiagnosis;
    private String confirmedCause;
    private String recommendations;
    private String furtherActionRequired;

    private List<CfrPhotoRequest> photos;

    @Getter
    @Setter
    public static class CfrPhotoRequest {
        private String id;
        private String filename;
        private String caption;
        private String createdAt;
        private String previewUrl;
    }
}