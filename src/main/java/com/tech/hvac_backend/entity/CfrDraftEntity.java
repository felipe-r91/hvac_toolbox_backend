package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cfr_drafts")
public class CfrDraftEntity {

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

    @Column(nullable = false)
    private String machineModel;

    private String machineSerialNumber;

    @Column(nullable = false)
    private String machineType;

    @Column(nullable = false)
    private String machineStarterType;

    @Column(nullable = false)
    private String machineLocation;

    private String machineRefrigerant;
    private String machineOilType;
    private String machineControlSystem;
    private String machineSoftwareVersion;
    private String machineCompressorType;
    private String machineMfg;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private String machineStatus; // online | down

    @Column(nullable = false)
    private String reportCategory; // cfr

    private String failureComponent;
    private String failureMode;
    private String failureCode;

    @Column(columnDefinition = "TEXT")
    private String conditionFound;

    @Column(columnDefinition = "TEXT")
    private String symptomsObserved;

    @Column(columnDefinition = "TEXT")
    private String alarmsObserved;

    @Column(columnDefinition = "TEXT")
    private String operationalImpact;

    @Column(columnDefinition = "TEXT")
    private String preliminaryDiagnosis;

    @Column(columnDefinition = "TEXT")
    private String confirmedCause;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String furtherActionRequired;

    @Column(nullable = false)
    private boolean synced;
}
