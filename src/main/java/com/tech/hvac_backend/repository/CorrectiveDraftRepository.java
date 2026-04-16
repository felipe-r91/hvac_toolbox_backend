package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectiveDraftRepository extends JpaRepository<CorrectiveDraftEntity, String> {

    List<CorrectiveDraftEntity> findAllByOrderByCreatedAtDesc();

    List<CorrectiveDraftEntity> findByMachineIdOrderByCreatedAtDesc(String machineId);

    long countByMachineId(String machineId);
}