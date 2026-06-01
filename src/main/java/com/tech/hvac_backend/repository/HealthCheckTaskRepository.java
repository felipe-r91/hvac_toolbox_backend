package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckTaskRepository extends JpaRepository<HealthCheckTaskEntity, String> {
    List<HealthCheckTaskEntity> findByTemplateVersionIdOrderBySortOrderAsc(String templateVersionId);
}
