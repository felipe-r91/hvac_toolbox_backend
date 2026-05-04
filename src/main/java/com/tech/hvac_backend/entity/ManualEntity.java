package com.tech.hvac_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "manuals")
public class ManualEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "manual_name", nullable = false)
    private String manualName;

    @Column(name = "machine_model", nullable = false)
    private String machineModel;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}