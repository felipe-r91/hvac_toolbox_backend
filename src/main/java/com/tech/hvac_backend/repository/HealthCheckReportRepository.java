package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.HealthCheckReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthCheckReportRepository extends JpaRepository<HealthCheckReportEntity, String> {

    List<HealthCheckReportEntity> findAllByOrderByCompletedAtDesc();

    List<HealthCheckReportEntity> findByMachineIdOrderByCompletedAtDesc(String machineId);

    long countByMachineId(String machineId);
}
