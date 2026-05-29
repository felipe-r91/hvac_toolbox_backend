package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import com.tech.hvac_backend.entity.VesselEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MachineMaintenancePromptBuilderService {

    private final ManualKnowledgeService manualKnowledgeService;

    public MachineMaintenancePromptBuilderService(ManualKnowledgeService manualKnowledgeService) {
        this.manualKnowledgeService = manualKnowledgeService;
    }

    public String buildPrompt(
            PreventiveReportEntity report,
            MachineEntity machine,
            VesselEntity vessel,
            List<PreventiveReportTaskEntity> tasks,
            List<PhotoRecordEntity> photos
    ) {
        String taskList = tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.joining("\n"));

        String alarmTaskList = tasks.stream()
                .filter(this::isAlarmTask)
                .map(this::formatTask)
                .collect(Collectors.joining("\n"));

        String photoList = photos == null || photos.isEmpty()
                ? "No attached photos."
                : photos.stream()
                .map(photo -> "- Task Id: " + nullSafe(photo.getTaskId())
                        + " | Caption: " + nullSafe(photo.getCaption())
                        + " | Filename: " + nullSafe(photo.getFilename()))
                .collect(Collectors.joining("\n"));

        List<ManualKnowledgeChunkEntity> relevantChunks =
                manualKnowledgeService.findRelevantChunks(report, machine, tasks);

        String manualContext =
                manualKnowledgeService.buildManualContext(relevantChunks);

        return """
                You are generating a customer-ready Johnson Controls Marine & Navy Machine Maintenance Report.

                You are acting as an experienced HVAC/refrigeration field service engineer reviewing a completed machine maintenance checklist, technician notes, measured values, machine information, photos, and OEM manual references.

                Use the technician maintenance tasks as the primary field evidence.
                Use the manual reference context only to support technical clarity, risk explanation, and relevant follow-up recommendations.

                Write in a professional technical field-service tone suitable for a vessel customer.

                Do not fabricate measurements, inspections, parts, alarms, test results, completed work, or events that are not supported by the provided source data.
                Do not invent fault causes or diagnostic conclusions unless supported by task notes, failure classification, or abnormal task status.
                Do not copy manual text directly; synthesize it into clear maintenance explanations.

                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not include explanations.

                The JSON must match EXACTLY this structure:

                {
                  "reportNo": "",
                  "title": "Machine Maintenance Report",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "maintenanceResult": "",
                  "alarmStatus": "",
                  "executiveSummary": "",
                  "maintenanceSummary": "",
                  "alarms": [
                    {
                      "description": "",
                      "status": ""
                    }
                  ],
                  "activities": [
                    {
                      "category": "",
                      "task": "",
                      "tool": "",
                      "status": "",
                      "notes": "",
                      "measuredValue": "",
                      "unit": "",
                      "completedAt": "",
                      "photos": []
                    }
                  ],
                  "recommendations": [],
                  "furtherActionRequired": "",
                  "ehsStatement": ""
                }

                Field rules:
                - title must be "Machine Maintenance Report".
                - company must be "Johnson Controls".
                - reportNo, branch, serviceOrder, engineer, projectManager, and ehsStatement are layout fields. Return empty string unless explicitly provided.
                - subtitle should summarize the machine, for example: "AC#1 - Liquid Chiller".
                - date should be based on completedAt.
                - location should use the machine location or vessel/location information available.

                - machineStatus must be exactly one of these two strings:
                  "Work Finished" or "Work In Progress".
                - Use "Work Finished" when all non-skipped tasks are complete and no attention or fault tasks are present.
                - Use "Work In Progress" when any task is marked attention or fault, any follow-up is required, or the source data does not clearly confirm normal condition.

                - maintenanceResult should state the overall result of the maintenance visit based on task statuses, faultCount, skippedCount, and notes.
                - alarmStatus should be "Alarm reported" when at least one task has status attention or fault, otherwise "No alarm reported".

                - executiveSummary should summarize:
                  maintenance scope + completed activities + abnormal findings + required action.

                - maintenanceSummary should be a concise customer-ready narrative of the maintenance activities performed on the machine.

                - activities must include every source task exactly once.
                - Preserve each task category, task text, tool, status, notes, measuredValue, unit, and completedAt when provided.
                - Do not omit skipped or not-applicable tasks; represent their status accurately.
                - photos should be an array of concise photo references for the task when source photos or photo ids are provided, otherwise an empty array.

                - alarms must be an array of objects with description and status.
                - Create one alarm object for each task with status "attention" or "fault".
                - For "attention" tasks, use status "Monitoring" unless the notes clearly indicate a more specific status.
                - For "fault" tasks, use status "Open" unless the source data clearly indicates it was resolved.
                - If no task has status "attention" or "fault", return an empty alarms array.
                - Do not create alarms for ok, skipped, pending, or not-applicable tasks unless notes explicitly describe an alarm or abnormal finding.

                - recommendations must be clear, actionable, and relevant to the maintenance result. Use manual context only when it directly supports the reported task or abnormal condition.
                - furtherActionRequired should summarize required follow-up, monitoring, return visit, parts, pending confirmation, or operational limitation based only on source data. If no follow-up is required, state that no further action is required based on the completed maintenance tasks.

                Manual reference context:
                %s

                Source report data:

                Report Type:
                Machine Maintenance Report

                Completed At:
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

                Failure Classification:
                Component: %s
                Mode: %s
                Code: %s

                Failure / Maintenance Notes:
                %s

                Fault Count:
                %s

                Skipped Count:
                %s

                All Maintenance Tasks:
                %s

                Tasks Requiring Alarm Items:
                %s

                Attached Photos:
                %s
                """.formatted(
                manualContext,
                nullSafe(report.getCompletedAt()),
                nullSafe(report.getVesselName()),
                nullSafe(resolveVesselImo(report.getVesselImo(), vessel)),
                nullSafe(prefer(report.getVesselType(), vessel != null ? vessel.getVesselType() : null)),
                nullSafe(prefer(report.getOwnerCustomer(), vessel != null ? vessel.getOwnerCustomer() : null)),
                nullSafe(prefer(report.getVesselContact(), vessel != null ? vessel.getVesselContact() : null)),
                nullSafe(report.getMachineTag()),
                nullSafe(report.getMachineModel()),
                nullSafe(prefer(report.getMachineMfg(), machine != null ? machine.getMfg() : null)),
                nullSafe(report.getMachineType()),
                nullSafe(prefer(report.getMachineCompressorType(), machine != null ? machine.getCompressorType() : null)),
                nullSafe(report.getMachineStarterType()),
                nullSafe(prefer(report.getMachineSerialNumber(), machine != null ? machine.getSerialNumber() : null)),
                nullSafe(report.getMachineLocation()),
                nullSafe(prefer(report.getMachineRefrigerant(), machine != null ? machine.getRefrigerant() : null)),
                nullSafe(prefer(report.getMachineOilType(), machine != null ? machine.getOilType() : null)),
                nullSafe(prefer(report.getMachineControlSystem(), machine != null ? machine.getControlSystem() : null)),
                nullSafe(prefer(report.getMachineSoftwareVersion(), machine != null ? machine.getSoftwareVersion() : null)),
                nullSafe(report.getFailureComponent()),
                nullSafe(report.getFailureMode()),
                nullSafe(report.getFailureCode()),
                nullSafe(report.getFailureNotes()),
                String.valueOf(report.getFaultCount() == null ? 0 : report.getFaultCount()),
                String.valueOf(report.getSkippedCount() == null ? 0 : report.getSkippedCount()),
                taskList.isBlank() ? "No maintenance tasks provided." : taskList,
                alarmTaskList.isBlank() ? "No attention or fault tasks." : alarmTaskList,
                photoList
        );
    }

    private String formatTask(PreventiveReportTaskEntity task) {
        return """
                - Task Id: %s
                  Category: %s
                  Task: %s
                  Tool: %s
                  Checked: %s
                  Status: %s
                  Notes: %s
                  Measured Value: %s
                  Unit: %s
                  Photo Ids: %s
                  Completed At: %s
                """.formatted(
                nullSafe(task.getTaskTemplateId()),
                nullSafe(task.getCategory()),
                nullSafe(task.getTaskName()),
                nullSafe(task.getTool()),
                Boolean.TRUE.equals(task.getChecked()) ? "Yes" : "No",
                nullSafe(task.getStatus()),
                nullSafe(task.getNotes()),
                nullSafe(task.getMeasuredValue()),
                nullSafe(task.getUnit()),
                task.getPhotoIds() == null || task.getPhotoIds().isEmpty()
                        ? "None"
                        : String.join(", ", task.getPhotoIds()),
                nullSafe(task.getCompletedAt())
        );
    }

    private boolean isAlarmTask(PreventiveReportTaskEntity task) {
        return "attention".equalsIgnoreCase(task.getStatus()) || "fault".equalsIgnoreCase(task.getStatus());
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    private String prefer(String primary, String fallback) {
        return primary == null || primary.isBlank() ? fallback : primary;
    }

    private String resolveVesselImo(String vesselImo, VesselEntity vessel) {
        return prefer(vesselImo, vessel != null ? vessel.getImoNumber() : null);
    }
}
