package com.tech.hvac_backend.dto.customerReport;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCustomerReportRequest {

    private UUID sourceReportId;
    private String sourceReportType; // cfr, corrective, health_check, daily

    private String vesselId;
    private String vesselName;
    private String vesselType;
    private String ownerCustomer;
    private String vesselContact;

    private String machineId;
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
    private String machineStatus;

    private String title;

    private String createdBy;
}
