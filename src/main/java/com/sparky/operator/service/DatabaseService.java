package com.sparky.operator.service;

import com.sparky.operator.crd.SpringBootApp;
import com.sparky.operator.crd.SpringBootAppSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.*;

import java.util.UUID;

/**
 * сервіс для роботи з aws rds базами даних
 * service for working with aws rds databases
 * сервис для работы с aws rds базами данных
 */
public class DatabaseService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    
    private final RdsClient rdsClient;
    
    public DatabaseService(KubernetesClient client) {
        super(client);
        // ініціалізуємо aws rds клієнт
        // initialize aws rds client
        // инициализируем aws rds клиент
        this.rdsClient = RdsClient.builder()
                .region(Region.US_EAST_1) // TODO: зробити конфігурованим
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
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
        // TODO: реалізувати оновлення статусу кастомного ресурсу
        // TODO: implement custom resource status update
        // TODO: реализовать обновление статуса кастомного ресурса
        logger.info("оновлення статусу аплікації: dbId={}, endpoint={}", databaseId, endpoint);
        logger.info("оновлення статусу аплікації: dbId={}, endpoint={}", databaseId, endpoint);
        logger.info("обновление статуса приложения: dbId={}, endpoint={}", databaseId, endpoint);
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