package com.sparky.operator.crd;

import java.util.List;

/**
 * статус спринг бут аплікації
 * status of spring boot application
 * статус спринг бут приложения
 */
public class SpringBootAppStatus {
    // стан аплікації
    // application state
    // состояние приложения
    private String phase;
    
    // повідомлення про стан
    // state message
    // сообщение о состоянии
    private String message;
    
    // створені ресурси
    // created resources
    // созданные ресурсы
    private List<String> createdResources;
    
    // ідентифікатор бази даних aws
    // aws database identifier
    // идентификатор базы данных aws
    private String databaseId;
    
    // ендпоінт бази даних
    // database endpoint
    // эндпоинт базы данных
    private String databaseEndpoint;
    
    // стандартні геттери та сеттери
    // standard getters and setters
    // стандартные геттеры и сеттеры
    
    public String getPhase() {
        return phase;
    }
    
    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<String> getCreatedResources() {
        return createdResources;
    }
    
    public void setCreatedResources(List<String> createdResources) {
        this.createdResources = createdResources;
    }
    
    public String getDatabaseId() {
        return databaseId;
    }
    
    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }
    
    public String getDatabaseEndpoint() {
        return databaseEndpoint;
    }
    
    public void setDatabaseEndpoint(String databaseEndpoint) {
        this.databaseEndpoint = databaseEndpoint;
    }
}