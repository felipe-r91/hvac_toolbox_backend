package com.tech.hvac_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "health_check_report_tasks")
public class HealthCheckReportTaskEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String reportId;

    @Column(nullable = false)
    private String taskTemplateId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 2000)
    private String taskName;

    private String tool;
    private Boolean checked;
    private String status;

    @Column(length = 4000)
    private String notes;

    private String measuredValue;
    private String unit;

    @Convert(converter = StringListJsonConverter.class)
    @Column(length = 4000)
    private List<String> photoIds;

    private String completedAt;

    public HealthCheckReportTaskEntity() {
    }
}
