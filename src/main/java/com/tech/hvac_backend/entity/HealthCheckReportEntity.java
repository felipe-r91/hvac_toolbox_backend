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
@Table(name = "health_check_reports")
public class HealthCheckReportEntity {

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
    private String machineLocation;

    @Column(nullable = false)
    private String machineStarterType;

    private String machineRefrigerant;
    private String machineOilType;
    private String machineControlSystem;
    private String machineSoftwareVersion;
    private String machineCompressorType;
    private String machineMfg;

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

    public HealthCheckReportEntity() {
    }
}
