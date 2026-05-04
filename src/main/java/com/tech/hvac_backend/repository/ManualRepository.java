package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.ManualEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManualRepository extends JpaRepository<ManualEntity, UUID> {

    Optional<ManualEntity> findByManualNameAndMachineModel(
            String manualName,
            String machineModel
    );

    Optional<ManualEntity> findFirstByMachineModelIgnoreCase(
            String machineModel
    );
}