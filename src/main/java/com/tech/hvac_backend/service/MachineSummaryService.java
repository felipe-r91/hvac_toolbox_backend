package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineSummaryResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.CorrectiveDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MachineSummaryService {

    private final MachineRepository machineRepository;
    private final VesselRepository vesselRepository;
    private final PreventiveReportRepository preventiveReportRepository;
    private final CorrectiveDraftRepository correctiveDraftRepository;
    private final CfrDraftRepository cfrDraftRepository;

    public MachineSummaryService(
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            PreventiveReportRepository preventiveReportRepository,
            CorrectiveDraftRepository correctiveDraftRepository,
            CfrDraftRepository cfrDraftRepository
    ) {
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.preventiveReportRepository = preventiveReportRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
        this.cfrDraftRepository = cfrDraftRepository;
    }

    public List<MachineSummaryResponse> getAllMachineSummaries() {
        return machineRepository.findAllByOrderByTagAsc()
                .stream()
                .map(this::buildSummary)
                .toList();
    }

    public MachineSummaryResponse getMachineSummaryById(String machineId) {
        MachineEntity machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

        return buildSummary(machine);
    }

    private MachineSummaryResponse buildSummary(MachineEntity machine) {
        VesselEntity vessel = vesselRepository.findById(machine.getVesselId()).orElse(null);

        List<PreventiveReportEntity> preventiveReports =
                preventiveReportRepository.findByMachineIdOrderByCompletedAtDesc(machine.getId());

        List<CorrectiveDraftEntity> correctiveDrafts =
                correctiveDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        List<CfrDraftEntity> cfrDrafts =
                cfrDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        String latestReportDate = null;
        String latestReportType = null;
        String latestKnownStatus = "unknown";

        Optional<LatestRecord> latestPreventive = preventiveReports.stream()
                .findFirst()
                .map(report -> new LatestRecord(
                        report.getCompletedAt(),
                        "health_check",
                        normalizePreventiveStatus(report.getOverallStatus())
                ));

        Optional<LatestRecord> latestCorrective = correctiveDrafts.stream()
                .findFirst()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "corrective",
                        mapCorrectiveStatus(draft.getMachineReturnedToService())
                ));

        Optional<LatestRecord> latestCfr = cfrDrafts.stream()
                .findFirst()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "cfr",
                        normalizeMachineStatus(draft.getMachineStatus())
                ));

        Optional<LatestRecord> latest = Stream.of(
                        latestPreventive.orElse(null),
                        latestCorrective.orElse(null),
                        latestCfr.orElse(null)
                )
                .filter(item -> item != null && item.date() != null && !item.date().isBlank())
                .max(Comparator.comparing(LatestRecord::date));

        if (latest.isPresent()) {
            latestReportDate = latest.get().date();
            latestReportType = latest.get().type();
            latestKnownStatus = latest.get().status();
        }

        return new MachineSummaryResponse(
                machine.getId(),
                machine.getVesselId(),
                vessel != null ? vessel.getName() : "Unknown Vessel",
                machine.getTag(),
                machine.getModel(),
                machine.getSerialNumber(),
                machine.getType(),
                machine.getStarterType(),
                machine.getLocation(),
                latestReportDate,
                latestReportType,
                latestKnownStatus,
                preventiveReportRepository.countByMachineId(machine.getId()),
                correctiveDraftRepository.countByMachineId(machine.getId()),
                cfrDraftRepository.countByMachineId(machine.getId())
        );
    }

    private String normalizePreventiveStatus(String status) {
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

    private String normalizeMachineStatus(String status) {
        if (status == null || status.isBlank()) {
            return "unknown";
        }

        return switch (status.toLowerCase()) {
            case "online", "down", "unknown" -> status.toLowerCase();
            default -> "unknown";
        };
    }

    private record LatestRecord(String date, String type, String status) {
    }
}