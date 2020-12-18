package com.github.hibernate.event.tool;

import com.github.hibernate.event.annotation.HibernateEventListener;
import com.github.hibernate.event.exception.AssignableParameterException;
import com.github.hibernate.event.exception.InvalidParameterCountException;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;
import org.hibernate.event.spi.PostCollectionRemoveEvent;
import org.hibernate.event.spi.PostCollectionRemoveEventListener;
import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreCollectionRecreateEvent;
import org.hibernate.event.spi.PreCollectionRecreateEventListener;
import org.hibernate.event.spi.PreCollectionRemoveEvent;
import org.hibernate.event.spi.PreCollectionRemoveEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author 石少东
 * @date 2020-11-11 15:22
 * @since 1.0
 */


public class AnnotatedHibernateEventListenerInvoker implements PostCollectionRecreateEventListener, PostCollectionRemoveEventListener,
        PostCollectionUpdateEventListener, PreCollectionRecreateEventListener,
        PreCollectionRemoveEventListener, PreCollectionUpdateEventListener, SaveOrUpdateEventListener, PreInsertEventListener, PreDeleteEventListener, PreUpdateEventListener,
        PostInsertEventListener,
        PostDeleteEventListener,
        PostUpdateEventListener,
        BeanPostProcessor {

    private static final long serialVersionUID = -7604282574518275654L;

    private final MultiValueMap<Class<?>, EventHandlerMethod> handlerMethods = new LinkedMultiValueMap<>();

    @Override
    public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
        onEvent(event, event.getAffectedOwnerOrNull());
    }

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
        onEvent(event, event.getEntity());
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        onEvent(event, event.getEntity());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        onEvent(event, event.getEntity());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        onEvent(event, event.getEntity());
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        return onEvent(event, event.getEntity());
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        return onEvent(event, event.getEntity());
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        return onEvent(event, event.getEntity());
    }

    private boolean onEvent(AbstractEvent event, Object entity) {
        if (handlerMethods.get(event.getClass()) == null) {
            return false;
        }

        for (EventHandlerMethod handlerMethod : handlerMethods.get(event.getClass())) {
            if (ClassUtils.isAssignable(handlerMethod.targetType, entity.getClass())) {
                ReflectionUtils.invokeMethod(handlerMethod.method, handlerMethod.bean, entity, event);
            }
        }

        return false;
    }

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

    private <T extends Annotation> void register(Object bean, Method method) throws InvalidParameterCountException, AssignableParameterException {
        HibernateEventListener listener = AnnotationUtils.findAnnotation(method, HibernateEventListener.class);
        // 没有这个注解
        if (listener == null) {
            return;
        }
        Class<?> clz = listener.value();
        // 有这个注解,但是参数类型确实
        if (method.getParameterCount() <= 1 && supports(clz)) {
            throw new InvalidParameterCountException("参数缺失.");
        }

        if (method.getParameterCount() > 2) {
            throw new InvalidParameterCountException("方法参数过多.");
        }

        //有这个注解，并且填写类型
        if (method.getParameterCount() != 2) {
            throw new InvalidParameterCountException("The method must have exactly 2 parameters.");
        }

        Class<?> entityClass = ResolvableType.forMethodParameter(method, 0, bean.getClass()).resolve();
        Class<?> eventClass = ResolvableType.forMethodParameter(method, 1, bean.getClass()).resolve();

        if (!ClassUtils.isAssignable(AbstractEvent.class, eventClass)) {
            throw new AssignableParameterException("The second parameter must be of type org.hibernate.event.spi.AbstractEvent.");
        }

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


    static class EventHandlerMethod implements Comparable<EventHandlerMethod> {
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
}
