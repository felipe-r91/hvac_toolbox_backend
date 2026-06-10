package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.PartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartRepository extends JpaRepository<PartEntity, String> {

    boolean existsByJciPartNumberIgnoreCase(String jciPartNumber);

    boolean existsByJciPartNumberIgnoreCaseAndIdNot(String jciPartNumber, String id);

    List<PartEntity> findAllByOrderByJciPartNumberAsc();
}
