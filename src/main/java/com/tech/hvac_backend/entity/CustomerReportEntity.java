package com.tech.hvac_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "customer_reports")
public class CustomerReportEntity {

    @Id
    @GeneratedValue
    private UUID id;

    // Link to original report (CFR, corrective, daily, etc)
    @Setter
    @Column(name = "source_report_id")
    private UUID sourceReportId;

    @Setter
    @Column(name = "source_report_type", nullable = false)
    private String sourceReportType; // cfr, corrective, health_check, daily

    // Vessel info
    @Setter
    @Column(name = "vessel_id")
    private String vesselId;

    @Setter
    @Column(name = "vessel_name")
    private String vesselName;

    @Setter
    @Column(name = "vessel_type")
    private String vesselType;

    @Setter
    @Column(name = "owner_customer")
    private String ownerCustomer;

    @Setter
    @Column(name = "vessel_contact")
    private String vesselContact;

    // Machine info
    @Setter
    @Column(name = "machine_id")
    private String machineId;

    @Setter
    @Column(name = "machine_tag")
    private String machineTag;

    @Setter
    @Column(name = "machine_model")
    private String machineModel;

    @Setter
    @Column(name = "machine_serial_number")
    private String machineSerialNumber;

    @Setter
    @Column(name = "machine_type")
    private String machineType;

    @Setter
    @Column(name = "machine_starter_type")
    private String machineStarterType;

    @Setter
    @Column(name = "machine_location")
    private String machineLocation;

    @Setter
    @Column(name = "machine_refrigerant")
    private String machineRefrigerant;

    @Setter
    @Column(name = "machine_oil_type")
    private String machineOilType;

    @Setter
    @Column(name = "machine_control_system")
    private String machineControlSystem;

    @Setter
    @Column(name = "machine_software_version")
    private String machineSoftwareVersion;

    @Setter
    @Column(name = "machine_compressor_type")
    private String machineCompressorType;

    @Setter
    @Column(name = "machine_mfg")
    private String machineMfg;

    @Setter
    @Column(name = "machine_status")
    private String machineStatus;

    // Report info
    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    // PDF storage (R2 / S3)
    @Setter
    @Column(name = "pdf_bucket", nullable = false)
    private String pdfBucket;

    @Setter
    @Column(name = "pdf_object_key", nullable = false)
    private String pdfObjectKey;

    @Setter
    @Column(name = "pdf_filename", nullable = false)
    private String pdfFilename;

    // Audit
    @Setter
    @Column(name = "created_by")
    private String createdBy;

    @Setter
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ===== Lifecycle =====
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.reportDate == null) {
            this.reportDate = LocalDateTime.now();
        }
    }

}
