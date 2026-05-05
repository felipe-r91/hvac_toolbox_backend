package com.tech.hvac_backend.dto.customerReport;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateCustomerReportRequest {

    private UUID sourceReportId;
    private String sourceReportType; // cfr, corrective, health_check

    private String vesselId;
    private String vesselName;

    private String machineId;
    private String machineTag;
    private String machineModel;
    private String machineType;
    private String machineStatus;

    private String title;

    private String createdBy;
}