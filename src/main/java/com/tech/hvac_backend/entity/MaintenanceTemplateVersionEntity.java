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
@Table(name = "maintenance_template_versions")
public class MaintenanceTemplateVersionEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 36)
    private String templateId;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Boolean isPublished = true;
}