package com.sparky.operator.service;

import com.sparky.operator.crd.SpringBootApp;
import com.sparky.operator.crd.SpringBootAppSpec;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * сервіс для роботи з деплойментами
 * service for working with deployments
 * сервис для работы с деплойментами
 */
public class DeploymentService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(DeploymentService.class);
    
    public DeploymentService(KubernetesClient client) {
        super(client);
    }
    
    /**
     * створює або оновлює деплоймент
     * creates or updates deployment
     * создает или обновляет деплоймент
     */
    public void createOrUpdateDeployment(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("створення/оновлення деплойменту для {}", appName);
        logger.info("створення/оновлення деплойменту для {}", appName);
        logger.info("создание/обновление деплоймента для {}", appName);
        
        // отримуємо специфікацію
        // get specification
        // получаем спецификацию
        SpringBootAppSpec spec = app.getSpec();
        
        // створюємо деплоймент
        // create deployment
        // создаем деплоймент
        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                    .withName(getResourceName(appName, "app"))
                    .withNamespace(namespace)
                    .addToLabels("app", appName)
                    .addToLabels("sparky-operator-managed", "true")
                .endMetadata()
                .withNewSpec()
                    .withReplicas(spec.getReplicas())
                    .withNewSelector()
                        .addToMatchLabels("app", appName)
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", appName)
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("app")
                                .withImage(spec.getImage())
                                .withPorts(createContainerPorts(spec))
                                .withResources(createResourceRequirements(spec))
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();
        
        // створюємо або оновлюємо деплоймент в кластері
        // create or update deployment in cluster
        // создаем или обновляем деплоймент в кластере
        client.apps().deployments().inNamespace(namespace).createOrReplace(deployment);
        
        logger.info("деплоймент створено для {}", appName);
        logger.info("деплоймент створено для {}", appName);
        logger.info("деплоймент создан для {}", appName);
    }
    
    /**
     * видаляє деплоймент
     * deletes deployment
     * удаляет деплоймент
     */
    public void deleteDeployment(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("видалення деплойменту для {}", appName);
        logger.info("видалення деплойменту для {}", appName);
        logger.info("удаление деплоймента для {}", appName);
        
        client.apps().deployments()
                .inNamespace(namespace)
                .withName(getResourceName(appName, "app"))
                .delete();
        
        logger.info("деплоймент видалено для {}", appName);
        logger.info("деплоймент видалено для {}", appName);
        logger.info("деплоймент удален для {}", appName);
    }
    
    /**
     * створює порти контейнера зі специфікації
     * creates container ports from specification
     * создает порты контейнера из спецификации
     */
    private ContainerPort[] createContainerPorts(SpringBootAppSpec spec) {
        if (spec.getPorts() == null || spec.getPorts().isEmpty()) {
            // стандартний порт для спринг бут аплікацій
            // default port for spring boot applications
            // стандартный порт для спринг бут приложений
            return new ContainerPort[] {
                new ContainerPortBuilder()
                    .withContainerPort(8080)
                    .withName("http")
                    .build()
            };
        }
        
        return spec.getPorts().entrySet().stream()
                .map(entry -> new ContainerPortBuilder()
                        .withName(entry.getKey())
                        .withContainerPort(entry.getValue())
                        .build())
                .toArray(ContainerPort[]::new);
    }
    
    /**
     * створює вимоги до ресурсів зі специфікації
     * creates resource requirements from specification
     * создает требования к ресурсам из спецификации
     */
    private ResourceRequirements createResourceRequirements(SpringBootAppSpec spec) {
        if (spec.getResources() == null) {
            return new ResourceRequirementsBuilder().build();
        }
        
        Map<String, Quantity> requests = new HashMap<>();
        Map<String, Quantity> limits = new HashMap<>();
        
        SpringBootAppSpec.ResourceLimits resourceLimits = spec.getResources();
        
        // додаємо запити ресурсів
        // add resource requests
        // добавляем запросы ресурсов
        if (resourceLimits.getCpuRequest() != null) {
            requests.put("cpu", new Quantity(resourceLimits.getCpuRequest()));
        }
        
        if (resourceLimits.getMemoryRequest() != null) {
            requests.put("memory", new Quantity(resourceLimits.getMemoryRequest()));
        }
        
        // додаємо ліміти ресурсів
        // add resource limits
        // добавляем лимиты ресурсов
        if (resourceLimits.getCpuLimit() != null) {
            limits.put("cpu", new Quantity(resourceLimits.getCpuLimit()));
        }
        
        if (resourceLimits.getMemoryLimit() != null) {
            limits.put("memory", new Quantity(resourceLimits.getMemoryLimit()));
        }
        
        return new ResourceRequirementsBuilder()
                .withRequests(requests)
                .withLimits(limits)
                .build();
    }
}