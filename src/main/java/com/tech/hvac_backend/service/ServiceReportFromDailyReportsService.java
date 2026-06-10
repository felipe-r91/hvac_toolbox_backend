package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiServiceReportFromDailyReportsResult;
import com.tech.hvac_backend.dto.request.ServiceReportFromDailyReportsRequest;
import com.tech.hvac_backend.dto.response.PhotoDetailResponse;
import com.tech.hvac_backend.dto.response.ServiceReportDraftDetailResponse;
import com.tech.hvac_backend.dto.response.ServiceReportFromDailyReportsResponse;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServiceReportFromDailyReportsService {

    private static final int MAX_SELECTED_PHOTOS = 4;

    private final DailyDraftRepository dailyDraftRepository;
    private final PhotoRecordRepository photoRecordRepository;
    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final DailyReportsServiceReportPromptBuilderService promptBuilderService;
    private final ServiceReportFromDailyReportsAiGenerator aiGenerator;

    public ServiceReportFromDailyReportsService(
            DailyDraftRepository dailyDraftRepository,
            PhotoRecordRepository photoRecordRepository,
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            DailyReportsServiceReportPromptBuilderService promptBuilderService,
            ServiceReportFromDailyReportsAiGenerator aiGenerator
    ) {
        this.dailyDraftRepository = dailyDraftRepository;
        this.photoRecordRepository = photoRecordRepository;
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.promptBuilderService = promptBuilderService;
        this.aiGenerator = aiGenerator;
    }

    public ServiceReportFromDailyReportsResponse generate(
            ServiceReportFromDailyReportsRequest request
    ) {
        List<String> reportIds = validateAndNormalizeIds(request);
        List<DailyDraftEntity> reports = loadReports(reportIds);
        validateSameMachineAndVessel(reports);

        reports.sort(Comparator.comparing(
                DailyDraftEntity::getCreatedAt,
                Comparator.nullsFirst(String::compareTo)
        ));
        DailyDraftEntity latestReport = reports.getLast();

        MachineEntity machine = machineRepository.findById(latestReport.getMachineId()).orElse(null);
        VesselEntity vessel = vesselRepository.findById(latestReport.getVesselId()).orElse(null);
        List<PhotoRecordEntity> photos = loadPhotos(reportIds);

        String prompt = promptBuilderService.buildPrompt(reports, machine, vessel, photos);
        AiServiceReportFromDailyReportsResult generated =
                aiGenerator.generateServiceReportFromDailyReports(prompt);

        if (generated == null || generated.getServiceReport() == null) {
            throw new IllegalStateException("OpenAI returned no Service Report.");
        }

        List<PhotoRecordEntity> selectedPhotos =
                selectValidPhotos(photos, generated.getSelectedPhotoIds());
        ServiceReportDraftDetailResponse sourceReport =
                buildSourceReport(reports, latestReport, machine, vessel, selectedPhotos);

        return new ServiceReportFromDailyReportsResponse(
                sourceReport,
                generated.getServiceReport()
        );
    }

    private List<String> validateAndNormalizeIds(ServiceReportFromDailyReportsRequest request) {
        if (request == null || request.getDailyReportIds() == null) {
            throw new IllegalArgumentException("dailyReportIds is required.");
        }

        List<String> ids = request.getDailyReportIds().stream()
                .map(id -> id == null ? "" : id.trim())
                .filter(id -> !id.isEmpty())
                .toList();
        Set<String> uniqueIds = new LinkedHashSet<>(ids);

        if (uniqueIds.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two distinct Daily Report ids are required."
            );
        }
        if (uniqueIds.size() != request.getDailyReportIds().size()) {
            throw new IllegalArgumentException(
                    "dailyReportIds must not contain blank or duplicate ids."
            );
        }

        return List.copyOf(uniqueIds);
    }

    private List<DailyDraftEntity> loadReports(List<String> reportIds) {
        List<DailyDraftEntity> reports = dailyDraftRepository.findAllById(reportIds);
        Set<String> foundIds = reports.stream()
                .map(DailyDraftEntity::getId)
                .collect(Collectors.toSet());
        List<String> missingIds = reportIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Daily report drafts not found: " + String.join(", ", missingIds)
            );
        }
        return new ArrayList<>(reports);
    }

    private void validateSameMachineAndVessel(List<DailyDraftEntity> reports) {
        DailyDraftEntity first = reports.getFirst();
        boolean mixedMachine = reports.stream()
                .anyMatch(report -> !first.getMachineId().equals(report.getMachineId()));
        boolean mixedVessel = reports.stream()
                .anyMatch(report -> !first.getVesselId().equals(report.getVesselId()));

        if (mixedMachine || mixedVessel) {
            throw new IllegalArgumentException(
                    "All Daily Reports must belong to the same machine and vessel."
            );
        }
    }

    private List<PhotoRecordEntity> loadPhotos(List<String> reportIds) {
        return reportIds.stream()
                .flatMap(reportId -> photoRecordRepository
                        .findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc(
                                PhotoOwnerType.DAILY_DRAFT,
                                reportId
                        )
                        .stream())
                .sorted(Comparator.comparing(
                        PhotoRecordEntity::getCreatedAt,
                        Comparator.nullsFirst(String::compareTo)
                ))
                .toList();
    }

    private List<PhotoRecordEntity> selectValidPhotos(
            List<PhotoRecordEntity> availablePhotos,
            List<String> selectedPhotoIds
    ) {
        var photosById = availablePhotos.stream()
                .collect(Collectors.toMap(
                        PhotoRecordEntity::getId,
                        Function.identity(),
                        (first, ignored) -> first
                ));

        LinkedHashSet<String> validIds = new LinkedHashSet<>();
        if (selectedPhotoIds != null) {
            selectedPhotoIds.stream()
                    .filter(photosById::containsKey)
                    .limit(MAX_SELECTED_PHOTOS)
                    .forEach(validIds::add);
        }

        for (PhotoRecordEntity photo : availablePhotos) {
            if (validIds.size() >= Math.min(MAX_SELECTED_PHOTOS, availablePhotos.size())) {
                break;
            }
            validIds.add(photo.getId());
        }

        return validIds.stream()
                .map(photosById::get)
                .toList();
    }

    private ServiceReportDraftDetailResponse buildSourceReport(
            List<DailyDraftEntity> reports,
            DailyDraftEntity latestReport,
            MachineEntity machine,
            VesselEntity vessel,
            List<PhotoRecordEntity> selectedPhotos
    ) {
        String sourceId = UUID.nameUUIDFromBytes(
                reports.stream()
                        .map(DailyDraftEntity::getId)
                        .sorted()
                        .collect(Collectors.joining("|"))
                        .getBytes(StandardCharsets.UTF_8)
        ).toString();

        return new ServiceReportDraftDetailResponse(
                sourceId,
                latestReport.getVesselId(),
                latestReport.getVesselName(),
                prefer(latestReport.getVesselImo(), vessel != null ? vessel.getImoNumber() : null),
                prefer(latestReport.getVesselType(), vessel != null ? vessel.getVesselType() : null),
                prefer(latestReport.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null),
                prefer(latestReport.getVesselContact(), vessel != null ? vessel.getVesselContact() : null),
                latestReport.getMachineId(),
                latestReport.getMachineTag(),
                latestReport.getMachineModel(),
                prefer(latestReport.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null),
                latestReport.getMachineType(),
                latestReport.getMachineStarterType(),
                latestReport.getMachineLocation(),
                prefer(latestReport.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null),
                prefer(latestReport.getMachineOilType(), machine != null ? machine.getOilType() : null),
                prefer(latestReport.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null),
                prefer(latestReport.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null),
                prefer(latestReport.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null),
                prefer(latestReport.getMachineMfg(), machine != null ? machine.getMfg() : null),
                machine != null ? machine.getMachinePhotoId() : null,
                machine != null ? machine.getMachinePhotoPreviewUrl() : null,
                latestReport.getCreatedAt(),
                joinNonBlank(reports, DailyDraftEntity::getWorkConductedToday),
                null,
                joinNonBlank(reports, DailyDraftEntity::getFurtherActions),
                null,
                "unknown",
                false,
                selectedPhotos.stream().map(this::mapPhoto).toList(),
                "service_report"
        );
    }

    private String joinNonBlank(
            List<DailyDraftEntity> reports,
            Function<DailyDraftEntity, String> getter
    ) {
        String joined = reports.stream()
                .map(getter)
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining("\n"));
        return joined.isBlank() ? null : joined;
    }

    private PhotoDetailResponse mapPhoto(PhotoRecordEntity photo) {
        return new PhotoDetailResponse(
                photo.getId(),
                photo.getFilename(),
                photo.getCaption(),
                photo.getCreatedAt(),
                photo.getPreviewUrl()
        );
    }

    private String prefer(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }
}
