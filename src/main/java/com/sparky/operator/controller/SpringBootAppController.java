package com.sparky.operator.controller;

import com.sparky.operator.crd.SpringBootApp;
import com.sparky.operator.crd.SpringBootAppSpec;
import com.sparky.operator.service.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * контролер для обробки спринг бут аплікацій
 * controller for processing spring boot applications
 * контроллер для обработки спринг бут приложений
 */
public class SpringBootAppController {
    private static final Logger logger = LoggerFactory.getLogger(SpringBootAppController.class);
    
    private final KubernetesClient client;
    private final DeploymentService deploymentService;
    private final ServiceService serviceService;
    private final DatabaseService databaseService;
    private final ConfigService configService;
    private final MonitoringService monitoringService;
    
    public SpringBootAppController(KubernetesClient client) {
        this.client = client;
        this.deploymentService = new DeploymentService(client);
        this.serviceService = new ServiceService(client);
        this.databaseService = new DatabaseService(client);
        this.configService = new ConfigService(client);
        this.monitoringService = new MonitoringService(client);
    }
    
    /**
     * основна логіка примирення
     * main reconciliation logic
     * основная логика примирения
     */
    public void reconcile(SpringBootApp app) {
        try {
            logger.info("починаємо примирення для {}", app.getMetadata().getName());
            logger.info("починаємо примирення для {}", app.getMetadata().getName());
            logger.info("начинаем примирение для {}", app.getMetadata().getName());
            
            // отримуємо специфікацію
            // get specification
            // получаем спецификацию
            SpringBootAppSpec spec = app.getSpec();
            
            // створюємо або оновлюємо деплоймент
            // create or update deployment
            // создаем или обновляем деплоймент
            deploymentService.createOrUpdateDeployment(app);
            
            // створюємо або оновлюємо сервіс
            // create or update service
            // создаем или обновляем сервис
            serviceService.createOrUpdateService(app);
            
            // якщо ввімкнено базу даних
            // if database is enabled
            // если включена база данных
            if (spec.getDatabase() != null && spec.getDatabase().isEnabled()) {
                // створюємо або отримуємо базу даних aws
                // create or get aws database
                // создаем или получаем базу данных aws
                databaseService.createOrUpdateDatabase(app);
            }
            
            // створюємо конфігураційні ресурси
            // create configuration resources
            // создаем конфигурационные ресурсы
            configService.createOrUpdateConfigResources(app);
            
            // якщо ввімкнено моніторинг
            // if monitoring is enabled
            // если включен мониторинг
            if (spec.getMonitoring() != null && spec.getMonitoring().isEnabled()) {
                // налаштовуємо моніторинг
                // configure monitoring
                // настраиваем мониторинг
                monitoringService.setupMonitoring(app);
            }
            
            logger.info("примирення завершено для {}", app.getMetadata().getName());
            logger.info("примирення завершено для {}", app.getMetadata().getName());
            logger.info("примирение завершено для {}", app.getMetadata().getName());
        } catch (Exception e) {
            logger.error("помилка під час примирення " + app.getMetadata().getName(), e);
            logger.error("помилка під час примирення " + app.getMetadata().getName(), e);
            logger.error("ошибка во время примирения " + app.getMetadata().getName(), e);
        }
    }
    
    /**
     * видалення ресурсів
     * resource deletion
     * удаление ресурсов
     */
    public void delete(SpringBootApp app) {
        try {
            logger.info("видалення ресурсів для {}", app.getMetadata().getName());
            logger.info("видалення ресурсів для {}", app.getMetadata().getName());
            logger.info("удаление ресурсов для {}", app.getMetadata().getName());
            
            // видаляємо всі створені ресурси в зворотньому порядку
            // delete all created resources in reverse order
            // удаляем все созданные ресурсы в обратном порядке
            
            // видаляємо моніторинг
            // delete monitoring
            // удаляем мониторинг
            if (app.getSpec().getMonitoring() != null && app.getSpec().getMonitoring().isEnabled()) {
                monitoringService.deleteMonitoring(app);
            }
            
            // видаляємо конфігураційні ресурси
            // delete configuration resources
            // удаляем конфигурационные ресурсы
            configService.deleteConfigResources(app);
            
            // видаляємо базу даних
            // delete database
            // удаляем базу данных
            if (app.getSpec().getDatabase() != null && app.getSpec().getDatabase().isEnabled()) {
                databaseService.deleteDatabase(app);
            }
            
            // видаляємо сервіс
            // delete service
            // удаляем сервис
            serviceService.deleteService(app);
            
            // видаляємо деплоймент
            // delete deployment
            // удаляем деплоймент
            deploymentService.deleteDeployment(app);
            
            logger.info("видалення завершено для {}", app.getMetadata().getName());
            logger.info("видалення завершено для {}", app.getMetadata().getName());
            logger.info("удаление завершено для {}", app.getMetadata().getName());
        } catch (Exception e) {
            logger.error("помилка під час видалення " + app.getMetadata().getName(), e);
            logger.error("помилка під час видалення " + app.getMetadata().getName(), e);
            logger.error("ошибка во время удаления " + app.getMetadata().getName(), e);
        }
    }
}