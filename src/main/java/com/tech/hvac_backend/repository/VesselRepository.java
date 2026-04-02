package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.VesselEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VesselRepository extends JpaRepository<VesselEntity, String> {

    List<VesselEntity> findAllByOrderByNameAsc();
}