package com.tech.hvac_backend.dto.sync;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DailySyncRequest {

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

    private Boolean alarmPresent;
    private String reportCategory;

    private String failureComponent;
    private String failureMode;
    private String failureCode;
    private String failureNotes;

    private String workConductedToday;
    private String furtherActions;

    private List<CorrectivePhotoDto> photos;
}
