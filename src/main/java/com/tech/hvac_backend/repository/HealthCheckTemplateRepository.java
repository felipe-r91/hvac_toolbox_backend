package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthCheckTemplateRepository extends JpaRepository<HealthCheckTemplateEntity, String> {
    Optional<HealthCheckTemplateEntity> findByCode(String code);

    boolean existsByCode(String code);
}
