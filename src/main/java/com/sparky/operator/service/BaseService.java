package com.sparky.operator.service;

import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * базовий сервіс для роботи з ресурсами кубернетеса
 * base service for working with kubernetes resources
 * базовый сервис для работы с ресурсами кубернетеса
 */
public abstract class BaseService {
    protected final KubernetesClient client;
    
    public BaseService(KubernetesClient client) {
        this.client = client;
    }
    
    /**
     * генерує ім'я ресурсу на основі імені аплікації
     * generates resource name based on application name
     * генерирует имя ресурса на основе имени приложения
     */
    protected String getResourceName(String appName, String resourceType) {
        return appName + "-" + resourceType;
    }
}