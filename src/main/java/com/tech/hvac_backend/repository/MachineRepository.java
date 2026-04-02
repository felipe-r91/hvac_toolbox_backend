package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineRepository extends JpaRepository<MachineEntity, String> {

    List<MachineEntity> findAllByOrderByTagAsc();

    List<MachineEntity> findByVesselIdOrderByTagAsc(String vesselId);
}