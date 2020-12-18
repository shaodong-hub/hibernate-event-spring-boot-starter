package com.github.hibernate.event.common;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.lang.reflect.Method;

/**
 * @author 石少东
 * @date 2020-11-11 17:10
 * @since 1.0
 */

@Getter
public class EventHandlerMethod implements Comparable<EventHandlerMethod> {
    final Class<?> targetType;
    final Method method;
    final Object bean;

    public EventHandlerMethod(Class<?> targetType, Object bean, Method method) {
        this.targetType = targetType;
        this.method = method;
        this.bean = bean;
    }

    @Override
    public int compareTo(@NotNull EventHandlerMethod o) {
        return AnnotationAwareOrderComparator.INSTANCE.compare(this.method, o.method);
    }
}