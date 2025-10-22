package com.sparky.operator.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

/**
 * кастомний ресурс для спринг бут аплікацій
 * custom resource for spring boot applications
 * кастомный ресурс для спринг бут приложений
 */
@Group("sparky.com")
@Version("v1")
public class SpringBootApp extends CustomResource<SpringBootAppSpec, SpringBootAppStatus> implements Namespaced {
    // це просто каркас для нашого кастомного ресурсу
    // this is just a skeleton for our custom resource
    // это просто каркас для нашего кастомного ресурса
}