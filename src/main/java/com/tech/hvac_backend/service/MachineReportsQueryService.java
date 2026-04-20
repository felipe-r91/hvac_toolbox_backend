package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineTimelineItemResponse;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineReportsQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final CorrectiveDraftRepository correctiveDraftRepository;

    public MachineReportsQueryService(
            PreventiveReportRepository preventiveReportRepository,
            CorrectiveDraftRepository correctiveDraftRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
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

    private MachineTimelineItemResponse mapPreventive(PreventiveReportEntity report) {
        String summary = report.getFailureNotes();
        if (summary == null || summary.isBlank()) {
            summary = "Health check completed.";
        }

        return new MachineTimelineItemResponse(
                report.getId(),
                "health_check",
                report.getReportCategory() != null ? report.getReportCategory() : "health_check",
                report.getCompletedAt(),
                report.getOverallStatus(),
                "Health check completed",
                summary,
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode(),
                report.getLinkedCorrectiveDraftId(),
                null
        );
    }

    private MachineTimelineItemResponse mapCorrective(CorrectiveDraftEntity draft) {
        String status = "unknown";
        if ("no".equalsIgnoreCase(draft.getMachineReturnedToService())) {
            status = "down";
        } else if ("yes".equalsIgnoreCase(draft.getMachineReturnedToService())) {
            status = "online";
        }

        String summary = draft.getProblemSummary();
        if (summary == null || summary.isBlank()) {
            summary = "Corrective report.";
        }

        String reportCategory =
                draft.getReportCategory() != null ? draft.getReportCategory() : "corrective";

        String title = "Corrective report";
        if ("cfr".equalsIgnoreCase(reportCategory)) {
            title = "Conditions found report";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "corrective",
                reportCategory,
                draft.getCreatedAt(),
                status,
                title,
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                null,
                draft.getSourcePreventiveReportId()
        );
    }
}