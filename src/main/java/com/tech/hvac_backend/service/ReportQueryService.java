package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.*;
import com.tech.hvac_backend.entity.*;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReportQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final PreventiveReportTaskRepository preventiveReportTaskRepository;
    private final HealthCheckReportRepository healthCheckReportRepository;
    private final HealthCheckReportTaskRepository healthCheckReportTaskRepository;
    private final ServiceReportDraftRepository serviceReportDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final CfrDraftRepository cfrDraftRepository;
    private final DailyDraftRepository dailyDraftRepository;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;

    public ReportQueryService(
            PreventiveReportRepository preventiveReportRepository,
            PreventiveReportTaskRepository preventiveReportTaskRepository,
            HealthCheckReportRepository healthCheckReportRepository,
            HealthCheckReportTaskRepository healthCheckReportTaskRepository,
            ServiceReportDraftRepository serviceReportDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            CfrDraftRepository cfrDraftRepository,
            DailyDraftRepository dailyDraftRepository,
            MachineRepository machineRepository,
            VesselRepository vesselRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.preventiveReportTaskRepository = preventiveReportTaskRepository;
        this.healthCheckReportRepository = healthCheckReportRepository;
        this.healthCheckReportTaskRepository = healthCheckReportTaskRepository;
        this.serviceReportDraftRepository = serviceReportDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.cfrDraftRepository = cfrDraftRepository;
        this.dailyDraftRepository = dailyDraftRepository;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
    }

    public List<PreventiveReportSummaryResponse> getAllPreventiveReports() {
        return preventiveReportRepository.findAllByOrderByCompletedAtDesc()
                .stream()
                .filter(this::isMachineMaintenance)
                .map(this::mapPreventiveSummary)
                .toList();
    }

    public List<PreventiveReportSummaryResponse> getAllHealthCheckReports() {
        return healthCheckReportRepository.findAllByOrderByCompletedAtDesc()
                .stream()
                .map(this::mapHealthCheckSummary)
                .toList();
    }

    public List<ServiceReportDraftSummaryResponse> getAllServiceReportDrafts() {
        return serviceReportDraftRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapServiceReportSummary)
                .toList();
    }

    public List<CfrDraftSummaryResponse> getAllCfrDrafts() {
        return cfrDraftRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapCfrSummary)
                .toList();
    }

    public List<DailyDraftSummaryResponse> getAllDailyDrafts() {
        return dailyDraftRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapDailySummary)
                .toList();
    }

    public PreventiveReportDetailResponse getPreventiveReportById(String id) {
        PreventiveReportEntity report = preventiveReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preventive report not found: " + id));

        MachineEntity machine = getMachine(report.getMachineId());
        VesselEntity vessel = getVessel(report.getVesselId());

        List<PreventiveReportTaskDetailResponse> tasks = preventiveReportTaskRepository
                .findByReportIdOrderByCategoryAscTaskNameAsc(id)
                .stream()
                .map(this::mapPreventiveTaskDetail)
                .toList();

        return new PreventiveReportDetailResponse(
                report.getId(),
                report.getVesselId(),
                report.getVesselName(),
                resolveVesselImo(report.getVesselImo(), vessel),
                prefer(report.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(report.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(report.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                report.getMachineId(),
                report.getMachineTag(),
                report.getMachineModel(),
                prefer(report.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                report.getMachineType(),
                report.getMachineLocation(),
                report.getMachineStarterType(),
                prefer(report.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(report.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(report.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(report.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(report.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(report.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                report.getCompletedAt(),
                report.getOverallStatus(),
                report.getDowntimeReason(),
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getFailureNotes(),
                report.getLinkedServiceReportDraftId(),
                report.getFaultCount(),
                report.getSkippedCount(),
                report.getSynced(),
                tasks,
                report.getReportCategory()
        );
    }

    public PreventiveReportDetailResponse getHealthCheckReportById(String id) {
        HealthCheckReportEntity report = healthCheckReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Health check report not found: " + id));

        MachineEntity machine = getMachine(report.getMachineId());
        VesselEntity vessel = getVessel(report.getVesselId());

        List<PreventiveReportTaskDetailResponse> tasks = healthCheckReportTaskRepository
                .findByReportIdOrderByCategoryAscTaskNameAsc(id)
                .stream()
                .map(this::mapHealthCheckTaskDetail)
                .toList();

        return new PreventiveReportDetailResponse(
                report.getId(),
                report.getVesselId(),
                report.getVesselName(),
                resolveVesselImo(report.getVesselImo(), vessel),
                prefer(report.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(report.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(report.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                report.getMachineId(),
                report.getMachineTag(),
                report.getMachineModel(),
                prefer(report.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                report.getMachineType(),
                report.getMachineLocation(),
                report.getMachineStarterType(),
                prefer(report.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(report.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(report.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(report.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(report.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(report.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                report.getCompletedAt(),
                report.getOverallStatus(),
                report.getDowntimeReason(),
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getFailureNotes(),
                null,
                report.getFaultCount(),
                report.getSkippedCount(),
                report.getSynced(),
                tasks,
                HealthCheckSyncService.REPORT_CATEGORY
        );
    }

    public ServiceReportDraftDetailResponse getServiceReportDraftById(String id) {
        ServiceReportDraftEntity draft = serviceReportDraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service report draft not found: " + id));

        MachineEntity machine = getMachine(draft.getMachineId());
        VesselEntity vessel = getVessel(draft.getVesselId());

        List<PhotoDetailResponse> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.SERVICE_REPORT_DRAFT, id)
                .stream()
                .map(this::mapPhotoDetail)
                .toList();

        return new ServiceReportDraftDetailResponse(
                draft.getId(),
                draft.getVesselId(),
                draft.getVesselName(),
                resolveVesselImo(draft.getVesselImo(), vessel),
                prefer(draft.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(draft.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(draft.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                draft.getMachineId(),
                draft.getMachineTag(),
                draft.getMachineModel(),
                prefer(draft.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getMachineLocation(),
                prefer(draft.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(draft.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(draft.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(draft.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(draft.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(draft.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                draft.getCreatedAt(),
                draft.getWorkPerformed(),
                draft.getRecommendations(),
                draft.getFurtherActionRequired(),
                draft.getSourcePreventiveReportId(),
                draft.getMachineReturnedToService(),
                draft.getSynced(),
                photos,
                draft.getReportCategory()
        );
    }

    public CfrDraftDetailResponse getCfrDraftById(String id) {
        CfrDraftEntity draft = cfrDraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CFR draft not found: " + id));

        MachineEntity machine = getMachine(draft.getMachineId());
        VesselEntity vessel = getVessel(draft.getVesselId());

        List<PhotoDetailResponse> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.CFR_DRAFT, id)
                .stream()
                .map(this::mapPhotoDetail)
                .toList();

        return new CfrDraftDetailResponse(
                draft.getId(),
                draft.getVesselId(),
                draft.getVesselName(),
                resolveVesselImo(draft.getVesselImo(), vessel),
                prefer(draft.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(draft.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(draft.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                draft.getMachineId(),
                draft.getMachineTag(),
                draft.getMachineModel(),
                prefer(draft.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getMachineLocation(),
                prefer(draft.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(draft.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(draft.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(draft.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(draft.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(draft.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                draft.getCreatedAt(),
                draft.getMachineStatus(),
                draft.getDowntimeReason(),
                draft.getReportCategory(),
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                draft.getConditionFound(),
                draft.getSymptomsObserved(),
                draft.getAlarmsObserved(),
                draft.getOperationalImpact(),
                draft.getPreliminaryDiagnosis(),
                draft.getConfirmedCause(),
                draft.getRecommendations(),
                draft.getFurtherActionRequired(),
                draft.isSynced(),
                photos
        );
    }

    public DailyDraftDetailResponse getDailyDraftById(String id) {
        DailyDraftEntity draft = dailyDraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Daily draft not found: " + id));

        MachineEntity machine = getMachine(draft.getMachineId());
        VesselEntity vessel = getVessel(draft.getVesselId());

        List<PhotoDetailResponse> photos = photoRecordRepository
                .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(PhotoOwnerType.DAILY_DRAFT, id)
                .stream()
                .map(this::mapPhotoDetail)
                .toList();

        return new DailyDraftDetailResponse(
                draft.getId(),
                draft.getVesselId(),
                draft.getVesselName(),
                resolveVesselImo(draft.getVesselImo(), vessel),
                prefer(draft.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(draft.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(draft.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                draft.getMachineId(),
                draft.getMachineTag(),
                draft.getMachineModel(),
                prefer(draft.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getMachineLocation(),
                prefer(draft.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(draft.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(draft.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(draft.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(draft.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(draft.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                draft.getCreatedAt(),
                draft.isAlarmPresent(),
                draft.getReportCategory(),
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                draft.getFailureNotes(),
                draft.getWorkConductedToday(),
                draft.getFurtherActions(),
                draft.getSynced(),
                photos
        );
    }

    private PreventiveReportSummaryResponse mapPreventiveSummary(PreventiveReportEntity entity) {
        VesselEntity vessel = getVessel(entity.getVesselId());

        return new PreventiveReportSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                resolveVesselImo(entity.getVesselImo(), vessel),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCompletedAt(),
                entity.getOverallStatus(),
                entity.getFaultCount(),
                entity.getSkippedCount()
        );
    }

    private PreventiveReportSummaryResponse mapHealthCheckSummary(HealthCheckReportEntity entity) {
        VesselEntity vessel = getVessel(entity.getVesselId());

        return new PreventiveReportSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                resolveVesselImo(entity.getVesselImo(), vessel),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCompletedAt(),
                entity.getOverallStatus(),
                entity.getFaultCount(),
                entity.getSkippedCount()
        );
    }

    private ServiceReportDraftSummaryResponse mapServiceReportSummary(ServiceReportDraftEntity entity) {
        VesselEntity vessel = getVessel(entity.getVesselId());

        return new ServiceReportDraftSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                resolveVesselImo(entity.getVesselImo(), vessel),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCreatedAt(),
                entity.getWorkPerformed(),
                entity.getMachineReturnedToService(),
                entity.getReportCategory()
        );
    }

    private CfrDraftSummaryResponse mapCfrSummary(CfrDraftEntity entity) {
        VesselEntity vessel = getVessel(entity.getVesselId());

        return new CfrDraftSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                resolveVesselImo(entity.getVesselImo(), vessel),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCreatedAt(),
                entity.getMachineStatus(),
                entity.getFailureComponent(),
                entity.getFailureMode(),
                entity.getFailureCode(),
                entity.getConditionFound(),
                entity.getReportCategory()
        );

    }

    private DailyDraftSummaryResponse mapDailySummary(DailyDraftEntity entity) {
        VesselEntity vessel = getVessel(entity.getVesselId());

        return new DailyDraftSummaryResponse(
                entity.getId(),
                entity.getVesselName(),
                resolveVesselImo(entity.getVesselImo(), vessel),
                entity.getMachineTag(),
                entity.getMachineModel(),
                entity.getMachineLocation(),
                entity.getCreatedAt(),
                entity.isAlarmPresent(),
                entity.getFailureComponent(),
                entity.getFailureMode(),
                entity.getFailureCode(),
                entity.getFailureNotes(),
                entity.getWorkConductedToday(),
                entity.getReportCategory()
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
                entity.getPhotoIds(),
                mapTaskPhotos(entity.getPhotoIds()),
                entity.getCompletedAt()
        );
    }

    private PreventiveReportTaskDetailResponse mapHealthCheckTaskDetail(HealthCheckReportTaskEntity entity) {
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
                entity.getPhotoIds(),
                mapTaskPhotos(entity.getPhotoIds()),
                entity.getCompletedAt()
        );
    }

    private boolean isMachineMaintenance(PreventiveReportEntity report) {
        return report.getReportCategory() == null
                || report.getReportCategory().isBlank()
                || !"health_check".equalsIgnoreCase(report.getReportCategory());
    }

    private MachineEntity getMachine(String machineId) {
        if (machineId == null || machineId.isBlank()) {
            return null;
        }
        return machineRepository.findById(machineId).orElse(null);
    }

    private VesselEntity getVessel(String vesselId) {
        if (vesselId == null || vesselId.isBlank()) {
            return null;
        }
        return vesselRepository.findById(vesselId).orElse(null);
    }

    private String prefer(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private String resolveVesselImo(String vesselImo, VesselEntity vessel) {
        return prefer(vesselImo, vessel != null ? vessel.getImoNumber() : null);
    }

    private PhotoDetailResponse mapPhotoDetail(PhotoRecordEntity entity) {
        return new PhotoDetailResponse(
                entity.getId(),
                entity.getFilename(),
                entity.getCaption(),
                entity.getCreatedAt(),
                "/api/photos/" + entity.getId()
        );
    }

    private List<PhotoDetailResponse> mapTaskPhotos(List<String> photoIds) {
        if (photoIds == null || photoIds.isEmpty()) {
            return List.of();
        }

        Map<String, PhotoRecordEntity> photosById = photoRecordRepository.findAllById(photoIds)
                .stream()
                .collect(Collectors.toMap(PhotoRecordEntity::getId, Function.identity()));

        return photoIds.stream()
                .map(photosById::get)
                .filter(Objects::nonNull)
                .map(this::mapPhotoDetail)
                .toList();
    }
}
