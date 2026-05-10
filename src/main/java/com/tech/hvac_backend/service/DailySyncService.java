package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.sync.DailySyncRequest;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailySyncService {

    private final DailyDraftRepository dailyDraftRepository;

    public DailySyncService(DailyDraftRepository dailyDraftRepository) {
        this.dailyDraftRepository = dailyDraftRepository;
    }

    @Transactional
    public boolean syncDailyDraft(DailySyncRequest request) {
        validateRequest(request);

        if (dailyDraftRepository.existsById(request.getId())) {
            return false;
        }

        DailyDraftEntity draftEntity = mapDraft(request);
        dailyDraftRepository.save(draftEntity);

        return true;
    }

    private void validateRequest(DailySyncRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        if (isBlank(request.getId())) {
            throw new IllegalArgumentException("Draft id is required.");
        }

        if (isBlank(request.getVesselId())) {
            throw new IllegalArgumentException("Vessel id is required.");
        }

        if (isBlank(request.getMachineId())) {
            throw new IllegalArgumentException("Machine id is required.");
        }

        if (isBlank(request.getCreatedAt())) {
            throw new IllegalArgumentException("CreatedAt is required.");
        }
    }

    private DailyDraftEntity mapDraft(DailySyncRequest request) {
        DailyDraftEntity entity = new DailyDraftEntity();
        entity.setId(request.getId());
        entity.setVesselId(request.getVesselId());
        entity.setVesselName(request.getVesselName());
        entity.setMachineId(request.getMachineId());
        entity.setMachineTag(request.getMachineTag());
        entity.setMachineModel(request.getMachineModel());
        entity.setMachineType(request.getMachineType());
        entity.setMachineStarterType(request.getMachineStarterType());
        entity.setMachineLocation(request.getMachineLocation());
        entity.setCreatedAt(request.getCreatedAt());

        entity.setAlarmPresent(Boolean.TRUE.equals(request.getAlarmPresent()));
        entity.setReportCategory(isBlank(request.getReportCategory()) ? "daily" : request.getReportCategory());

        entity.setFailureComponent(request.getFailureComponent());
        entity.setFailureMode(request.getFailureMode());
        entity.setFailureCode(request.getFailureCode());
        entity.setFailureNotes(request.getFailureNotes());

        entity.setWorkConductedToday(request.getWorkConductedToday());
        entity.setFurtherActions(request.getFurtherActions());
        entity.setSynced(Boolean.TRUE);

        return entity;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
