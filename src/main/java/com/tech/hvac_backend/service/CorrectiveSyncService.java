package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.sync.CorrectiveSyncRequest;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CorrectiveSyncService {

    private final CorrectiveDraftRepository correctiveDraftRepository;

    public CorrectiveSyncService(CorrectiveDraftRepository correctiveDraftRepository) {
        this.correctiveDraftRepository = correctiveDraftRepository;
    }

    @Transactional
    public boolean syncCorrectiveDraft(CorrectiveSyncRequest request) {
        validateRequest(request);

        if (correctiveDraftRepository.existsById(request.getId())) {
            return false;
        }

        CorrectiveDraftEntity draftEntity = mapDraft(request);
        correctiveDraftRepository.save(draftEntity);

        return true;
    }

    private void validateRequest(CorrectiveSyncRequest request) {
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
        if (isBlank(request.getFailureComponent())) {
            throw new IllegalArgumentException("Failure component is required.");
        }

        if (isBlank(request.getFailureMode())) {
            throw new IllegalArgumentException("Failure mode is required.");
        }

        if (isBlank(request.getFailureCode())) {
            throw new IllegalArgumentException("Failure code is required.");
        }
    }

    private CorrectiveDraftEntity mapDraft(CorrectiveSyncRequest request) {
        CorrectiveDraftEntity entity = new CorrectiveDraftEntity();
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

        entity.setFailureComponent(request.getFailureComponent());
        entity.setFailureMode(request.getFailureMode());
        entity.setFailureCode(request.getFailureCode());

        entity.setProblemSummary(request.getProblemSummary());
        entity.setConditionFound(request.getConditionFound());
        entity.setSymptomsObserved(request.getSymptomsObserved());
        entity.setAlarmsObserved(request.getAlarmsObserved());
        entity.setOperationalImpact(request.getOperationalImpact());

        entity.setPreliminaryDiagnosis(request.getPreliminaryDiagnosis());
        entity.setConfirmedCause(request.getConfirmedCause());

        entity.setCorrectiveAction(request.getCorrectiveAction());
        entity.setRecommendations(request.getRecommendations());
        entity.setFurtherActionRequired(request.getFurtherActionRequired());
        entity.setSourcePreventiveReportId(request.getSourcePreventiveReportId());

        entity.setMachineReturnedToService(
                isBlank(request.getMachineReturnedToService())
                        ? "unknown"
                        : request.getMachineReturnedToService()
        );

        entity.setSynced(Boolean.TRUE);

        return entity;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}