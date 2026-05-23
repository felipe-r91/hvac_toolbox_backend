package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceReportDraftRepository extends JpaRepository<ServiceReportDraftEntity, String> {

    List<ServiceReportDraftEntity> findAllByOrderByCreatedAtDesc();

    List<ServiceReportDraftEntity> findByMachineIdOrderByCreatedAtDesc(String machineId);

    long countByMachineId(String machineId);
}
