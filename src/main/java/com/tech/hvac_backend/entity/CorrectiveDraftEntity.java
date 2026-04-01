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

    @Column(nullable = false)
    private String machineReturnedToService;

    private Boolean synced;

    public CorrectiveDraftEntity() {
    }

}