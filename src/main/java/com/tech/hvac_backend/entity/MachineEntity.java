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
@Table(name = "machines")
public class MachineEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String vesselId;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String serialNumber;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String starterType;

    @Column(length = 36)
    private String machineTemplateVersionId;

    @Column(length = 36)
    private String starterTemplateVersionId;

    public MachineEntity() {
    }
}