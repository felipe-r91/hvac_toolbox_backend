package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.CustomerReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerReportRepository extends JpaRepository<CustomerReportEntity, UUID> {

    List<CustomerReportEntity> findByVesselIdOrderByReportDateDesc(String vesselId);

    List<CustomerReportEntity> findByMachineIdOrderByReportDateDesc(String machineId);

    List<CustomerReportEntity> findByVesselIdAndMachineIdOrderByReportDateDesc(
            String vesselId,
            String machineId
    );

    List<CustomerReportEntity> findAllByOrderByReportDateDesc();

    List<CustomerReportEntity> findBySourceReportTypeOrderByReportDateDesc(String sourceReportType);

    boolean existsBySourceReportIdAndSourceReportType(UUID sourceReportId, String sourceReportType);


}