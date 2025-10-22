package com.sparky.operator;

import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * основний клас оператора - тут вся магія відбувається
 * main operator class - here all the magic happens
 * основной класс оператора - здесь вся магия происходит
 */
public class SparkyOperator {
    private static final Logger logger = LoggerFactory.getLogger(SparkyOperator.class);
    
    private final KubernetesClient client;
    private final SharedInformerFactory informerFactory;
    
    public SparkyOperator() {
        // ініціалізуємо клієнт кубернетеса
        // initialize kubernetes client
        // инициализируем клиент кубернетеса
        this.client = new DefaultKubernetesClient();
        this.informerFactory = client.informers();
    }
    
    /**
     * запуск оператора
     * start the operator
     * запуск оператора
     */
    public void run() {
        logger.info("запуск sparky оператора..."); // starting sparky operator...
        logger.info("запуск оператора..."); // starting operator...
        logger.info("запуск оператора..."); // starting operator...
        
        // реєструємо інформери для наших кастомних ресурсів
        // register informers for our custom resources
        // регистрируем информеры для наших кастомных ресурсов
        // TODO: реалізувати реєстрацію інформерів для CRD
        // TODO: implement registration of informers for CRD
        // TODO: реализовать регистрацию информеров для CRD
        
        // запускаємо інформери
        // start informers
        // запускаем информеры
        informerFactory.startAllRegisteredInformers();
        
        // тримаємо програму активною
        // keep the program active
        // держим программу активной
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.error("оператор перервано", e); // operator interrupted
            logger.error("оператор перервано", e); // operator interrupted
            logger.error("оператор прерван", e); // operator interrupted
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * зупинка оператора
     * stop the operator
     * остановка оператора
     */
    public void stop() {
        logger.info("зупинка оператора..."); // stopping operator...
        logger.info("зупинка оператора..."); // stopping operator...
        logger.info("остановка оператора..."); // stopping operator...
        
        informerFactory.stopAllRegisteredInformers();
        client.close();
    }
    
    public static void main(String[] args) {
        SparkyOperator operator = new SparkyOperator();
        
        // реєструємо обробник вимкнення
        // register shutdown handler
        // регистрируем обработчик выключения
        Runtime.getRuntime().addShutdownHook(new Thread(operator::stop));
        
        try {
            operator.run();
        } catch (Exception e) {
            logger.error("критична помилка в операторі", e); // critical error in operator
            logger.error("критична помилка в операторі", e); // critical error in operator
            logger.error("критическая ошибка в операторе", e); // critical error in operator
        }
    }
}