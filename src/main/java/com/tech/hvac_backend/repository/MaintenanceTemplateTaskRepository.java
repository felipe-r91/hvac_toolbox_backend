package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.MaintenanceTemplateTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceTemplateTaskRepository extends JpaRepository<MaintenanceTemplateTaskEntity, String> {
    List<MaintenanceTemplateTaskEntity> findByTemplateVersionIdOrderBySortOrderAsc(String templateVersionId);
}