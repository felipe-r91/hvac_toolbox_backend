package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.PreventiveTaskDto;
import com.tech.hvac_backend.dto.sync.PreventiveSyncRequest;
import com.tech.hvac_backend.entity.PreventiveReportEntity;
import com.tech.hvac_backend.entity.PreventiveReportTaskEntity;
import com.tech.hvac_backend.repository.PreventiveReportRepository;
import com.tech.hvac_backend.repository.PreventiveReportTaskRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class PreventiveSyncServiceTest {

    private final PreventiveReportRepositoryStub preventiveReportRepositoryStub =
            new PreventiveReportRepositoryStub();
    private final PreventiveReportTaskRepositoryStub preventiveReportTaskRepositoryStub =
            new PreventiveReportTaskRepositoryStub();
    private final PreventiveSyncService service = new PreventiveSyncService(
            preventiveReportRepositoryStub.createProxy(),
            preventiveReportTaskRepositoryStub.createProxy()
    );

    @Test
    void syncMachineMaintenancePayloadPersistsOkTaskAndDerivesDefaults() {
        PreventiveSyncRequest request = baseRequest();
        request.setFaultCount(0);
        request.setSkippedCount(1);
        request.setTasks(List.of(okTask()));

        boolean created = service.syncPreventiveReport(request);

        PreventiveReportEntity saved = preventiveReportRepositoryStub.savedReport;
        assertThat(created).isTrue();
        assertThat(saved.getId()).isEqualTo("report_123");
        assertThat(saved.getReportCategory()).isEqualTo("machine_maintenance");
        assertThat(saved.getOverallStatus()).isEqualTo("online");
        assertThat(saved.getFaultCount()).isZero();
        assertThat(saved.getSkippedCount()).isEqualTo(1);
        assertThat(saved.getSynced()).isTrue();
        assertThat(preventiveReportTaskRepositoryStub.savedTasks).hasSize(1);
    }

    @Test
    void syncMachineMaintenancePayloadPersistsTaskPhotoIdsAndDerivesFaultStatus() {
        PreventiveSyncRequest request = baseRequest();
        PreventiveTaskDto task = new PreventiveTaskDto();
        task.setId("task_1");
        task.setCategory("Electrical");
        task.setTask("Inspect starter cabinet");
        task.setChecked(true);
        task.setStatus("fault");
        task.setNotes("Contactor chatter observed.");
        task.setMeasuredValue("18");
        task.setUnit("A");
        task.setPhotoIds(List.of("photo_1", "photo_2"));
        task.setCompletedAt("2026-05-28T14:20:00.000Z");
        request.setTasks(List.of(task));

        service.syncPreventiveReport(request);

        PreventiveReportTaskEntity savedTask = preventiveReportTaskRepositoryStub.savedTasks.getFirst();
        assertThat(preventiveReportRepositoryStub.savedReport.getOverallStatus()).isEqualTo("down");
        assertThat(savedTask.getTaskTemplateId()).isEqualTo("task_1");
        assertThat(savedTask.getStatus()).isEqualTo("fault");
        assertThat(savedTask.getPhotoIds()).containsExactly("photo_1", "photo_2");
    }

    @Test
    void syncPreventiveRejectsHealthCheckPayloads() {
        PreventiveSyncRequest request = baseRequest();
        request.setReportCategory("health_check");
        request.setTasks(List.of(okTask()));

        assertThatThrownBy(() -> service.syncPreventiveReport(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Use /api/sync/health-check for health check reports.");
    }

    private PreventiveSyncRequest baseRequest() {
        PreventiveSyncRequest request = new PreventiveSyncRequest();
        request.setId("report_123");
        request.setVesselId("vessel_1");
        request.setVesselName("MV Example");
        request.setMachineId("machine_1");
        request.setMachineTag("AC-01");
        request.setMachineSerialNumber("SN123");
        request.setMachineModel("Model X");
        request.setMachineType("Chiller");
        request.setMachineLocation("Engine Room");
        request.setMachineStarterType("VSD");
        request.setCompletedAt("2026-05-28T14:25:00.000Z");
        request.setDowntimeReason("");
        request.setFailureNotes("");
        request.setFaultCount(0);
        request.setSkippedCount(0);
        request.setSynced(false);
        request.setTasks(List.of());
        return request;
    }

    private PreventiveTaskDto okTask() {
        PreventiveTaskDto task = new PreventiveTaskDto();
        task.setId("task_ok");
        task.setCategory("General");
        task.setTask("Inspect filters");
        task.setChecked(true);
        task.setStatus("ok");
        task.setNotes("");
        task.setMeasuredValue("");
        task.setCompletedAt("2026-05-28T14:20:00.000Z");
        return task;
    }

    private static class PreventiveReportRepositoryStub implements InvocationHandler {
        private PreventiveReportEntity savedReport;

        private PreventiveReportRepository createProxy() {
            return (PreventiveReportRepository) Proxy.newProxyInstance(
                    PreventiveReportRepository.class.getClassLoader(),
                    new Class<?>[]{PreventiveReportRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "existsById" -> false;
                case "save" -> {
                    savedReport = (PreventiveReportEntity) args[0];
                    yield savedReport;
                }
                case "toString" -> "PreventiveReportRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class PreventiveReportTaskRepositoryStub implements InvocationHandler {
        private List<PreventiveReportTaskEntity> savedTasks = new ArrayList<>();

        private PreventiveReportTaskRepository createProxy() {
            return (PreventiveReportTaskRepository) Proxy.newProxyInstance(
                    PreventiveReportTaskRepository.class.getClassLoader(),
                    new Class<?>[]{PreventiveReportTaskRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "saveAll" -> {
                    savedTasks = new ArrayList<>();
                    for (Object item : (Iterable<?>) args[0]) {
                        savedTasks.add((PreventiveReportTaskEntity) item);
                    }
                    yield savedTasks;
                }
                case "toString" -> "PreventiveReportTaskRepositoryStub";
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
