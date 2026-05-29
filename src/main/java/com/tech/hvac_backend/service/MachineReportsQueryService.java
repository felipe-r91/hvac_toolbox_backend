package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineTimelineItemResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.ServiceReportDraftRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MachineReportsQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final ServiceReportDraftRepository serviceReportDraftRepository;
    private final CfrDraftRepository cfrDraftRepository;
    private final DailyDraftRepository dailyDraftRepository;

    public MachineReportsQueryService(
            PreventiveReportRepository preventiveReportRepository,
            ServiceReportDraftRepository serviceReportDraftRepository,
            CfrDraftRepository cfrDraftRepository,
            DailyDraftRepository dailyDraftRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.serviceReportDraftRepository = serviceReportDraftRepository;
        this.cfrDraftRepository = cfrDraftRepository;
        this.dailyDraftRepository = dailyDraftRepository;
    }

    public List<MachineTimelineItemResponse> getPreventiveReportsByMachineId(String machineId) {
        return preventiveReportRepository.findByMachineIdOrderByCompletedAtDesc(machineId)
                .stream()
                .map(this::mapPreventive)
                .toList();
    }

    public List<MachineTimelineItemResponse> getServiceReportsByMachineId(String machineId) {
        return serviceReportDraftRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::mapServiceReport)
                .toList();
    }

    public List<MachineTimelineItemResponse> getCfrReportsByMachineId(String machineId) {
        return cfrDraftRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::mapCfr)
                .toList();
    }

    public List<MachineTimelineItemResponse> getDailyReportsByMachineId(String machineId) {
        return dailyDraftRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::mapDaily)
                .toList();
    }

    public List<MachineTimelineItemResponse> getTimelineByMachineId(String machineId) {
        List<MachineTimelineItemResponse> preventive = getPreventiveReportsByMachineId(machineId);
        List<MachineTimelineItemResponse> serviceReports = getServiceReportsByMachineId(machineId);
        List<MachineTimelineItemResponse> cfr = getCfrReportsByMachineId(machineId);
        List<MachineTimelineItemResponse> daily = getDailyReportsByMachineId(machineId);

        return Stream.of(preventive, serviceReports, cfr, daily)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(MachineTimelineItemResponse::getDate).reversed())
                .toList();
    }

    private MachineTimelineItemResponse mapPreventive(PreventiveReportEntity report) {
        String summary = report.getFailureNotes();
        if (summary == null || summary.isBlank()) {
            summary = "Machine maintenance completed.";
        }

        String reportCategory = resolvePreventiveCategory(report);

        return new MachineTimelineItemResponse(
                report.getId(),
                "preventive",
                reportCategory,
                report.getCompletedAt(),
                normalizeMachineStatus(report.getOverallStatus()),
                resolvePreventiveTitle(reportCategory),
                summary,
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getLinkedServiceReportDraftId(),
                null
        );
    }

    private MachineTimelineItemResponse mapDaily(DailyDraftEntity draft) {
        String summary = draft.getWorkConductedToday();
        if (summary == null || summary.isBlank()) {
            summary = draft.getFailureNotes();
        }
        if (summary == null || summary.isBlank()) {
            summary = "Daily report completed.";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "daily",
                "daily",
                draft.getCreatedAt(),
                mapDailyStatus(draft.isAlarmPresent()),
                "Daily Report",
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                null,
                null
        );
    }

    private MachineTimelineItemResponse mapServiceReport(ServiceReportDraftEntity draft) {
        String summary = draft.getWorkPerformed();
        if (summary == null || summary.isBlank()) {
            summary = "Service report completed.";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "service_report",
                "service_report",
                draft.getCreatedAt(),
                mapServiceReportStatus(draft.getMachineReturnedToService()),
                "Service Report",
                summary,
                null,
                null,
                null,
                null,
                draft.getSourcePreventiveReportId()
        );
    }

    private MachineTimelineItemResponse mapCfr(CfrDraftEntity draft) {
        String summary = draft.getConditionFound();
        if (summary == null || summary.isBlank()) {
            summary = "Conditions found report.";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "cfr",
                "cfr",
                draft.getCreatedAt(),
                normalizeMachineStatus(draft.getMachineStatus()),
                "Conditions Found Report",
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                null,
                null
        );
    }

    private String normalizeMachineStatus(String status) {
        if (status == null || status.isBlank()) {
            return "unknown";
        }

        return switch (status.toLowerCase()) {
            case "online", "down", "unknown" -> status.toLowerCase();
            default -> "unknown";
        };
    }

    private String resolvePreventiveCategory(PreventiveReportEntity report) {
        return report.getReportCategory() == null || report.getReportCategory().isBlank()
                ? "machine_maintenance"
                : report.getReportCategory();
    }

    private String resolvePreventiveTitle(String reportCategory) {
        return "health_check".equalsIgnoreCase(reportCategory) ? "Health Check" : "Machine Maintenance";
    }

    private String mapServiceReportStatus(String machineReturnedToService) {
        if (machineReturnedToService == null || machineReturnedToService.isBlank()) {
            return "unknown";
        }

        return switch (machineReturnedToService.toLowerCase()) {
            case "yes" -> "online";
            case "no" -> "down";
            default -> "unknown";
        };
    }

    private String mapDailyStatus(boolean alarmPresent) {
        return alarmPresent ? "unknown" : "online";
    }
}
