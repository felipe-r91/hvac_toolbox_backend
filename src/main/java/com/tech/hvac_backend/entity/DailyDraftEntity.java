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
@Table(name = "daily_drafts")
public class DailyDraftEntity {

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

    private String machineModel;
    private String machineType;
    private String machineStarterType;
    private String machineLocation;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    private boolean alarmPresent;

    @Column(nullable = false, length = 30)
    private String reportCategory;

    private String failureComponent;
    private String failureMode;
    private String failureCode;

    @Column(length = 4000)
    private String failureNotes;

    @Column(length = 4000)
    private String workConductedToday;

    @Column(length = 4000)
    private String furtherActions;

    @Column(nullable = false)
    private Boolean synced;
}
