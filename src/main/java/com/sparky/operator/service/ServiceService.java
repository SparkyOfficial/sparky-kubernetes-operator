package com.sparky.operator.service;

import com.sparky.operator.crd.SpringBootApp;
import com.sparky.operator.crd.SpringBootAppSpec;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * сервіс для роботи з кубернетес сервісами
 * service for working with kubernetes services
 * сервис для работы с кубернетес сервисами
 */
public class ServiceService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceService.class);
    
    public ServiceService(KubernetesClient client) {
        super(client);
    }
    
    /**
     * створює або оновлює сервіс
     * creates or updates service
     * создает или обновляет сервис
     */
    public void createOrUpdateService(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("створення/оновлення сервісу для {}", appName);
        logger.info("створення/оновлення сервісу для {}", appName);
        logger.info("создание/обновление сервиса для {}", appName);
        
        // отримуємо специфікацію
        // get specification
        // получаем спецификацию
        SpringBootAppSpec spec = app.getSpec();
        
        // створюємо порти сервісу
        // create service ports
        // создаем порты сервиса
        List<ServicePort> servicePorts = createServicePorts(spec);
        
        // створюємо сервіс
        // create service
        // создаем сервис
        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(getResourceName(appName, "service"))
                    .withNamespace(namespace)
                    .addToLabels("app", appName)
                    .addToLabels("sparky-operator-managed", "true")
                .endMetadata()
                .withNewSpec()
                    .withSelector(Map.of("app", appName))
                    .withPorts(servicePorts)
                    .withType("ClusterIP")
                .endSpec()
                .build();
        
        // створюємо або оновлюємо сервіс в кластері
        // create or update service in cluster
        // создаем или обновляем сервис в кластере
        client.services().inNamespace(namespace).createOrReplace(service);
        
        logger.info("сервіс створено для {}", appName);
        logger.info("сервіс створено для {}", appName);
        logger.info("сервис создан для {}", appName);
    }
    
    /**
     * видаляє сервіс
     * deletes service
     * удаляет сервис
     */
    public void deleteService(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("видалення сервісу для {}", appName);
        logger.info("видалення сервісу для {}", appName);
        logger.info("удаление сервиса для {}", appName);
        
        client.services()
                .inNamespace(namespace)
                .withName(getResourceName(appName, "service"))
                .delete();
        
        logger.info("сервіс видалено для {}", appName);
        logger.info("сервіс видалено для {}", appName);
        logger.info("сервис удален для {}", appName);
    }
    
    /**
     * створює порти сервісу зі специфікації
     * creates service ports from specification
     * создает порты сервиса из спецификации
     */
    private List<ServicePort> createServicePorts(SpringBootAppSpec spec) {
        List<ServicePort> ports = new ArrayList<>();
        
        if (spec.getPorts() == null || spec.getPorts().isEmpty()) {
            // стандартний порт для спринг бут аплікацій
            // default port for spring boot applications
            // стандартный порт для спринг бут приложений
            ports.add(new ServicePortBuilder()
                    .withName("http")
                    .withPort(8080)
                    .withTargetPort(new IntOrString(8080))
                    .build());
        } else {
            spec.getPorts().forEach((name, port) -> 
                ports.add(new ServicePortBuilder()
                        .withName(name)
                        .withPort(port)
                        .withTargetPort(new IntOrString(port))
                        .build())
            );
        }
        
        return ports;
    }
}