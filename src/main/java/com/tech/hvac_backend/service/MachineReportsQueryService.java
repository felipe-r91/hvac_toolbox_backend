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
            summary = "Preventive maintenance completed.";
        }

        return new MachineTimelineItemResponse(
                report.getId(),
                "preventive",
                report.getCompletedAt(),
                report.getOverallStatus(),
                "Preventive maintenance completed",
                summary,
                report.getFailureComponent(),
                report.getFailureMode(),
                report.getFailureCode()
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
                draft.getCreatedAt(),
                status,
                "Corrective maintenance",
                summary,
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode()
        );
    }
}