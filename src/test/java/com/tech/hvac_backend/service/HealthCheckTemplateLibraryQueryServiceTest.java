package com.tech.hvac_backend.service;

import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryResponse;
import com.tech.hvac_backend.dto.response.MaintenanceTemplateLibraryTaskResponse;
import com.tech.hvac_backend.entity.HealthCheckTemplateEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateTaskEntity;
import com.tech.hvac_backend.entity.HealthCheckTemplateVersionEntity;
import com.tech.hvac_backend.repository.HealthCheckTemplateRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateTaskRepository;
import com.tech.hvac_backend.repository.HealthCheckTemplateVersionRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckTemplateLibraryQueryServiceTest {

    private final HealthCheckTemplateRepositoryStub templateRepositoryStub =
            new HealthCheckTemplateRepositoryStub();
    private final HealthCheckTemplateVersionRepositoryStub versionRepositoryStub =
            new HealthCheckTemplateVersionRepositoryStub();
    private final HealthCheckTemplateTaskRepositoryStub taskRepositoryStub =
            new HealthCheckTemplateTaskRepositoryStub();
    private final HealthCheckTemplateLibraryQueryService service = new HealthCheckTemplateLibraryQueryService(
            templateRepositoryStub.createProxy(),
            versionRepositoryStub.createProxy(),
            taskRepositoryStub.createProxy()
    );

    @Test
    void getTemplateLibraryReturnsActivePublishedHealthCheckTemplate() {
        templateRepositoryStub.templates = List.of(template("health_check_template", true));
        versionRepositoryStub.version = version("health_check_template_version_1", "health_check_template", 1);
        taskRepositoryStub.tasks = List.of(
                task("task_1", "health_check_template_version_1", "comp_oil_pressure", "Compressor", "Check oil pressure", "bar", true),
                task("task_2", "health_check_template_version_1", "cond_approach", "Condenser", "Approach", "F", true)
        );

        MaintenanceTemplateLibraryResponse response = service.getTemplateLibrary();

        assertThat(response.getTemplates()).hasSize(1);
        assertThat(response.getTemplates().getFirst().getCode()).isEqualTo("HEALTH_CHECK");
        assertThat(response.getTemplates().getFirst().getTemplateType()).isEqualTo("HEALTH_CHECK");
        assertThat(response.getTemplates().getFirst().getVersionId()).isEqualTo("health_check_template_version_1");
        assertThat(response.getTemplates().getFirst().getVersionNumber()).isEqualTo(1);
        assertThat(response.getTemplates().getFirst().getTasks()).hasSize(2);

        MaintenanceTemplateLibraryTaskResponse firstTask = response.getTemplates().getFirst().getTasks().getFirst();
        assertThat(firstTask.getId()).isEqualTo("comp_oil_pressure");
        assertThat(firstTask.getCategory()).isEqualTo("Compressor");
        assertThat(firstTask.getTask()).isEqualTo("Check oil pressure");
        assertThat(firstTask.getUnit()).isEqualTo("bar");
        assertThat(firstTask.getRequired()).isTrue();
        assertThat(firstTask.getMeasurable()).isTrue();
        assertThat(firstTask.getPhotoRequiredOnFault()).isTrue();
        assertThat(firstTask.getPhotoRequiredOnAttention()).isTrue();
    }

    @Test
    void getTemplateLibrarySkipsInactiveTemplate() {
        templateRepositoryStub.templates = List.of(template("health_check_template", false));
        versionRepositoryStub.version = version("health_check_template_version_1", "health_check_template", 1);

        MaintenanceTemplateLibraryResponse response = service.getTemplateLibrary();

        assertThat(response.getTemplates()).isEmpty();
    }

    private HealthCheckTemplateEntity template(String id, Boolean active) {
        HealthCheckTemplateEntity template = new HealthCheckTemplateEntity();
        template.setId(id);
        template.setCode("HEALTH_CHECK");
        template.setName("Health Check");
        template.setIsActive(active);
        return template;
    }

    private HealthCheckTemplateVersionEntity version(String id, String templateId, Integer versionNumber) {
        HealthCheckTemplateVersionEntity version = new HealthCheckTemplateVersionEntity();
        version.setId(id);
        version.setTemplateId(templateId);
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
            String defaultUnit,
            Boolean measurable
    ) {
        HealthCheckTemplateTaskEntity task = new HealthCheckTemplateTaskEntity();
        task.setId(id);
        task.setTemplateVersionId(templateVersionId);
        task.setTaskCode(taskCode);
        task.setCategory(category);
        task.setTaskName(taskName);
        task.setDefaultUnit(defaultUnit);
        task.setIsRequired(true);
        task.setMeasurable(measurable);
        task.setPhotoRequiredOnFault(true);
        task.setPhotoRequiredOnAttention(true);
        return task;
    }

    private static class HealthCheckTemplateRepositoryStub implements InvocationHandler {
        private List<HealthCheckTemplateEntity> templates = List.of();

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
                case "findAll" -> templates;
                case "toString" -> "HealthCheckTemplateRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckTemplateVersionRepositoryStub implements InvocationHandler {
        private HealthCheckTemplateVersionEntity version;

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
                        version != null && version.getTemplateId().equals(args[0])
                                ? Optional.of(version)
                                : Optional.empty();
                case "toString" -> "HealthCheckTemplateVersionRepositoryStub";
                default -> defaultValue(method.getReturnType());
            };
        }
    }

    private static class HealthCheckTemplateTaskRepositoryStub implements InvocationHandler {
        private List<HealthCheckTemplateTaskEntity> tasks = List.of();

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
                case "findByTemplateVersionIdOrderBySortOrderAsc" -> tasks.stream()
                        .filter(task -> task.getTemplateVersionId().equals(args[0]))
                        .toList();
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
