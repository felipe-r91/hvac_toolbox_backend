package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.PreventiveTaskDto;
import com.tech.hvac_backend.dto.sync.HealthCheckSyncRequest;
import com.tech.hvac_backend.entity.HealthCheckReportEntity;
import com.tech.hvac_backend.entity.HealthCheckReportTaskEntity;
import com.tech.hvac_backend.repository.HealthCheckReportRepository;
import com.tech.hvac_backend.repository.HealthCheckReportTaskRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckSyncServiceTest {

    private final HealthCheckReportRepositoryStub reportRepositoryStub =
            new HealthCheckReportRepositoryStub();
    private final HealthCheckReportTaskRepositoryStub taskRepositoryStub =
            new HealthCheckReportTaskRepositoryStub();
    private final HealthCheckSyncService service = new HealthCheckSyncService(
            reportRepositoryStub.createProxy(),
            taskRepositoryStub.createProxy()
    );

    @Test
    void syncHealthCheckPayloadPersistsReportAndTasks() {
        HealthCheckSyncRequest request = baseRequest();
        PreventiveTaskDto task = new PreventiveTaskDto();
        task.setId("comp_oil_pressure");
        task.setCategory("Compressor");
        task.setTask("Check oil pressure");
        task.setChecked(true);
        task.setStatus("fault");
        task.setNotes("Oil pressure below expected range.");
        task.setMeasuredValue("2");
        task.setUnit("bar");
        task.setPhotoIds(List.of("photo_1"));
        task.setCompletedAt("2026-06-02T14:20:00.000Z");
        request.setTasks(List.of(task));

        boolean created = service.syncHealthCheckReport(request);

        HealthCheckReportEntity savedReport = reportRepositoryStub.savedReport;
        HealthCheckReportTaskEntity savedTask = taskRepositoryStub.savedTasks.getFirst();

        assertThat(created).isTrue();
        assertThat(savedReport.getId()).isEqualTo("health_check_123");
        assertThat(savedReport.getOverallStatus()).isEqualTo("down");
        assertThat(savedReport.getFaultCount()).isZero();
        assertThat(savedReport.getSynced()).isTrue();
        assertThat(savedTask.getReportId()).isEqualTo("health_check_123");
        assertThat(savedTask.getTaskTemplateId()).isEqualTo("comp_oil_pressure");
        assertThat(savedTask.getStatus()).isEqualTo("fault");
        assertThat(savedTask.getPhotoIds()).containsExactly("photo_1");
    }

    private HealthCheckSyncRequest baseRequest() {
        HealthCheckSyncRequest request = new HealthCheckSyncRequest();
        request.setId("health_check_123");
        request.setVesselId("vessel_1");
        request.setVesselName("MV Example");
        request.setMachineId("machine_1");
        request.setMachineTag("AC-01");
        request.setMachineSerialNumber("SN123");
        request.setMachineModel("Model X");
        request.setMachineType("Chiller");
        request.setMachineLocation("Engine Room");
        request.setMachineStarterType("VSD");
        request.setCompletedAt("2026-06-02T14:25:00.000Z");
        request.setDowntimeReason("");
        request.setFailureNotes("");
        request.setFaultCount(0);
        request.setSkippedCount(0);
        request.setSynced(false);
        request.setTasks(List.of());
        return request;
    }

    private static class HealthCheckReportRepositoryStub implements InvocationHandler {
        private HealthCheckReportEntity savedReport;

        private HealthCheckReportRepository createProxy() {
            return (HealthCheckReportRepository) Proxy.newProxyInstance(
                    HealthCheckReportRepository.class.getClassLoader(),
                    new Class<?>[]{HealthCheckReportRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "existsById" -> false;
                case "save" -> {
                    savedReport = (HealthCheckReportEntity) args[0];
                    yield savedReport;
                }
                case "toString" -> "HealthCheckReportRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckReportTaskRepositoryStub implements InvocationHandler {
        private List<HealthCheckReportTaskEntity> savedTasks = new ArrayList<>();

        private HealthCheckReportTaskRepository createProxy() {
            return (HealthCheckReportTaskRepository) Proxy.newProxyInstance(
                    HealthCheckReportTaskRepository.class.getClassLoader(),
                    new Class<?>[]{HealthCheckReportTaskRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "saveAll" -> {
                    savedTasks = new ArrayList<>();
                    for (Object item : (Iterable<?>) args[0]) {
                        savedTasks.add((HealthCheckReportTaskEntity) item);
                    }
                    yield savedTasks;
                }
                case "toString" -> "HealthCheckReportTaskRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static Object defaultValue(Class<?> returnType) {
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
