package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CfrPromptBuilderService {

    public String buildPrompt(CfrDraftEntity draft, List<PhotoRecordEntity> photos) {
        String photoList = photos.stream()
                .map(photo -> "- Caption: " + nullSafe(photo.getCaption())
                        + " | Filename: " + nullSafe(photo.getFilename()))
                .collect(Collectors.joining("\n"));

        return """
                You are generating a customer-ready Johnson Controls Marine & Navy Conditions Found Report.

                Write in a professional technical field-service tone.
                Do not invent facts.
                If information is missing, keep the statement general or return an empty string for layout-only fields.
                Use clear wording suitable for vessel customer review.

                You must return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the response in ```json.
                Do not include explanations.

                The JSON must match EXACTLY this structure:

                {
                  "reportNo": "",
                  "title": "Conditions Found Report",
                  "subtitle": "",
                  "company": "Johnson Controls",
                  "branch": "",
                  "date": "",
                  "serviceOrder": "",
                  "engineer": "",
                  "projectManager": "",
                  "location": "",
                  "machineStatus": "",
                  "severity": "",
                  "finalCondition": "",
                  "executiveSummary": "",
                  "conditionFound": "",
                  "alarms": [],
                  "operationalImpact": "",
                  "probableRootCause": "",
                  "recommendations": [],
                  "furtherActionRequired": "",
                  "ehsStatement": ""
                }

                Field rules:
                - reportNo, branch, serviceOrder, engineer, projectManager and ehsStatement are layout fields. Return empty string unless explicitly provided.
                - title must be "Conditions Found Report".
                - company must be "Johnson Controls".
                - subtitle should summarize the machine, for example: "AC#1 – Liquid Chiller".
                - date should be based on the report created date.
                - location should use the machine location or vessel/location information available.
                - severity should be "Low", "Medium", or "High" based only on the provided condition and impact.
                - finalCondition should describe the machine final condition based on machine status and report data.
                - probableRootCause should be determined from the condition found, symptoms, alarms, operational impact, preliminary diagnosis, confirmed cause, failure classification, and recommendations.
                - If confirmed cause is provided, use it as the primary basis for probableRootCause.
                - If confirmed cause is not provided, infer the most probable technical cause using only the source report data.
                - Do not invent specific failed parts, measurements, or events that are not supported by the source report.
                - If there is not enough information to determine a probable root cause, return a general statement explaining that the root cause could not be conclusively determined from the available information.
                - alarms must be an array of strings. If no alarms are provided, return an empty array.
                - recommendations must be an array of strings. If no recommendations are provided, return an empty array.
                

                Source report data:

                Report Type:
                Conditions Found Report

                Created At:
                %s

                Vessel:
                Name: %s

                Machine:
                Tag: %s
                Model: %s
                Type: %s
                Starter Type: %s
                Location: %s

                Machine Status:
                %s

                Failure Classification:
                Component: %s
                Mode: %s
                Code: %s

                Condition Found:
                %s

                Symptoms Observed:
                %s

                Alarms / Abnormal Readings:
                %s

                Operational Impact:
                %s

                Preliminary Diagnosis:
                %s

                Confirmed Cause:
                %s

                Recommendations:
                %s

                Further Action Required:
                %s

                Attached Photos:
                %s
                """.formatted(
                nullSafe(draft.getCreatedAt()),
                nullSafe(draft.getVesselName()),
                nullSafe(draft.getMachineTag()),
                nullSafe(draft.getMachineModel()),
                nullSafe(draft.getMachineType()),
                nullSafe(draft.getMachineStarterType()),
                nullSafe(draft.getMachineLocation()),
                nullSafe(draft.getMachineStatus()),
                nullSafe(draft.getFailureComponent()),
                nullSafe(draft.getFailureMode()),
                nullSafe(draft.getFailureCode()),
                nullSafe(draft.getConditionFound()),
                nullSafe(draft.getSymptomsObserved()),
                nullSafe(draft.getAlarmsObserved()),
                nullSafe(draft.getOperationalImpact()),
                nullSafe(draft.getPreliminaryDiagnosis()),
                nullSafe(draft.getConfirmedCause()),
                nullSafe(draft.getRecommendations()),
                nullSafe(draft.getFurtherActionRequired()),
                photoList.isBlank() ? "No attached photos." : photoList
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }
}