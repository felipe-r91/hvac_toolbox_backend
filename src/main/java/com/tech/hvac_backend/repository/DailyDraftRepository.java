package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.DailyDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyDraftRepository extends JpaRepository<DailyDraftEntity, String> {

    List<DailyDraftEntity> findAllByOrderByCreatedAtDesc();

    List<DailyDraftEntity> findByMachineIdOrderByCreatedAtDesc(String machineId);

    long countByMachineId(String machineId);
}
