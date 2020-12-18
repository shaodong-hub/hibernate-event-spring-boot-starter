package com.github.hibernate.event.tool;

import com.github.hibernate.event.annotation.HibernateEventListener;
import com.github.hibernate.event.common.EventHandlerMethod;
import com.github.hibernate.event.exception.AssignableParameterException;
import com.github.hibernate.event.exception.InvalidParameterCountException;
import org.hibernate.event.spi.AbstractEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author 石少东
 * @date 2020-11-11 15:47
 * @since 1.0
 */


public class HibernateEventListenerBeanPostProcessor implements BeanPostProcessor {

    private final MultiValueMap<Class<?>, EventHandlerMethod> handlerMethods = HibernateEventListenerInvoker.HANDLER_METHODS;

    private final static int args = 2;

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        for (Method method : ReflectionUtils.getUniqueDeclaredMethods(ClassUtils.getUserClass(bean))) {
            try {
                register(bean, method);
            } catch (InvalidParameterCountException | AssignableParameterException e) {
                throw new BeanInitializationException(HibernateEventListener.class.getName() + " 注解注册失败。", e);
            }
        }
        return bean;
    }

    private void register(Object bean, Method method) throws InvalidParameterCountException, AssignableParameterException {
        HibernateEventListener listener = AnnotationUtils.findAnnotation(method, HibernateEventListener.class);
        // 没有这个注解
        if (listener == null) {
            return;
        }
        Class<?> eventClass = listener.value();
        // 有这个注解,但是参数类型确实
        if (method.getParameterCount() != args) {
            throw new InvalidParameterCountException("参数错误.");
        }
        Class<?> entityClass = ResolvableType.forMethodParameter(method, 0, bean.getClass()).resolve();
        ReflectionUtils.makeAccessible(method);
        EventHandlerMethod handlerMethod = new EventHandlerMethod(entityClass, bean, method);
        handlerMethods.add(eventClass, handlerMethod);
        List<EventHandlerMethod> events = handlerMethods.get(eventClass);
        if (events.size() > 1) {
            Collections.sort(events);
            handlerMethods.put(eventClass, events);
        }
    }

    private boolean supports(Class<?> clazz) {
        return !ObjectUtils.isEmpty(clazz) && AbstractEvent.class.isAssignableFrom(clazz);
    }

}
