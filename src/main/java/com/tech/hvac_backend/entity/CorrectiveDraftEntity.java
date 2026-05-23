package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "corrective_drafts")
public class CorrectiveDraftEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String vesselId;

    @Column(nullable = false)
    private String vesselName;

    private String vesselImo;
    private String vesselType;
    private String ownerCustomer;
    private String vesselContact;

    @Column(nullable = false)
    private String machineId;

    @Column(nullable = false)
    private String machineTag;

    private String machineModel;
    private String machineSerialNumber;
    private String machineType;
    private String machineStarterType;
    private String machineLocation;
    private String machineRefrigerant;
    private String machineOilType;
    private String machineControlSystem;
    private String machineSoftwareVersion;
    private String machineCompressorType;
    private String machineMfg;

    @Column(nullable = false)
    private String createdAt;

    private String failureComponent;
    private String failureMode;
    private String failureCode;
    private String sourcePreventiveReportId;

    @Column(nullable = false, length = 30)
    private String reportCategory;

    @Column(length = 4000)
    private String problemSummary;

    @Column(length = 4000)
    private String conditionFound;

    @Column(length = 4000)
    private String symptomsObserved;

    @Column(length = 4000)
    private String alarmsObserved;

    @Column(length = 4000)
    private String operationalImpact;

    @Column(length = 4000)
    private String preliminaryDiagnosis;

    @Column(length = 4000)
    private String confirmedCause;

    @Column(length = 4000)
    private String correctiveAction;

    @Column(length = 4000)
    private String recommendations;

    @Column(length = 4000)
    private String furtherActionRequired;

    private String machineReturnedToService;

    @Column(nullable = false)
    private Boolean synced;

    public CorrectiveDraftEntity() {
    }
}
