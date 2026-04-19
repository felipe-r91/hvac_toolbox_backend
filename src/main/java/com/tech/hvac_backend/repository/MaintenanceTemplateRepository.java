package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.MaintenanceTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaintenanceTemplateRepository extends JpaRepository<MaintenanceTemplateEntity, String> {
    Optional<MaintenanceTemplateEntity> findByCode(String code);
}