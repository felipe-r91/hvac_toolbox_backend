package com.tech.hvac_backend.service;

import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.ManualKnowledgeChunkEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DailyReportsServiceReportPromptBuilderServiceTest {

    @Test
    void buildPromptRequiresConciseSynthesisAndFourRelevantPhotos() {
        DailyReportsServiceReportPromptBuilderService service =
                new DailyReportsServiceReportPromptBuilderService(
                        new ManualKnowledgeServiceStub()
                );
        DailyDraftEntity first = dailyReport("daily_1", "2026-06-08", "Inspected compressor.");
        DailyDraftEntity second = dailyReport("daily_2", "2026-06-09", "Adjusted oil pressure.");

        String prompt = service.buildPrompt(
                List.of(first, second),
                null,
                null,
                List.of(
                        photo("photo_1", "daily_1"),
                        photo("photo_2", "daily_1"),
                        photo("photo_3", "daily_2"),
                        photo("photo_4", "daily_2"),
                        photo("photo_5", "daily_2")
                )
        );

        assertThat(prompt)
                .contains("This is a synthesis task, not a transcription task")
                .contains("Do not create a day-by-day diary or an exhaustive list")
                .contains("workConducted: 4 to 8 consolidated action items")
                .contains("selectedPhotoIds must contain exactly 4 unique ids")
                .contains("Daily Report Id: daily_1")
                .contains("Daily Report Id: daily_2")
                .contains("Id: photo_5");
    }

    private DailyDraftEntity dailyReport(String id, String createdAt, String work) {
        DailyDraftEntity report = new DailyDraftEntity();
        report.setId(id);
        report.setVesselId("vessel_1");
        report.setVesselName("MV Example");
        report.setMachineId("machine_1");
        report.setMachineTag("AC-01");
        report.setMachineModel("Model X");
        report.setCreatedAt(createdAt);
        report.setWorkConductedToday(work);
        return report;
    }

    private PhotoRecordEntity photo(String id, String ownerId) {
        PhotoRecordEntity photo = new PhotoRecordEntity();
        photo.setId(id);
        photo.setOwnerType(PhotoOwnerType.DAILY_DRAFT);
        photo.setOwnerId(ownerId);
        photo.setMachineId("machine_1");
        photo.setCreatedAt("2026-06-09");
        photo.setFilename(id + ".jpg");
        photo.setCaption("Evidence " + id);
        photo.setStorageKey(id);
        return photo;
    }

    private static class ManualKnowledgeServiceStub extends ManualKnowledgeService {

        private ManualKnowledgeServiceStub() {
            super(null);
        }

        @Override
        public List<ManualKnowledgeChunkEntity> findRelevantChunks(DailyDraftEntity draft) {
            return List.of();
        }

        @Override
        public String buildManualContext(List<ManualKnowledgeChunkEntity> chunks) {
            return "No relevant manual reference was found.";
        }
    }
}
