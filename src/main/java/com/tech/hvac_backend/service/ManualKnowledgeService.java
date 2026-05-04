package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.CfrDraftEntity;
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
        String model = clean(draft.getMachineModel());
        String query = buildSearchQuery(draft);
        List<String> topics = detectTopics(draft);

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

    private String buildSearchQuery(CfrDraftEntity draft) {
        return String.join(" ",
                clean(draft.getMachineModel()),
                clean(draft.getMachineType()),
                clean(draft.getMachineStarterType()),
                clean(draft.getFailureComponent()),
                clean(draft.getFailureMode()),
                clean(draft.getFailureCode()),
                clean(draft.getConditionFound()),
                clean(draft.getSymptomsObserved()),
                clean(draft.getAlarmsObserved()),
                clean(draft.getOperationalImpact()),
                clean(draft.getPreliminaryDiagnosis()),
                clean(draft.getConfirmedCause()),
                clean(draft.getRecommendations())
        ).trim();
    }

    private List<String> detectTopics(CfrDraftEntity draft) {
        String text = buildSearchQuery(draft).toLowerCase();

        Set<String> topics = new LinkedHashSet<>();

        addIf(text, topics, "fault_finding",
                "fault", "alarm", "trip", "failure", "symptom", "diagnosis");

        addIf(text, topics, "compressor",
                "compressor", "slide valve", "capacity", "rotor", "bearing", "shaft");

        addIf(text, topics, "oil_system",
                "oil", "lubrication", "oil pressure", "oil filter", "oil separator");

        addIf(text, topics, "vsd_drive",
                "vsd", "drive", "frequency", "motor current", "starter");

        addIf(text, topics, "capacity_control",
                "capacity", "load", "unload", "slide valve", "sov03", "sov04");

        addIf(text, topics, "expansion_valve",
                "expansion valve", "superheat", "liquid carryover", "eev", "cov01");

        addIf(text, topics, "heat_exchanger",
                "evaporator", "condenser", "cooling water", "chilled water", "flow");

        addIf(text, topics, "safety",
                "emergency", "safety", "danger", "risk", "shutdown");

        addIf(text, topics, "operating_data",
                "temperature", "pressure", "setpoint", "reading", "value");

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
}