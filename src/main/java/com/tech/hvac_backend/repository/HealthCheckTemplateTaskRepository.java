package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckTemplateTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckTemplateTaskRepository extends JpaRepository<HealthCheckTemplateTaskEntity, String> {
    List<HealthCheckTemplateTaskEntity> findByTemplateVersionIdOrderBySortOrderAsc(String templateVersionId);
}
