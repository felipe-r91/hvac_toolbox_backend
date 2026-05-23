package com.tech.hvac_backend.dto.customerReport;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerReportResponse {

    private UUID id;
    private UUID sourceReportId;
    private String sourceReportType;
    private String vesselId;
    private String vesselName;
    private String vesselImo;
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
    private LocalDateTime reportDate;
    private String pdfFilename;
    private LocalDateTime createdAt;

}
