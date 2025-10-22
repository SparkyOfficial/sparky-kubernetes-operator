package com.sparky.operator.service;

import com.sparky.operator.crd.SpringBootApp;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * сервіс для роботи з конфігураційними ресурсами
 * service for working with configuration resources
 * сервис для работы с конфигурационными ресурсами
 */
public class ConfigService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);
    
    public ConfigService(KubernetesClient client) {
        super(client);
    }
    
    /**
     * створює або оновлює конфігураційні ресурси
     * creates or updates configuration resources
     * создает или обновляет конфигурационные ресурсы
     */
    public void createOrUpdateConfigResources(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("створення/оновлення конфігураційних ресурсів для {}", appName);
        logger.info("створення/оновлення конфігураційних ресурсів для {}", appName);
        logger.info("создание/обновление конфигурационных ресурсов для {}", appName);
        
        // створюємо конфігмап
        // create configmap
        // создаем конфигмап
        createConfigMap(app);
        
        // створюємо секрет
        // create secret
        // создаем секрет
        createSecret(app);
        
        logger.info("конфігураційні ресурси створено для {}", appName);
        logger.info("конфігураційні ресурси створено для {}", appName);
        logger.info("конфигурационные ресурсы созданы для {}", appName);
    }
    
    /**
     * видаляє конфігураційні ресурси
     * deletes configuration resources
     * удаляет конфигурационные ресурсы
     */
    public void deleteConfigResources(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("видалення конфігураційних ресурсів для {}", appName);
        logger.info("видалення конфігураційних ресурсів для {}", appName);
        logger.info("удаление конфигурационных ресурсов для {}", appName);
        
        // видаляємо конфігмап
        // delete configmap
        // удаляем конфигмап
        client.configMaps()
                .inNamespace(namespace)
                .withName(getResourceName(appName, "config"))
                .delete();
        
        // видаляємо секрет
        // delete secret
        // удаляем секрет
        client.secrets()
                .inNamespace(namespace)
                .withName(getResourceName(appName, "secret"))
                .delete();
        
        logger.info("конфігураційні ресурси видалено для {}", appName);
        logger.info("конфігураційні ресурси видалено для {}", appName);
        logger.info("конфигурационные ресурсы удалены для {}", appName);
    }
    
    /**
     * створює конфігмап з параметрами аплікації
     * creates configmap with application parameters
     * создает конфигмап с параметрами приложения
     */
    private void createConfigMap(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        // створюємо дані для конфігмапу
        // create data for configmap
        // создаем данные для конфигмапа
        Map<String, String> configData = new HashMap<>();
        configData.put("application.properties", createApplicationProperties(app));
        
        // створюємо конфігмап
        // create configmap
        // создаем конфигмап
        ConfigMap configMap = new ConfigMapBuilder()
                .withNewMetadata()
                    .withName(getResourceName(appName, "config"))
                    .withNamespace(namespace)
                    .addToLabels("app", appName)
                    .addToLabels("sparky-operator-managed", "true")
                .endMetadata()
                .withData(configData)
                .build();
        
        // створюємо або оновлюємо конфігмап в кластері
        // create or update configmap in cluster
        // создаем или обновляем конфигмап в кластере
        client.configMaps().inNamespace(namespace).createOrReplace(configMap);
    }
    
    /**
     * створює секрет з кредами бази даних
     * creates secret with database credentials
     * создает секрет с кредами базы данных
     */
    private void createSecret(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        // створюємо дані для секрету
        // create data for secret
        // создаем данные для секрета
        Map<String, String> secretData = new HashMap<>();
        secretData.put("db-username", Base64.getEncoder().encodeToString("admin".getBytes()));
        secretData.put("db-password", Base64.getEncoder().encodeToString("TempPassword123!".getBytes()));
        
        // створюємо секрет
        // create secret
        // создаем секрет
        Secret secret = new SecretBuilder()
                .withNewMetadata()
                    .withName(getResourceName(appName, "secret"))
                    .withNamespace(namespace)
                    .addToLabels("app", appName)
                    .addToLabels("sparky-operator-managed", "true")
                .endMetadata()
                .withData(secretData)
                .withType("Opaque")
                .build();
        
        // створюємо або оновлюємо секрет в кластері
        // create or update secret in cluster
        // создаем или обновляем секрет в кластере
        client.secrets().inNamespace(namespace).createOrReplace(secret);
    }
    
    /**
     * створює властивості додатку
     * creates application properties
     * создает свойства приложения
     */
    private String createApplicationProperties(SpringBootApp app) {
        StringBuilder props = new StringBuilder();
        props.append("# конфігурація спринг бут аплікації\n");
        props.append("# spring boot application configuration\n");
        props.append("# конфигурация спринг бут приложения\n");
        props.append("spring.application.name=").append(app.getMetadata().getName()).append("\n");
        
        // якщо ввімкнено базу даних
        // if database is enabled
        // если включена база данных
        if (app.getSpec().getDatabase() != null && app.getSpec().getDatabase().isEnabled()) {
            props.append("\n# налаштування бази даних\n");
            props.append("# database configuration\n");
            props.append("# настройки базы данных\n");
            props.append("spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:").append(app.getMetadata().getName()).append("}\n");
            props.append("spring.datasource.username=${DB_USERNAME:admin}\n");
            props.append("spring.datasource.password=${DB_PASSWORD:TempPassword123!}\n");
        }
        
        return props.toString();
    }
}