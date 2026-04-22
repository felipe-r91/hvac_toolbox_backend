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

    @Column(nullable = false)
    private String machineId;

    @Column(nullable = false)
    private String machineTag;

    @Column(nullable = false)
    private String machineModel;

    @Column(nullable = false)
    private String machineType;

    @Column(nullable = false)
    private String machineStarterType;

    @Column(nullable = false)
    private String machineLocation;

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