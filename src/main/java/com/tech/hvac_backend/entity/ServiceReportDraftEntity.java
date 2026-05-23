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
@Table(name = "service_report_drafts")
public class ServiceReportDraftEntity {

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

    private String sourcePreventiveReportId;

    @Column(nullable = false, length = 30)
    private String reportCategory;

    @Column(length = 4000)
    private String workPerformed;

    @Column(length = 4000)
    private String recommendations;

    @Column(length = 4000)
    private String furtherActionRequired;

    private String machineReturnedToService;

    @Column(nullable = false)
    private Boolean synced;

    public ServiceReportDraftEntity() {
    }
}
