package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CorrectiveSyncRequest {

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

    private String failureComponent;
    private String failureMode;
    private String failureCode;

    private String problemSummary;
    private String conditionFound;
    private String symptomsObserved;
    private String alarmsObserved;
    private String operationalImpact;

    private String preliminaryDiagnosis;
    private String confirmedCause;

    private String correctiveAction;
    private String recommendations;
    private String furtherActionRequired;

    private String machineReturnedToService;

    private String sourcePreventiveReportId;

    private List<CorrectivePhotoDto> photos;

    private String reportCategory;

    public CorrectiveSyncRequest() {
    }

}