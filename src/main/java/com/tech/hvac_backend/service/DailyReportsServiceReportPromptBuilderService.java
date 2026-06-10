package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DailyReportsServiceReportPromptBuilderService {

    private static final int MAX_SELECTED_PHOTOS = 4;

    private final ManualKnowledgeService manualKnowledgeService;

    public DailyReportsServiceReportPromptBuilderService(
            ManualKnowledgeService manualKnowledgeService
    ) {
        this.manualKnowledgeService = manualKnowledgeService;
    }

    public String buildPrompt(
            List<DailyDraftEntity> dailyReports,
            MachineEntity machine,
            VesselEntity vessel,
            List<PhotoRecordEntity> photos
    ) {
        DailyDraftEntity latestReport = dailyReports.getLast();
        String manualContext = manualKnowledgeService.buildManualContext(
                findRelevantManualChunks(dailyReports)
        );

        String reportData = dailyReports.stream()
                .map(this::formatDailyReport)
                .collect(Collectors.joining("\n\n--- NEXT DAILY REPORT ---\n\n"));

        String photoList = photos.isEmpty()
                ? "No attached photos."
                : photos.stream()
                .map(this::formatPhoto)
                .collect(Collectors.joining("\n"));

        int requiredPhotoCount = Math.min(MAX_SELECTED_PHOTOS, photos.size());

        return """
                You are generating one customer-ready Johnson Controls Marine & Navy Service Report
                by consolidating multiple Daily Reports for the same machine.

                Act as an experienced HVAC/refrigeration field service engineer. Reconstruct the
                overall service story across the reporting period, prioritizing the most important
                findings, work, alarms, outcomes, unresolved risks, and follow-up.

                This is a synthesis task, not a transcription task:
                - Merge repeated or overlapping work into one concise item.
                - Do not create a day-by-day diary or an exhaustive list.
                - Prefer information that affected safety, machine availability, diagnosis,
                  corrective work, verification, final condition, or required follow-up.
                - Resolve progression over time: later updates supersede earlier status when the
                  reports clearly describe the same issue.
                - Preserve distinct significant events, but omit routine repetition and low-value detail.
                - Use Daily Report notes as the primary evidence.
                - Use manual context only for relevant technical clarity and recommendations.
                - Do not fabricate measurements, parts, inspections, alarm codes, test results,
                  completed work, or a return-to-service decision.
                - When the final machine status is not explicit, state that it is unknown or requires confirmation.

                Keep the result compact:
                - executiveSummary: one short paragraph.
                - conditionFound: one concise paragraph, or empty when unsupported.
                - alarms: at most 5 consolidated significant alarms.
                - workConducted: 4 to 8 consolidated action items when enough evidence exists.
                - recommendations: at most 5 prioritized actionable items.
                - furtherActionRequired: one concise paragraph.

                Select the most relevant report photos using their ids, captions, filenames, report
                context, and dates. Prioritize photos that best evidence a significant condition,
                alarm, corrective action, component state, or final result. Avoid duplicate views
                and low-information photos.

                You must return ONLY valid JSON, without markdown or explanations.
                The JSON must match EXACTLY this internal structure:

                {
                  "serviceReport": {
                    "reportNo": "",
                    "title": "Service Report",
                    "subtitle": "",
                    "company": "Johnson Controls",
                    "branch": "",
                    "date": "",
                    "serviceOrder": "",
                    "engineer": "",
                    "projectManager": "",
                    "location": "",
                    "machineStatus": "",
                    "serviceResult": "",
                    "machineReturnedToService": "",
                    "executiveSummary": "",
                    "conditionFound": "",
                    "alarms": [
                      {
                        "description": "",
                        "status": ""
                      }
                    ],
                    "workConducted": [],
                    "recommendations": [],
                    "furtherActionRequired": "",
                    "ehsStatement": ""
                  },
                  "selectedPhotoIds": []
                }

                Service Report field rules:
                - Keep exactly the Service Report fields shown above.
                - title must be "Service Report" and company must be "Johnson Controls".
                - Layout fields must be empty unless explicitly provided.
                - date should represent the service period from the earliest to latest Daily Report.
                - subtitle should identify the machine.
                - machineStatus, serviceResult, and machineReturnedToService must reflect the latest
                  supported final state. Do not infer return to service merely because work occurred.
                - Use "Unknown" for machineReturnedToService when the Daily Reports do not explicitly
                  establish whether the machine returned to service.
                - Return empty arrays instead of placeholder alarm, work, or recommendation items.

                Photo selection rules:
                - selectedPhotoIds must contain exactly %d unique ids when at least that many photos exist.
                - If fewer than %d photos exist, include every available photo id.
                - Never invent an id and never return more than %d ids.

                Manual reference context:
                %s

                Vessel:
                Name: %s
                IMO Number: %s
                Type: %s
                Owner / Customer: %s
                Contact: %s

                Machine:
                Tag: %s
                Model: %s
                Manufacturer: %s
                Type: %s
                Compressor Type: %s
                Starter Type: %s
                Serial Number: %s
                Location: %s
                Refrigerant: %s
                Oil Type: %s
                Control System: %s
                Software Version: %s

                Daily Reports, chronological:
                %s

                Available Photos:
                %s
                """.formatted(
                requiredPhotoCount,
                MAX_SELECTED_PHOTOS,
                MAX_SELECTED_PHOTOS,
                manualContext,
                nullSafe(latestReport.getVesselName()),
                nullSafe(prefer(latestReport.getVesselImo(), vessel != null ? vessel.getImoNumber() : null)),
                nullSafe(prefer(latestReport.getVesselType(), vessel != null ? vessel.getVesselType() : null)),
                nullSafe(prefer(latestReport.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null)),
                nullSafe(prefer(latestReport.getVesselContact(), vessel != null ? vessel.getVesselContact() : null)),
                nullSafe(latestReport.getMachineTag()),
                nullSafe(latestReport.getMachineModel()),
                nullSafe(prefer(latestReport.getMachineMfg(), machine != null ? machine.getMfg() : null)),
                nullSafe(latestReport.getMachineType()),
                nullSafe(prefer(latestReport.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null)),
                nullSafe(latestReport.getMachineStarterType()),
                nullSafe(prefer(latestReport.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null)),
                nullSafe(latestReport.getMachineLocation()),
                nullSafe(prefer(latestReport.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null)),
                nullSafe(prefer(latestReport.getMachineOilType(), machine != null ? machine.getOilType() : null)),
                nullSafe(prefer(latestReport.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null)),
                nullSafe(prefer(latestReport.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null)),
                reportData,
                photoList
        );
    }

    private List<ManualKnowledgeChunkEntity> findRelevantManualChunks(
            List<DailyDraftEntity> dailyReports
    ) {
        Map<UUID, ManualKnowledgeChunkEntity> uniqueChunks = new LinkedHashMap<>();
        for (DailyDraftEntity dailyReport : dailyReports) {
            for (ManualKnowledgeChunkEntity chunk : manualKnowledgeService.findRelevantChunks(dailyReport)) {
                uniqueChunks.putIfAbsent(chunk.getId(), chunk);
            }
        }
        return List.copyOf(uniqueChunks.values());
    }

    private String formatDailyReport(DailyDraftEntity report) {
        return """
                Daily Report Id: %s
                Created At: %s
                Alarm Present: %s
                Failure Component: %s
                Failure Mode: %s
                Failure Code: %s
                Failure Notes: %s
                Work Conducted Today: %s
                Further Actions: %s
                """.formatted(
                report.getId(),
                nullSafe(report.getCreatedAt()),
                report.isAlarmPresent() ? "Yes" : "No",
                nullSafe(report.getFailureComponent()),
                nullSafe(report.getFailureMode()),
                nullSafe(report.getFailureCode()),
                nullSafe(report.getFailureNotes()),
                nullSafe(report.getWorkConductedToday()),
                nullSafe(report.getFurtherActions())
        );
    }

    private String formatPhoto(PhotoRecordEntity photo) {
        return "- Id: " + photo.getId()
                + " | Daily Report Id: " + photo.getOwnerId()
                + " | Created At: " + nullSafe(photo.getCreatedAt())
                + " | Caption: " + nullSafe(photo.getCaption())
                + " | Filename: " + nullSafe(photo.getFilename());
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    private String prefer(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }
}
