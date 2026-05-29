package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineSummaryResponse;
import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.ServiceReportDraftEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.CfrDraftRepository;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.ServiceReportDraftRepository;
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
    private final ServiceReportDraftRepository serviceReportDraftRepository;
    private final CfrDraftRepository cfrDraftRepository;
    private final DailyDraftRepository dailyDraftRepository;

    public MachineSummaryService(
            MachineRepository machineRepository,
            VesselRepository vesselRepository,
            PreventiveReportRepository preventiveReportRepository,
            ServiceReportDraftRepository serviceReportDraftRepository,
            CfrDraftRepository cfrDraftRepository,
            DailyDraftRepository dailyDraftRepository
    ) {
        this.machineRepository = machineRepository;
        this.vesselRepository = vesselRepository;
        this.preventiveReportRepository = preventiveReportRepository;
        this.serviceReportDraftRepository = serviceReportDraftRepository;
        this.cfrDraftRepository = cfrDraftRepository;
        this.dailyDraftRepository = dailyDraftRepository;
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

        List<ServiceReportDraftEntity> serviceReportDrafts =
                serviceReportDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        List<CfrDraftEntity> cfrDrafts =
                cfrDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        List<DailyDraftEntity> dailyDrafts =
                dailyDraftRepository.findByMachineIdOrderByCreatedAtDesc(machine.getId());

        String latestReportDate = null;
        String latestReportType = null;
        String latestKnownStatus = "unknown";

        Optional<LatestRecord> latestPreventive = preventiveReports.stream()
                .findFirst()
                .map(report -> new LatestRecord(
                        report.getCompletedAt(),
                        resolvePreventiveCategory(report),
                        normalizePreventiveStatus(report.getOverallStatus())
                ));

        Optional<LatestRecord> latestServiceReport = serviceReportDrafts.stream()
                .findFirst()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "service_report",
                        mapServiceReportStatus(draft.getMachineReturnedToService())
                ));

        Optional<LatestRecord> latestCfr = cfrDrafts.stream()
                .findFirst()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "cfr",
                        normalizeMachineStatus(draft.getMachineStatus())
                ));

        Optional<LatestRecord> latestDaily = dailyDrafts.stream()
                .findFirst()
                .map(draft -> new LatestRecord(
                        draft.getCreatedAt(),
                        "daily",
                        mapDailyStatus(draft.isAlarmPresent())
                ));

        Optional<LatestRecord> latest = Stream.of(
                        latestPreventive.orElse(null),
                        latestServiceReport.orElse(null),
                        latestCfr.orElse(null),
                        latestDaily.orElse(null)
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
                vessel != null ? vessel.getImoNumber() : null,
                machine.getTag(),
                machine.getModel(),
                machine.getSerialNumber(),
                machine.getType(),
                machine.getStarterType(),
                machine.getRefrigerant(),
                machine.getOilType(),
                machine.getControlSystem(),
                machine.getSoftwareVersion(),
                machine.getCompressorType(),
                machine.getMfg(),
                machine.getLocation(),
                machine.getMachinePhotoPreviewUrl(),
                latestReportDate,
                latestReportType,
                latestKnownStatus,
                preventiveReportRepository.countByMachineId(machine.getId()),
                serviceReportDraftRepository.countByMachineId(machine.getId()),
                cfrDraftRepository.countByMachineId(machine.getId()),
                dailyDraftRepository.countByMachineId(machine.getId())
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

    private String resolvePreventiveCategory(PreventiveReportEntity report) {
        return report.getReportCategory() == null || report.getReportCategory().isBlank()
                ? "machine_maintenance"
                : report.getReportCategory();
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

    private String normalizeMachineStatus(String status) {
        if (status == null || status.isBlank()) {
            return "unknown";
        }

        return switch (status.toLowerCase()) {
            case "online", "down", "unknown" -> status.toLowerCase();
            default -> "unknown";
        };
    }

    private String mapDailyStatus(boolean alarmPresent) {
        return alarmPresent ? "unknown" : "online";
    }

    private record LatestRecord(String date, String type, String status) {
    }
}
