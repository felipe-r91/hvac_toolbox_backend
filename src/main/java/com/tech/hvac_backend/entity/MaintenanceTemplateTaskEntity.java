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
@Table(name = "maintenance_template_tasks")
public class MaintenanceTemplateTaskEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 36)
    private String templateVersionId;

    @Column(nullable = false, length = 80)
    private String taskCode;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 500)
    private String taskName;

    @Column(length = 255)
    private String tool;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean isRequired = true;

    @Column(nullable = false)
    private Boolean measurable = false;

    @Column(length = 30)
    private String defaultUnit;

    @Column(nullable = false)
    private Boolean photoRequiredOnFault = true;

    @Column(nullable = false)
    private Boolean photoRequiredOnAttention = true;
}