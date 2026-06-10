package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.ai.AiServiceReportFromDailyReportsResult;
import com.tech.hvac_backend.dto.ai.AiServiceReportResponse;
import com.tech.hvac_backend.dto.request.ServiceReportFromDailyReportsRequest;
import com.tech.hvac_backend.dto.response.ServiceReportFromDailyReportsResponse;
import com.tech.hvac_backend.entity.DailyDraftEntity;
import com.tech.hvac_backend.entity.PhotoOwnerType;
import com.tech.hvac_backend.entity.PhotoRecordEntity;
import com.tech.hvac_backend.repository.DailyDraftRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import com.tech.hvac_backend.repository.PhotoRecordRepository;
import com.tech.hvac_backend.repository.VesselRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceReportFromDailyReportsServiceTest {

    private final RepositoryStub repositoryStub = new RepositoryStub();
    private final PromptBuilderStub promptBuilder = new PromptBuilderStub();
    private final AiGeneratorStub aiGenerator = new AiGeneratorStub();
    private final ServiceReportFromDailyReportsService service =
            new ServiceReportFromDailyReportsService(
                    repositoryStub.proxy(DailyDraftRepository.class),
                    repositoryStub.proxy(PhotoRecordRepository.class),
                    repositoryStub.proxy(MachineRepository.class),
                    repositoryStub.proxy(VesselRepository.class),
                    promptBuilder,
                    aiGenerator
            );

    @Test
    void generateReturnsUnchangedServiceReportAndFourAiSelectedPhotos() {
        repositoryStub.dailyReports = List.of(
                dailyReport(
                        "daily_2",
                        "machine_1",
                        "2026-06-09T08:00:00Z",
                        "Adjusted oil pressure.",
                        "Confirm stable operation."
                ),
                dailyReport(
                        "daily_1",
                        "machine_1",
                        "2026-06-08T08:00:00Z",
                        "Inspected compressor.",
                        "Monitor oil pressure."
                )
        );
        repositoryStub.photos.put("daily_1", List.of(
                photo("photo_1", "daily_1", "2026-06-08T09:00:00Z")
        ));
        repositoryStub.photos.put("daily_2", List.of(
                photo("photo_2", "daily_2", "2026-06-09T09:00:00Z"),
                photo("photo_3", "daily_2", "2026-06-09T10:00:00Z"),
                photo("photo_4", "daily_2", "2026-06-09T11:00:00Z"),
                photo("photo_5", "daily_2", "2026-06-09T12:00:00Z")
        ));

        AiServiceReportResponse aiReport = new AiServiceReportResponse();
        aiReport.setTitle("Service Report");
        aiGenerator.result = new AiServiceReportFromDailyReportsResult();
        aiGenerator.result.setServiceReport(aiReport);
        aiGenerator.result.setSelectedPhotoIds(List.of(
                "unknown_photo",
                "photo_5",
                "photo_3",
                "photo_2",
                "photo_4",
                "photo_1"
        ));

        ServiceReportFromDailyReportsResponse response =
                service.generate(request("daily_1", "daily_2"));

        assertThat(response.getAiReport()).isSameAs(aiReport);
        assertThat(response.getSourceReport().getReportCategory()).isEqualTo("service_report");
        assertThat(response.getSourceReport().getMachineReturnedToService()).isEqualTo("unknown");
        assertThat(response.getSourceReport().getCreatedAt()).isEqualTo("2026-06-09T08:00:00Z");
        assertThat(response.getSourceReport().getWorkPerformed())
                .contains("Inspected compressor.", "Adjusted oil pressure.");
        assertThat(response.getSourceReport().getPhotos())
                .extracting("id")
                .containsExactly("photo_5", "photo_3", "photo_2", "photo_4");
        assertThat(aiGenerator.receivedPrompt).isEqualTo("prompt");
    }

    @Test
    void generateRejectsReportsFromDifferentMachines() {
        repositoryStub.dailyReports = List.of(
                dailyReport("daily_1", "machine_1", "2026-06-08", "Work 1", null),
                dailyReport("daily_2", "machine_2", "2026-06-09", "Work 2", null)
        );

        assertThatThrownBy(() -> service.generate(request("daily_1", "daily_2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("All Daily Reports must belong to the same machine and vessel.");

        assertThat(aiGenerator.receivedPrompt).isNull();
    }

    @Test
    void generateRejectsDuplicateReportIds() {
        assertThatThrownBy(() -> service.generate(request("daily_1", "daily_1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least two distinct Daily Report ids are required.");
    }

    private ServiceReportFromDailyReportsRequest request(String... ids) {
        ServiceReportFromDailyReportsRequest request =
                new ServiceReportFromDailyReportsRequest();
        request.setDailyReportIds(List.of(ids));
        return request;
    }

    private DailyDraftEntity dailyReport(
            String id,
            String machineId,
            String createdAt,
            String work,
            String furtherActions
    ) {
        DailyDraftEntity report = new DailyDraftEntity();
        report.setId(id);
        report.setVesselId("vessel_1");
        report.setVesselName("MV Example");
        report.setMachineId(machineId);
        report.setMachineTag("AC-01");
        report.setMachineModel("Model X");
        report.setCreatedAt(createdAt);
        report.setReportCategory("daily");
        report.setWorkConductedToday(work);
        report.setFurtherActions(furtherActions);
        report.setSynced(true);
        return report;
    }

    private PhotoRecordEntity photo(String id, String ownerId, String createdAt) {
        PhotoRecordEntity photo = new PhotoRecordEntity();
        photo.setId(id);
        photo.setOwnerType(PhotoOwnerType.DAILY_DRAFT);
        photo.setOwnerId(ownerId);
        photo.setMachineId("machine_1");
        photo.setFilename(id + ".jpg");
        photo.setCaption("Evidence " + id);
        photo.setCreatedAt(createdAt);
        photo.setStorageKey(id);
        photo.setPreviewUrl("https://example.test/" + id);
        return photo;
    }

    private static class PromptBuilderStub extends DailyReportsServiceReportPromptBuilderService {

        private PromptBuilderStub() {
            super(null);
        }

        @Override
        public String buildPrompt(
                List<DailyDraftEntity> dailyReports,
                com.tech.hvac_backend.entity.MachineEntity machine,
                com.tech.hvac_backend.entity.VesselEntity vessel,
                List<PhotoRecordEntity> photos
        ) {
            return "prompt";
        }
    }

    private static class AiGeneratorStub implements ServiceReportFromDailyReportsAiGenerator {

        private AiServiceReportFromDailyReportsResult result;
        private String receivedPrompt;

        @Override
        public AiServiceReportFromDailyReportsResult generateServiceReportFromDailyReports(
                String prompt
        ) {
            receivedPrompt = prompt;
            return result;
        }
    }

    private static class RepositoryStub implements InvocationHandler {

        private List<DailyDraftEntity> dailyReports = new ArrayList<>();
        private final Map<String, List<PhotoRecordEntity>> photos = new HashMap<>();

        @SuppressWarnings("unchecked")
        private <T> T proxy(Class<T> repositoryType) {
            return (T) Proxy.newProxyInstance(
                    repositoryType.getClassLoader(),
                    new Class<?>[]{repositoryType},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findAllById" -> dailyReports;
                case "findByOwnerTypeAndOwnerIdOrderByCreatedAtAsc" ->
                        photos.getOrDefault((String) args[1], List.of());
                case "findById" -> Optional.empty();
                case "toString" -> "RepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }

        private Object defaultValue(Class<?> returnType) {
            if (returnType == Boolean.TYPE) {
                return false;
            }
            if (returnType == Long.TYPE) {
                return 0L;
            }
            if (returnType == Integer.TYPE) {
                return 0;
            }
            return null;
        }
    }
}
