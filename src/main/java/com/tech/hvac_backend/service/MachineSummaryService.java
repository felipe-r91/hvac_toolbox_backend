package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineSummaryResponse;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
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

    public MachineSummaryService(
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            PreventiveReportRepository preventiveReportRepository,
            CorrectiveDraftRepository correctiveDraftRepository
    ) {
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.preventiveReportRepository = preventiveReportRepository;
        this.correctiveDraftRepository = correctiveDraftRepository;
    }

    public List<MachineSummaryResponse> getAllMachineSummaries() {
        return machineRepository.findAllByOrderByTagAsc()
                .stream()
                .map(this::buildSummary)
                .toList();
    }

    private MachineSummaryResponse buildSummary(MachineEntity machine) {
        VesselEntity vessel = vesselRepository.findById(machine.getVesselId()).orElse(null);

        List<PreventiveReportEntity> preventiveReports =
                preventiveReportRepository.findByMachineIdOrderByCompletedAtDesc(machine.getId());

        List<CorrectiveDraftEntity> correctiveDrafts =
                correctiveDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        String latestReportDate = null;
        String latestReportType = null;
        String latestKnownStatus = "unknown";

        Optional<LatestRecord> latestPreventive = preventiveReports.stream()
                .map(report -> new LatestRecord(
                        report.getCompletedAt(),
                        "preventive",
                        report.getOverallStatus()
                ))
                .findFirst();

        Optional<LatestRecord> latestCorrective = correctiveDrafts.stream()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "corrective",
                        draft.getMachineReturnedToService() != null &&
                                draft.getMachineReturnedToService().equalsIgnoreCase("no")
                                ? "down"
                                : "online"
                ))
                .findFirst();

        assert latestPreventive.orElse(null) != null;
        assert latestCorrective.orElse(null) != null;
        Optional<LatestRecord> latest = Stream.of(
                        latestPreventive.orElse(null),
                        latestCorrective.orElse(null)
                )
                .filter(item -> item.date() != null)
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
                correctiveDraftRepository.countByMachineId(machine.getId())
        );
    }

    private record LatestRecord(String date, String type, String status) {
    }

    public MachineSummaryResponse getMachineSummaryById(String machineId) {
        MachineEntity machine = machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + machineId));

        return buildSummary(machine);
    }
}