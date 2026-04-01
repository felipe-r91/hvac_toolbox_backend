package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreventiveReportTaskRepository extends JpaRepository<PreventiveReportTaskEntity, String> {
    void deleteByReportId(String reportId);
    List<PreventiveReportTaskEntity> findByReportIdOrderByCategoryAscTaskNameAsc(String reportId);
}