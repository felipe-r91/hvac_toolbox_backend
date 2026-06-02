package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckReportTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckReportTaskRepository extends JpaRepository<HealthCheckReportTaskEntity, String> {
    void deleteByReportId(String reportId);
    List<HealthCheckReportTaskEntity> findByReportIdOrderByCategoryAscTaskNameAsc(String reportId);
}
