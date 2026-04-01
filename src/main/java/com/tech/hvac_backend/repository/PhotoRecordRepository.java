package com.tech.hvac_backend.repository;

import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRecordRepository extends JpaRepository<PhotoRecordEntity, String> {

    // Used for corrective drafts and preventive reports
    List<PhotoRecordEntity> findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
            PhotoOwnerType ownerType,
            String ownerId
    );

    // Used for preventive task photos
    List<PhotoRecordEntity> findByOwnerTypeAndOwnerIdAndTaskIdOrderByCreatedAtAsc(
            PhotoOwnerType ownerType,
            String ownerId,
            String taskId
    );

    // Useful for machine overview pages
    List<PhotoRecordEntity> findByMachineIdOrderByCreatedAtAsc(String machineId);

    // Useful when listing all photos of a machine by type
    List<PhotoRecordEntity> findByOwnerTypeAndMachineIdOrderByCreatedAtAsc(
            PhotoOwnerType ownerType,
            String machineId
    );

    // Useful when filtering by owner and machine together
    List<PhotoRecordEntity> findByOwnerTypeAndOwnerIdAndMachineIdOrderByCreatedAtAsc(
            PhotoOwnerType ownerType,
            String ownerId,
            String machineId
    );
}