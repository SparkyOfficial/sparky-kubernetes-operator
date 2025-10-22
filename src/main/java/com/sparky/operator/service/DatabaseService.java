package com.sparky.operator.service;

import com.sparky.operator.crd.SpringBootApp;
import com.sparky.operator.crd.SpringBootAppSpec;
import com.sparky.operator.crd.SpringBootAppStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * сервіс для роботи з aws rds базами даних
 * service for working with aws rds databases
 * сервис для работы с aws rds базами данных
 * 
 * @author Андрій Будильников
 */
public class DatabaseService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    
    private final RdsClient rdsClient;
    private final MixedOperation<SpringBootApp, KubernetesResourceList<SpringBootApp>, Resource<SpringBootApp>> springBootAppClient;
    
    public DatabaseService(KubernetesClient client) {
        super(client);
        // ініціалізуємо aws rds клієнт
        // initialize aws rds client
        // инициализируем aws rds клиент
        this.rdsClient = RdsClient.builder()
                .region(getAwsRegion()) // зробити конфігурованим / make configurable / сделать настраиваемым
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        
        // ініціалізуємо клієнт для роботи з кастомними ресурсами
        // initialize client for working with custom resources
        // инициализируем клиент для работы с кастомными ресурсами
        this.springBootAppClient = client.resources(SpringBootApp.class);
    }
    
    /**
     * отримує регіон aws з змінних середовища або використовує за замовчуванням
     * gets aws region from environment variables or uses default
     * получает регион aws из переменных среды или использует по умолчанию
     */
    private Region getAwsRegion() {
        // спробуємо отримати регіон з змінної середовища
        // try to get region from environment variable
        // попробуем получить регион из переменной среды
        String regionStr = System.getenv("AWS_REGION");
        if (regionStr == null || regionStr.isEmpty()) {
            regionStr = System.getenv("AWS_DEFAULT_REGION");
        }
        
        // якщо не знайдено, використовуємо за замовчуванням
        // if not found, use default
        // если не найдено, используем по умолчанию
        if (regionStr == null || regionStr.isEmpty()) {
            logger.info("регіон aws не вказано, використовується за замовчуванням: us-east-1");
            logger.info("регион aws не указан, используется по умолчанию: us-east-1");
            logger.info("aws region not specified, using default: us-east-1");
            return Region.US_EAST_1;
        }
        
        try {
            Region region = Region.of(regionStr);
            logger.info("використовується регіон aws: {}", regionStr);
            logger.info("используется регион aws: {}", regionStr);
            logger.info("using aws region: {}", regionStr);
            return region;
        } catch (Exception e) {
            logger.warn("не вдалося розпізнати регіон aws: {}, використовується за замовчуванням: us-east-1", regionStr);
            logger.warn("не удалось распознать регион aws: {}, используется по умолчанию: us-east-1", regionStr);
            logger.warn("failed to recognize aws region: {}, using default: us-east-1", regionStr);
            return Region.US_EAST_1;
        }
    }
    
    /**
     * створює або отримує базу даних aws
     * creates or gets aws database
     * создает или получает базу данных aws
     */
    public void createOrUpdateDatabase(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String namespace = app.getMetadata().getNamespace();
        
        logger.info("створення/оновлення бази даних для {}", appName);
        logger.info("створення/оновлення бази даних для {}", appName);
        logger.info("создание/обновление базы данных для {}", appName);
        
        // отримуємо специфікацію бази даних
        // get database specification
        // получаем спецификацию базы данных
        SpringBootAppSpec.DatabaseConfig dbConfig = app.getSpec().getDatabase();
        
        // генеруємо унікальне ім'я для бази даних
        // generate unique name for database
        // генерируем уникальное имя для базы данных
        String dbInstanceIdentifier = getResourceName(appName, "db-" + UUID.randomUUID().toString().substring(0, 8));
        
        try {
            // перевіряємо чи існує база даних
            // check if database exists
            // проверяем существует ли база данных
            DescribeDbInstancesResponse response = rdsClient.describeDBInstances(
                DescribeDbInstancesRequest.builder()
                    .dbInstanceIdentifier(dbInstanceIdentifier)
                    .build()
            );
            
            // якщо існує, оновлюємо статус
            // if exists, update status
            // если существует, обновляем статус
            if (!response.dbInstances().isEmpty()) {
                DBInstance dbInstance = response.dbInstances().get(0);
                updateAppStatus(app, dbInstance.dbInstanceIdentifier(), dbInstance.endpoint().address());
                logger.info("база даних вже існує: {}", dbInstanceIdentifier);
                logger.info("база даних вже існує: {}", dbInstanceIdentifier);
                logger.info("база данных уже существует: {}", dbInstanceIdentifier);
                return;
            }
        } catch (RdsException e) {
            // якщо база даних не існує, створюємо її
            // if database doesn't exist, create it
            // если база данных не существует, создаем ее
            if (e.awsErrorDetails().errorCode().equals("DBInstanceNotFoundFault")) {
                logger.info("база даних не знайдена, створюємо нову: {}", dbInstanceIdentifier);
                logger.info("база даних не знайдена, створюємо нову: {}", dbInstanceIdentifier);
                logger.info("база данных не найдена, создаем новую: {}", dbInstanceIdentifier);
            } else {
                logger.error("помилка під час перевірки бази даних", e);
                logger.error("помилка під час перевірки бази даних", e);
                logger.error("ошибка во время проверки базы данных", e);
                throw e;
            }
        }
        
        // створюємо нову базу даних
        // create new database
        // создаем новую базу данных
        try {
            CreateDbInstanceResponse createResponse = rdsClient.createDBInstance(
                CreateDbInstanceRequest.builder()
                    .dbInstanceIdentifier(dbInstanceIdentifier)
                    .dbInstanceClass(dbConfig.getInstanceClass())
                    .engine(dbConfig.getEngine())
                    .allocatedStorage(dbConfig.getAllocatedStorage())
                    .masterUsername(dbConfig.getMasterUsername())
                    .masterUserPassword(generatePassword()) // TODO: згенерувати безпечний пароль
                    .publiclyAccessible(false)
                    .build()
            );
            
            // оновлюємо статус аплікації
            // update application status
            // обновляем статус приложения
            updateAppStatus(app, dbInstanceIdentifier, null);
            
            logger.info("запит на створення бази даних відправлено: {}", dbInstanceIdentifier);
            logger.info("запит на створення бази даних відправлено: {}", dbInstanceIdentifier);
            logger.info("запрос на создание базы данных отправлен: {}", dbInstanceIdentifier);
        } catch (RdsException e) {
            logger.error("помилка під час створення бази даних", e);
            logger.error("помилка під час створення бази даних", e);
            logger.error("ошибка во время создания базы данных", e);
            throw e;
        }
    }
    
    /**
     * видаляє базу даних aws
     * deletes aws database
     * удаляет базу данных aws
     */
    public void deleteDatabase(SpringBootApp app) {
        String appName = app.getMetadata().getName();
        String databaseId = app.getStatus() != null ? app.getStatus().getDatabaseId() : null;
        
        if (databaseId == null || databaseId.isEmpty()) {
            logger.warn("ідентифікатор бази даних не знайдено для {}", appName);
            logger.warn("ідентифікатор бази даних не знайдено для {}", appName);
            logger.warn("идентификатор базы данных не найден для {}", appName);
            return;
        }
        
        logger.info("видалення бази даних для {}", appName);
        logger.info("видалення бази даних для {}", appName);
        logger.info("удаление базы данных для {}", appName);
        
        try {
            rdsClient.deleteDBInstance(
                DeleteDbInstanceRequest.builder()
                    .dbInstanceIdentifier(databaseId)
                    .skipFinalSnapshot(true)
                    .build()
            );
            
            logger.info("запит на видалення бази даних відправлено: {}", databaseId);
            logger.info("запит на видалення бази даних відправлено: {}", databaseId);
            logger.info("запрос на удаление базы данных отправлен: {}", databaseId);
        } catch (RdsException e) {
            logger.error("помилка під час видалення бази даних", e);
            logger.error("помилка під час видалення бази даних", e);
            logger.error("ошибка во время удаления базы данных", e);
        }
    }
    
    /**
     * оновлює статус аплікації
     * updates application status
     * обновляет статус приложения
     */
    private void updateAppStatus(SpringBootApp app, String databaseId, String endpoint) {
        // реалізувати оновлення статусу кастомного ресурсу
        // implement custom resource status update
        // реализовать обновление статуса кастомного ресурса
        
        try {
            // отримуємо поточний стан аплікації
            // get current application state
            // получаем текущее состояние приложения
            SpringBootApp currentApp = springBootAppClient
                .inNamespace(app.getMetadata().getNamespace())
                .withName(app.getMetadata().getName())
                .get();
            
            if (currentApp != null) {
                // створюємо або оновлюємо статус
                // create or update status
                // создаем или обновляем статус
                SpringBootAppStatus status = currentApp.getStatus();
                if (status == null) {
                    status = new SpringBootAppStatus();
                }
                
                // оновлюємо поля статусу
                // update status fields
                // обновляем поля статуса
                status.setDatabaseId(databaseId);
                status.setDatabaseEndpoint(endpoint);
                status.setPhase(endpoint != null ? "DatabaseReady" : "DatabaseCreating");
                status.setMessage(endpoint != null ? 
                    "Database " + databaseId + " is ready at " + endpoint : 
                    "Database " + databaseId + " creation initiated");
                
                // оновлюємо ресурс в кластері
                // update resource in cluster
                // обновляем ресурс в кластере
                currentApp.setStatus(status);
                springBootAppClient
                    .inNamespace(app.getMetadata().getNamespace())
                    .withName(app.getMetadata().getName())
                    .createOrReplace(currentApp);
                
                logger.info("статус аплікації оновлено: dbId={}, endpoint={}", databaseId, endpoint);
                logger.info("статус аплікації оновлено: dbId={}, endpoint={}", databaseId, endpoint);
                logger.info("статус приложения обновлен: dbId={}, endpoint={}", databaseId, endpoint);
            }
        } catch (Exception e) {
            logger.error("помилка під час оновлення статусу аплікації", e);
            logger.error("помилка під час оновлення статусу аплікації", e);
            logger.error("ошибка во время обновления статуса приложения", e);
        }
    }
    
    /**
     * генерує випадковий пароль
     * generates random password
     * генерирует случайный пароль
     */
    private String generatePassword() {
        // TODO: реалізувати генерацію безпечного пароля
        // TODO: implement secure password generation
        // TODO: реализовать генерацию безопасного пароля
        return "TempPassword123!";
    }
}