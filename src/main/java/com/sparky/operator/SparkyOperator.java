package com.sparky.operator;

import com.sparky.operator.controller.SpringBootAppController;
import com.sparky.operator.crd.SpringBootApp;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.fabric8.kubernetes.api.model.HasMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * основний клас оператора - тут вся магія відбувається
 * main operator class - here all the magic happens
 * основной класс оператора - здесь вся магия происходит
 * 
 * @author Андрій Будильников
 */
public class SparkyOperator {
    private static final Logger logger = LoggerFactory.getLogger(SparkyOperator.class);
    
    private final KubernetesClient client;
    private final SharedInformerFactory informerFactory;
    private final SpringBootAppController controller;
    
    public SparkyOperator() {
        // ініціалізуємо клієнт кубернетеса
        // initialize kubernetes client
        // инициализируем клиент кубернетеса
        this.client = new DefaultKubernetesClient();
        this.informerFactory = client.informers();
        this.controller = new SpringBootAppController(client);
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
        registerInformer();
        
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
     * реєструє інформер для SpringBootApp
     * registers informer for SpringBootApp
     * регистрирует информер для SpringBootApp
     */
    private void registerInformer() {
        // отримуємо клієнт для нашого кастомного ресурсу
        // get client for our custom resource
        // получаем клиент для нашего кастомного ресурса
        MixedOperation<SpringBootApp, KubernetesResourceList<SpringBootApp>, Resource<SpringBootApp>> springBootAppClient = 
            client.resources(SpringBootApp.class);
        
        // створюємо інформер
        // create informer
        // создаем информер
        SharedIndexInformer<SpringBootApp> informer = informerFactory.sharedIndexInformerFor(
            springBootAppClient,
            SpringBootApp.class,
            30 * 1000L // резинхронізація кожні 30 секунд / resync every 30 seconds / ресинхронизация каждые 30 секунд
        );
        
        // додаємо обробники подій
        // add event handlers
        // добавляем обработчики событий
        informer.addEventHandler(new SparkyOperatorEventHandler(controller));
        
        logger.info("інформер для SpringBootApp зареєстровано"); // informer for SpringBootApp registered
        logger.info("інформер для SpringBootApp зареєстровано"); // informer for SpringBootApp registered
        logger.info("инformer для SpringBootApp зарегистрирован"); // informer for SpringBootApp registered
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
    
    /**
     * обробник подій для інформера
     * event handler for informer
     * обработчик событий для информера
     */
    private static class SparkyOperatorEventHandler implements ResourceEventHandler<SpringBootApp> {
        private final SpringBootAppController controller;
        
        public SparkyOperatorEventHandler(SpringBootAppController controller) {
            this.controller = controller;
        }
        
        @Override
        public void onAdd(SpringBootApp springBootApp) {
            logger.info("додано новий ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            logger.info("додано новий ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            logger.info("добавлен новый ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            controller.reconcile(springBootApp);
        }
        
        @Override
        public void onUpdate(SpringBootApp oldSpringBootApp, SpringBootApp newSpringBootApp) {
            logger.info("оновлено ресурс SpringBootApp: {}", newSpringBootApp.getMetadata().getName());
            logger.info("оновлено ресурс SpringBootApp: {}", newSpringBootApp.getMetadata().getName());
            logger.info("обновлен ресурс SpringBootApp: {}", newSpringBootApp.getMetadata().getName());
            controller.reconcile(newSpringBootApp);
        }
        
        @Override
        public void onDelete(SpringBootApp springBootApp, boolean deletedFinalStateUnknown) {
            logger.info("видалено ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            logger.info("видалено ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            logger.info("удален ресурс SpringBootApp: {}", springBootApp.getMetadata().getName());
            controller.delete(springBootApp);
        }
    }
}