package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineTimelineItemResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MachineReportsQueryService {

    private final PreventiveReportRepository preventiveReportRepository;
    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final CfrDraftRepository cfrDraftRepository;

    public MachineReportsQueryService(
            PreventiveReportRepository preventiveReportRepository,
            CorrectiveDraftRepository correctiveDraftRepository,
            CfrDraftRepository cfrDraftRepository
    ) {
        this.preventiveReportRepository = preventiveReportRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.cfrDraftRepository = cfrDraftRepository;
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
                report.getOverallStatus(),
                "Health Check",
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
            summary = "Corrective maintenance record.";
        }

        return new MachineTimelineItemResponse(
                draft.getId(),
                "corrective",
                "corrective",
                draft.getCreatedAt(),
                status,
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
                draft.getMachineStatus(),
                "Conditions Found Report",
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                null,
                null
        );
    }
}