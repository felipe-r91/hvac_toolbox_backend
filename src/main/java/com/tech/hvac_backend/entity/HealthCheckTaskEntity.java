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
@Table(name = "health_check_tasks")
public class HealthCheckTaskEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 36)
    private String templateVersionId;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(nullable = false, length = 500)
    private String taskName;

    @Column(length = 30)
    private String status;

    @Column(length = 255)
    private String measuredValue;

    @Column(length = 30)
    private String unit;

    @Column(length = 4000)
    private String notes;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Boolean photoRequiredOnFault = true;

    @Column(nullable = false)
    private Boolean photoRequiredOnAttention = true;

    public HealthCheckTaskEntity() {
    }
}
