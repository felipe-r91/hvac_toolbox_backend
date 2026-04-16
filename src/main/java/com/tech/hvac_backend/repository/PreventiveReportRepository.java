package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.PreventiveReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreventiveReportRepository extends JpaRepository<PreventiveReportEntity, String> {

    List<PreventiveReportEntity> findAllByOrderByCompletedAtDesc();

    List<PreventiveReportEntity> findByMachineIdOrderByCompletedAtDesc(String machineId);

    long countByMachineId(String machineId);
}