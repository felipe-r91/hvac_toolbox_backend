package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.CfrDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CfrDraftRepository extends JpaRepository<CfrDraftEntity, String> {
    List<CfrDraftEntity> findByMachineIdOrderByCreatedAtDesc(String machineId);
    List<CfrDraftEntity> findAllByOrderByCreatedAtDesc();
}