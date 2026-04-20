package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.CorrectiveDraftDetailResponse;
import com.tech.hvac_backend.dto.response.CorrectiveDraftSummaryResponse;
import com.tech.hvac_backend.dto.response.CorrectivePhotoDetailResponse;
import com.tech.hvac_backend.dto.response.PreventiveReportDetailResponse;
import com.tech.hvac_backend.dto.response.PreventiveReportSummaryResponse;
import com.tech.hvac_backend.dto.response.PreventiveReportTaskDetailResponse;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.PreventiveReportTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final PreventiveReportTaskRepository preventiveReportTaskRepository;
    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;

    public ReportQueryService(
            PreventiveReportRepository preventiveReportRepository,
            PreventiveReportTaskRepository preventiveReportTaskRepository,
            CorrectiveDraftRepository correctiveDraftRepository,
            PhotoRecordRepository photoRecordRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.preventiveReportTaskRepository = preventiveReportTaskRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
    }

    public List<PreventiveReportSummaryResponse> getAllPreventiveReports() {
        return preventiveReportRepository.findAllByOrderByCompletedAtDesc()
                .stream()
                .map(this::mapPreventiveSummary)
                .toList();
    }

    public List<CorrectiveDraftSummaryResponse> getAllCorrectiveDrafts() {
        return correctiveDraftRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapCorrectiveSummary)
                .toList();
    }

    public PreventiveReportDetailResponse getPreventiveReportById(String id) {
        PreventiveReportEntity report = preventiveReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preventive report not found: " + id));

        List<PreventiveReportTaskDetailResponse> tasks = preventiveReportTaskRepository
                .findByReportIdOrderByCategoryAscTaskNameAsc(id)
                .stream()
                .map(this::mapPreventiveTaskDetail)
                .toList();

        return new PreventiveReportDetailResponse(
                report.getId(),
                report.getVesselId(),
                report.getVesselName(),
                report.getMachineId(),
                report.getMachineTag(),
                report.getMachineModel(),
                report.getMachineType(),
                report.getMachineLocation(),
                report.getMachineStarterType(),
                report.getCompletedAt(),
                report.getOverallStatus(),
                report.getDowntimeReason(),
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getFailureNotes(),
                report.getLinkedCorrectiveDraftId(),
                report.getFaultCount(),
                report.getSkippedCount(),
                report.getSynced(),
                tasks,
                report.getReportCategory()
        );
    }

    public CorrectiveDraftDetailResponse getCorrectiveDraftById(String id) {
        CorrectiveDraftEntity draft = correctiveDraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corrective draft not found: " + id));

        List<CorrectivePhotoDetailResponse> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.CORRECTIVE_DRAFT, id)
                .stream()
                .map(this::mapCorrectivePhotoDetail)
                .toList();

        return new CorrectiveDraftDetailResponse(
                draft.getId(),
                draft.getVesselId(),
                draft.getVesselName(),
                draft.getMachineId(),
                draft.getMachineTag(),
                draft.getMachineModel(),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getMachineLocation(),
                draft.getCreatedAt(),
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                draft.getProblemSummary(),
                draft.getConditionFound(),
                draft.getSymptomsObserved(),
                draft.getAlarmsObserved(),
                draft.getOperationalImpact(),
                draft.getPreliminaryDiagnosis(),
                draft.getConfirmedCause(),
                draft.getCorrectiveAction(),
                draft.getRecommendations(),
                draft.getFurtherActionRequired(),
                draft.getMachineReturnedToService(),
                draft.getSourcePreventiveReportId(),
                draft.getSynced(),
                photos,
                draft.getReportCategory()
        );
    }

    private PreventiveReportSummaryResponse mapPreventiveSummary(PreventiveReportEntity entity) {
        return new PreventiveReportSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCompletedAt(),
                entity.getOverallStatus(),
                entity.getFaultCount(),
                entity.getSkippedCount()
        );
    }

    private CorrectiveDraftSummaryResponse mapCorrectiveSummary(CorrectiveDraftEntity entity) {
        return new CorrectiveDraftSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCreatedAt(),
                entity.getFailureComponent(),
                entity.getFailureMode(),
                entity.getFailureCode(),
                entity.getProblemSummary(),
                entity.getMachineReturnedToService()
        );
    }

    private PreventiveReportTaskDetailResponse mapPreventiveTaskDetail(PreventiveReportTaskEntity entity) {
        return new PreventiveReportTaskDetailResponse(
                entity.getId(),
                entity.getTaskTemplateId(),
                entity.getCategory(),
                entity.getTaskName(),
                entity.getTool(),
                entity.getChecked(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getMeasuredValue(),
                entity.getUnit(),
                entity.getCompletedAt()
        );
    }

    private CorrectivePhotoDetailResponse mapCorrectivePhotoDetail(PhotoRecordEntity entity) {
        return new CorrectivePhotoDetailResponse(
                entity.getId(),
                entity.getFilename(),
                entity.getCaption(),
                entity.getCreatedAt(),
                "/api/photos/" + entity.getId()
        );
    }
}