package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HealthCheckTemplateVersionRepository extends JpaRepository<HealthCheckTemplateVersionEntity, String> {
    List<HealthCheckTemplateVersionEntity> findByTemplateIdOrderByVersionNumberDesc(String templateId);

    Optional<HealthCheckTemplateVersionEntity> findByTemplateIdAndVersionNumber(String templateId, Integer versionNumber);

    Optional<HealthCheckTemplateVersionEntity> findFirstByTemplateIdAndIsPublishedTrueOrderByVersionNumberDesc(String templateId);
}
