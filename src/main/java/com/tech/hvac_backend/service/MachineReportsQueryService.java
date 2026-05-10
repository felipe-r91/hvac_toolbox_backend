package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineTimelineItemResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MachineReportsQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final CfrDraftRepository cfrDraftRepository;
    private final DailyDraftRepository dailyDraftRepository;

    public MachineReportsQueryService(
            PreventiveReportRepository preventiveReportRepository,
            CorrectiveDraftRepository correctiveDraftRepository,
            CfrDraftRepository cfrDraftRepository,
            DailyDraftRepository dailyDraftRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.cfrDraftRepository = cfrDraftRepository;
        this.dailyDraftRepository = dailyDraftRepository;
    }

    public List<MachineTimelineItemResponse> getPreventiveReportsByMachineId(String machineId) {
        return preventiveReportRepository.findByMachineIdOrderByCompletedAtDesc(machineId)
                .stream()
                .map(this::mapPreventive)
                .toList();
    }

    public List<MachineTimelineItemResponse> getCorrectiveReportsByMachineId(String machineId) {
        return correctiveDraftRepository.findByMachineIdOrderByCreatedAtDesc(machineId)
                .stream()
                .map(this::mapCorrective)
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
        List<MachineTimelineItemResponse> corrective = getCorrectiveReportsByMachineId(machineId);
        List<MachineTimelineItemResponse> cfr = getCfrReportsByMachineId(machineId);
        List<MachineTimelineItemResponse> daily = getDailyReportsByMachineId(machineId);

        return Stream.of(preventive, corrective, cfr, daily)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(MachineTimelineItemResponse::getDate).reversed())
                .toList();
    }

    private MachineTimelineItemResponse mapPreventive(PreventiveReportEntity report) {
        String summary = report.getFailureNotes();
        if (summary == null || summary.isBlank()) {
            summary = "Health check completed.";
        }

        return new MachineTimelineItemResponse(
                report.getId(),
                "preventive",
                "health_check",
                report.getCompletedAt(),
                normalizeMachineStatus(report.getOverallStatus()),
                "Health Check",
                summary,
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getLinkedCorrectiveDraftId(),
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

    private MachineTimelineItemResponse mapCorrective(CorrectiveDraftEntity draft) {
        String summary = draft.getProblemSummary();
        if (summary == null || summary.isBlank()) {
            summary = "Corrective maintenance record.";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "corrective",
                "corrective",
                draft.getCreatedAt(),
                mapCorrectiveStatus(draft.getMachineReturnedToService()),
                "Corrective Maintenance",
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
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

    private String mapCorrectiveStatus(String machineReturnedToService) {
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
