package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MachineHealthCheckResponse;
import com.tech.hvac_backend.dto.response.MaintenancePlanTaskResponse;
import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateTaskEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import com.tech.hvac_backend.entity.MachineEntity;
import com.tech.hvac_backend.exception.ResourceNotFoundException;
import com.tech.hvac_backend.repository.HealthCheckTemplateRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateTaskRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateVersionRepository;
import com.tech.hvac_backend.repository.MachineRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HealthCheckQueryServiceTest {

    private final MachineRepositoryStub machineRepositoryStub = new MachineRepositoryStub();
    private final HealthCheckTemplateRepositoryStub templateRepositoryStub =
            new HealthCheckTemplateRepositoryStub();
    private final HealthCheckTemplateVersionRepositoryStub versionRepositoryStub =
            new HealthCheckTemplateVersionRepositoryStub();
    private final HealthCheckTemplateTaskRepositoryStub taskRepositoryStub = new HealthCheckTemplateTaskRepositoryStub();
    private final HealthCheckQueryService service = new HealthCheckQueryService(
            machineRepositoryStub.createProxy(),
            templateRepositoryStub.createProxy(),
            versionRepositoryStub.createProxy(),
            taskRepositoryStub.createProxy()
    );

    @Test
    void getHealthCheckReturnsMachineAndHealthCheckTasks() {
        machineRepositoryStub.machine = machine();
        templateRepositoryStub.template = template(true);
        versionRepositoryStub.publishedVersion = version("health_check_template_version_2", 2);
        taskRepositoryStub.tasks = List.of(
                task("hc_1", "health_check_template_version_2", "HC-COMP-001", "Compressor", "Check electrical current", "Clamp meter", true, true, "Amps", 1),
                task("hc_2", "health_check_template_version_2", "HC-EVAP-001", "Evaporator", "Leaving Chilled Water Temperature", null, true, true, "F", 2)
        );

        MachineHealthCheckResponse response = service.getHealthCheck("machine_1");

        assertThat(response.getMachine().getId()).isEqualTo("machine_1");
        assertThat(response.getMachine().getTag()).isEqualTo("AC-01");
        assertThat(response.getTemplateCode()).isEqualTo("HEALTH_CHECK");
        assertThat(response.getTemplateVersionId()).isEqualTo("health_check_template_version_2");
        assertThat(response.getTemplateVersionNumber()).isEqualTo(2);
        assertThat(response.getTasks()).hasSize(2);

        MaintenancePlanTaskResponse firstTask = response.getTasks().getFirst();
        assertThat(firstTask.getId()).isEqualTo("HC-COMP-001");
        assertThat(firstTask.getCategory()).isEqualTo("Compressor");
        assertThat(firstTask.getTask()).isEqualTo("Check electrical current");
        assertThat(firstTask.getTool()).isEqualTo("Clamp meter");
        assertThat(firstTask.getChecked()).isFalse();
        assertThat(firstTask.getStatus()).isEqualTo("pending");
        assertThat(firstTask.getNotes()).isEmpty();
        assertThat(firstTask.getMeasuredValue()).isEmpty();
        assertThat(firstTask.getUnit()).isEqualTo("Amps");
        assertThat(firstTask.getRequired()).isTrue();
        assertThat(firstTask.getMeasurable()).isTrue();
        assertThat(firstTask.getPhotoRequiredOnFault()).isTrue();
        assertThat(firstTask.getPhotoRequiredOnAttention()).isTrue();
    }

    @Test
    void getHealthCheckDefaultsBlankTaskState() {
        machineRepositoryStub.machine = machine();
        templateRepositoryStub.template = template(true);
        versionRepositoryStub.publishedVersion = version("health_check_template_version_1", 1);
        taskRepositoryStub.tasks = List.of(
                task("hc_3", "health_check_template_version_1", "HC-START-001", "Starter", "Check Fans", null, true, false, null, 3)
        );

        MaintenancePlanTaskResponse task = service.getHealthCheck("machine_1").getTasks().getFirst();

        assertThat(task.getStatus()).isEqualTo("pending");
        assertThat(task.getNotes()).isEmpty();
        assertThat(task.getMeasuredValue()).isEmpty();
        assertThat(task.getUnit()).isNull();
        assertThat(task.getRequired()).isTrue();
        assertThat(task.getMeasurable()).isFalse();
    }

    @Test
    void getHealthCheckPreservesDisabledPhotoRequirements() {
        machineRepositoryStub.machine = machine();
        templateRepositoryStub.template = template(true);
        versionRepositoryStub.publishedVersion = version("health_check_template_version_1", 1);
        HealthCheckTemplateTaskEntity sourceTask = task("hc_4", "health_check_template_version_1", "HC-CTRL-001", "Control System", "Check Parameters", null, true, false, null, 4);
        sourceTask.setPhotoRequiredOnFault(false);
        sourceTask.setPhotoRequiredOnAttention(false);
        taskRepositoryStub.tasks = List.of(sourceTask);

        MaintenancePlanTaskResponse task = service.getHealthCheck("machine_1").getTasks().getFirst();

        assertThat(task.getPhotoRequiredOnFault()).isFalse();
        assertThat(task.getPhotoRequiredOnAttention()).isFalse();
    }

    @Test
    void getHealthCheckReturnsNoTasksWhenTemplateIsNotActive() {
        machineRepositoryStub.machine = machine();
        templateRepositoryStub.template = template(false);
        versionRepositoryStub.publishedVersion = version("health_check_template_version_1", 1);
        taskRepositoryStub.tasks = List.of(
                task("hc_5", "health_check_template_version_1", "HC-COMP-004", "Compressor", "Check oil pressure", null, true, true, "bar", 5)
        );

        MachineHealthCheckResponse response = service.getHealthCheck("machine_1");

        assertThat(response.getTemplateCode()).isEqualTo("HEALTH_CHECK");
        assertThat(response.getTemplateVersionId()).isNull();
        assertThat(response.getTemplateVersionNumber()).isNull();
        assertThat(response.getTasks()).isEmpty();
        assertThat(taskRepositoryStub.requestedTemplateVersionId).isNull();
    }

    @Test
    void getHealthCheckThrowsWhenMachineDoesNotExist() {
        machineRepositoryStub.machine = null;

        assertThatThrownBy(() -> service.getHealthCheck("missing"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Machine not found: missing");
    }

    private MachineEntity machine() {
        MachineEntity machine = new MachineEntity();
        machine.setId("machine_1");
        machine.setVesselId("vessel_1");
        machine.setLocation("Engine Room");
        machine.setTag("AC-01");
        machine.setModel("Model X");
        machine.setSerialNumber("SN123");
        machine.setType("Chiller");
        machine.setStarterType("VSD");
        machine.setRefrigerant("R134a");
        machine.setOilType("POE");
        machine.setControlSystem("PLC");
        machine.setSoftwareVersion("1.0");
        machine.setCompressorType("Screw");
        machine.setMfg("Carrier");
        machine.setMachineTemplateVersionId("machine_template_version_1");
        machine.setStarterTemplateVersionId("starter_template_version_1");
        return machine;
    }

    private HealthCheckTemplateEntity template(Boolean active) {
        HealthCheckTemplateEntity template = new HealthCheckTemplateEntity();
        template.setId("health_check_template");
        template.setCode("HEALTH_CHECK");
        template.setName("Health Check");
        template.setIsActive(active);
        return template;
    }

    private HealthCheckTemplateVersionEntity version(String id, Integer versionNumber) {
        HealthCheckTemplateVersionEntity version = new HealthCheckTemplateVersionEntity();
        version.setId(id);
        version.setTemplateId("health_check_template");
        version.setVersionNumber(versionNumber);
        version.setIsPublished(true);
        return version;
    }

    private HealthCheckTemplateTaskEntity task(
            String id,
            String templateVersionId,
            String taskCode,
            String category,
            String taskName,
            String tool,
            Boolean isRequired,
            Boolean measurable,
            String defaultUnit,
            Integer sortOrder
    ) {
        HealthCheckTemplateTaskEntity task = new HealthCheckTemplateTaskEntity();
        task.setId(id);
        task.setTemplateVersionId(templateVersionId);
        task.setTaskCode(taskCode);
        task.setCategory(category);
        task.setTaskName(taskName);
        task.setTool(tool);
        task.setSortOrder(sortOrder);
        task.setIsRequired(isRequired);
        task.setMeasurable(measurable);
        task.setDefaultUnit(defaultUnit);
        task.setPhotoRequiredOnFault(true);
        task.setPhotoRequiredOnAttention(true);
        return task;
    }

    private static class MachineRepositoryStub implements InvocationHandler {
        private MachineEntity machine;

        private MachineRepository createProxy() {
            return (MachineRepository) Proxy.newProxyInstance(
                    MachineRepository.class.getClassLoader(),
                    new Class<?>[]{MachineRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findById" -> Optional.ofNullable(machine)
                        .filter(entity -> entity.getId().equals(args[0]));
                case "toString" -> "MachineRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckTemplateRepositoryStub implements InvocationHandler {
        private HealthCheckTemplateEntity template;

        private HealthCheckTemplateRepository createProxy() {
            return (HealthCheckTemplateRepository) Proxy.newProxyInstance(
                    HealthCheckTemplateRepository.class.getClassLoader(),
                    new Class<?>[]{HealthCheckTemplateRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findByCode" -> "HEALTH_CHECK".equals(args[0])
                        ? Optional.ofNullable(template)
                        : Optional.empty();
                case "toString" -> "HealthCheckTemplateRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckTemplateVersionRepositoryStub implements InvocationHandler {
        private HealthCheckTemplateVersionEntity publishedVersion;

        private HealthCheckTemplateVersionRepository createProxy() {
            return (HealthCheckTemplateVersionRepository) Proxy.newProxyInstance(
                    HealthCheckTemplateVersionRepository.class.getClassLoader(),
                    new Class<?>[]{HealthCheckTemplateVersionRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findFirstByTemplateIdAndIsPublishedTrueOrderByVersionNumberDesc" ->
                        publishedVersion != null && publishedVersion.getTemplateId().equals(args[0])
                                ? Optional.of(publishedVersion)
                                : Optional.empty();
                case "toString" -> "HealthCheckTemplateVersionRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckTemplateTaskRepositoryStub implements InvocationHandler {
        private List<HealthCheckTemplateTaskEntity> tasks = List.of();
        private String requestedTemplateVersionId;

        private HealthCheckTemplateTaskRepository createProxy() {
            return (HealthCheckTemplateTaskRepository) Proxy.newProxyInstance(
                    HealthCheckTemplateTaskRepository.class.getClassLoader(),
                    new Class<?>[]{HealthCheckTemplateTaskRepository.class},
                    this
            );
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findByTemplateVersionIdOrderBySortOrderAsc" -> {
                    requestedTemplateVersionId = (String) args[0];
                    yield tasks.stream()
                            .filter(task -> requestedTemplateVersionId.equals(task.getTemplateVersionId()))
                            .toList();
                }
                case "toString" -> "HealthCheckTemplateTaskRepositoryStub";
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
