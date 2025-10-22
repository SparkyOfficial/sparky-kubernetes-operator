package com.sparky.operator.crd;

import java.util.Map;

/**
 * специфікація для спринг бут аплікації
 * specification for spring boot application
 * спецификация для спринг бут приложения
 */
public class SpringBootAppSpec {
    // образ докер контейнера
    // docker container image
    // образ докер контейнера
    private String image;
    
    // порти для сервісу
    // ports for service
    // порты для сервиса
    private Map<String, Integer> ports;
    
    // репліки деплоймента
    // deployment replicas
    // реплики деплоймента
    private int replicas = 1;
    
    // ліміти ресурсів
    // resource limits
    // лимиты ресурсов
    private ResourceLimits resources;
    
    // налаштування бази даних
    // database configuration
    // настройки базы данных
    private DatabaseConfig database;
    
    // налаштування моніторингу
    // monitoring configuration
    // настройки мониторинга
    private MonitoringConfig monitoring;
    
    // геттери та сеттери
    // getters and setters
    // геттеры и сеттеры
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Map<String, Integer> getPorts() {
        return ports;
    }
    
    public void setPorts(Map<String, Integer> ports) {
        this.ports = ports;
    }
    
    public int getReplicas() {
        return replicas;
    }
    
    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
    
    public ResourceLimits getResources() {
        return resources;
    }
    
    public void setResources(ResourceLimits resources) {
        this.resources = resources;
    }
    
    public DatabaseConfig getDatabase() {
        return database;
    }
    
    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }
    
    public MonitoringConfig getMonitoring() {
        return monitoring;
    }
    
    public void setMonitoring(MonitoringConfig monitoring) {
        this.monitoring = monitoring;
    }
    
    /**
     * клас для лімітів ресурсів
     * class for resource limits
     * класс для лимитов ресурсов
     */
    public static class ResourceLimits {
        private String cpuRequest;
        private String memoryRequest;
        private String cpuLimit;
        private String memoryLimit;
        
        // стандартні геттери та сеттери
        // standard getters and setters
        // стандартные геттеры и сеттеры
        
        public String getCpuRequest() {
            return cpuRequest;
        }
        
        public void setCpuRequest(String cpuRequest) {
            this.cpuRequest = cpuRequest;
        }
        
        public String getMemoryRequest() {
            return memoryRequest;
        }
        
        public void setMemoryRequest(String memoryRequest) {
            this.memoryRequest = memoryRequest;
        }
        
        public String getCpuLimit() {
            return cpuLimit;
        }
        
        public void setCpuLimit(String cpuLimit) {
            this.cpuLimit = cpuLimit;
        }
        
        public String getMemoryLimit() {
            return memoryLimit;
        }
        
        public void setMemoryLimit(String memoryLimit) {
            this.memoryLimit = memoryLimit;
        }
    }
    
    /**
     * клас для конфігурації бази даних
     * class for database configuration
     * класс для конфигурации базы данных
     */
    public static class DatabaseConfig {
        private boolean enabled = false;
        private String engine = "aurora-mysql";
        private String instanceClass = "db.t3.medium";
        private int allocatedStorage = 20;
        private String masterUsername = "admin";
        
        // стандартні геттери та сеттери
        // standard getters and setters
        // стандартные геттеры и сеттеры
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getEngine() {
            return engine;
        }
        
        public void setEngine(String engine) {
            this.engine = engine;
        }
        
        public String getInstanceClass() {
            return instanceClass;
        }
        
        public void setInstanceClass(String instanceClass) {
            this.instanceClass = instanceClass;
        }
        
        public int getAllocatedStorage() {
            return allocatedStorage;
        }
        
        public void setAllocatedStorage(int allocatedStorage) {
            this.allocatedStorage = allocatedStorage;
        }
        
        public String getMasterUsername() {
            return masterUsername;
        }
        
        public void setMasterUsername(String masterUsername) {
            this.masterUsername = masterUsername;
        }
    }
    
    /**
     * клас для конфігурації моніторингу
     * class for monitoring configuration
     * класс для конфигурации мониторинга
     */
    public static class MonitoringConfig {
        private boolean enabled = false;
        private String grafanaDashboardTemplate = "spring-boot-dashboard";
        
        // стандартні геттери та сеттери
        // standard getters and setters
        // стандартные геттеры и сеттеры
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getGrafanaDashboardTemplate() {
            return grafanaDashboardTemplate;
        }
        
        public void setGrafanaDashboardTemplate(String grafanaDashboardTemplate) {
            this.grafanaDashboardTemplate = grafanaDashboardTemplate;
        }
    }
}