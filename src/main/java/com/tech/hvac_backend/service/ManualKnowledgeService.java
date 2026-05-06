package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.CfrDraftEntity;
import com.tech.hvac_backend.entity.CorrectiveDraftEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.repository.ManualKnowledgeChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ManualKnowledgeService {

    private final ManualKnowledgeChunkRepository repository;

    public ManualKnowledgeService(ManualKnowledgeChunkRepository repository) {
        this.repository = repository;
    }

    public List<ManualKnowledgeChunkEntity> findRelevantChunks(CfrDraftEntity draft) {
        return findRelevantChunks(new ManualSearchInput(
                draft.getMachineModel(),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                draft.getConditionFound(),
                draft.getSymptomsObserved(),
                draft.getAlarmsObserved(),
                draft.getOperationalImpact(),
                draft.getPreliminaryDiagnosis(),
                draft.getConfirmedCause(),
                null,
                draft.getRecommendations(),
                draft.getFurtherActionRequired()
        ));
    }

    public List<ManualKnowledgeChunkEntity> findRelevantChunks(CorrectiveDraftEntity draft) {
        return findRelevantChunks(new ManualSearchInput(
                draft.getMachineModel(),
                draft.getMachineType(),
                draft.getMachineStarterType(),
                draft.getFailureComponent(),
                draft.getFailureMode(),
                draft.getFailureCode(),
                draft.getConditionFound(),
                draft.getSymptomsObserved(),
                draft.getAlarmsObserved(),
                draft.getOperationalImpact(),
                draft.getPreliminaryDiagnosis(),
                draft.getConfirmedCause(),
                draft.getCorrectiveAction(),
                draft.getRecommendations(),
                draft.getFurtherActionRequired()
        ));
    }

    private List<ManualKnowledgeChunkEntity> findRelevantChunks(ManualSearchInput input) {
        String model = clean(input.machineModel());
        String query = buildSearchQuery(input);
        List<String> topics = detectTopics(input);

        if (model.isBlank()) {
            return query.isBlank() ? List.of() : repository.searchFallback(query);
        }

        if (query.isBlank()) {
            return repository.findBestChunksByModel(model);
        }

        List<ManualKnowledgeChunkEntity> chunks =
                repository.searchRelevantChunks(model, query, topics);

        if (chunks.isEmpty()) {
            chunks = repository.findBestChunksByModel(model);
        }

        if (chunks.isEmpty()) {
            chunks = repository.searchFallback(query);
        }

        return chunks;
    }

    public String buildManualContext(List<ManualKnowledgeChunkEntity> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return "No relevant manual reference was found.";
        }

        return chunks.stream()
                .map(chunk -> """
                        Manual Reference:
                        Section: %s
                        Pages: %s-%s
                        Topics: %s
                        Engineering Reference Content:
                        %s
                        """.formatted(
                        nullSafe(chunk.getSection()),
                        chunk.getPageStart(),
                        chunk.getPageEnd(),
                        chunk.getTopics() == null ? "Not provided" : String.join(", ", chunk.getTopics()),
                        nullSafe(chunk.getContent())
                ))
                .collect(Collectors.joining("\n---\n"));
    }

    private String buildSearchQuery(ManualSearchInput input) {
        return String.join(" ",
                clean(input.machineModel()),
                clean(input.machineType()),
                clean(input.machineStarterType()),
                clean(input.failureComponent()),
                clean(input.failureMode()),
                clean(input.failureCode()),
                clean(input.conditionFound()),
                clean(input.symptomsObserved()),
                clean(input.alarmsObserved()),
                clean(input.operationalImpact()),
                clean(input.preliminaryDiagnosis()),
                clean(input.confirmedCause()),
                clean(input.correctiveAction()),
                clean(input.recommendations()),
                clean(input.furtherActionRequired())
        ).trim();
    }

    private List<String> detectTopics(ManualSearchInput input) {
        String text = buildSearchQuery(input).toLowerCase();

        Set<String> topics = new LinkedHashSet<>();

        addIf(text, topics, "fault_finding",
                "fault", "alarm", "trip", "failure", "symptom", "diagnosis", "corrective");

        addIf(text, topics, "maintenance",
                "maintenance", "service", "repair", "replace", "inspect", "clean", "adjust", "calibrate", "corrective action");

        addIf(text, topics, "compressor",
                "compressor", "slide valve", "capacity", "rotor", "bearing", "shaft", "seal");

        addIf(text, topics, "oil_system",
                "oil", "lubrication", "oil pressure", "oil filter", "oil separator", "oil pump");

        addIf(text, topics, "vsd_drive",
                "vsd", "drive", "frequency", "motor current", "starter", "overload", "phase", "electrical");

        addIf(text, topics, "capacity_control",
                "capacity", "load", "unload", "slide valve", "sov03", "sov04");

        addIf(text, topics, "expansion_valve",
                "expansion valve", "superheat", "liquid carryover", "eev", "exv", "cov01");

        addIf(text, topics, "heat_exchanger",
                "evaporator", "condenser", "cooling water", "chilled water", "brine", "flow", "heat exchanger");

        addIf(text, topics, "safety",
                "emergency", "safety", "danger", "risk", "shutdown", "lockout", "unsafe");

        addIf(text, topics, "operating_data",
                "temperature", "pressure", "setpoint", "reading", "value", "amps", "current", "voltage");

        if (topics.isEmpty()) {
            topics.add("fault_finding");
            topics.add("maintenance");
            topics.add("operating_data");
        }

        return new ArrayList<>(topics);
    }

    private void addIf(String text, Set<String> topics, String topic, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                topics.add(topic);
                return;
            }
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    private record ManualSearchInput(
            String machineModel,
            String machineType,
            String machineStarterType,
            String failureComponent,
            String failureMode,
            String failureCode,
            String conditionFound,
            String symptomsObserved,
            String alarmsObserved,
            String operationalImpact,
            String preliminaryDiagnosis,
            String confirmedCause,
            String correctiveAction,
            String recommendations,
            String furtherActionRequired
    ) {
    }
}
