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
@Table(name = "preventive_reports")
public class PreventiveReportEntity {

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
    private String machineLocation;

    @Column(nullable = false)
    private String machineStarterType;

    @Column(nullable = false)
    private String completedAt;

    @Column(nullable = false)
    private String overallStatus;

    private String downtimeReason;
    private String failureComponent;
    private String failureMode;
    private String failureCode;

    @Column(length = 4000)
    private String failureNotes;

    private Integer faultCount;
    private Integer skippedCount;
    private Boolean synced;

    public PreventiveReportEntity() {
    }

}