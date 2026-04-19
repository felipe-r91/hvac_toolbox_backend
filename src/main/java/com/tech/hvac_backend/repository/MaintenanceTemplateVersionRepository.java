package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.MaintenanceTemplateVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceTemplateVersionRepository extends JpaRepository<MaintenanceTemplateVersionEntity, String> {
    List<MaintenanceTemplateVersionEntity> findByTemplateIdOrderByVersionNumberDesc(String templateId);

    Optional<MaintenanceTemplateVersionEntity> findByTemplateIdAndVersionNumber(String templateId, Integer versionNumber);
}