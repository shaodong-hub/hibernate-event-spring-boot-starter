package com.github.hibernate.event;

import com.github.hibernate.event.tool.AnnotatedHibernateEventListenerInvoker;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

/**
 * @author 石少东
 * @date 2020-11-11 15:21
 * @since 1.0
 */

@Configuration
@ConditionalOnClass(EntityManagerFactory.class)
public class HibernateEventAutoConfiguration {


    @Resource
    private ApplicationContext context;

    @Bean
    @ConditionalOnMissingBean
    public AnnotatedHibernateEventListenerInvoker annotatedHibernateEventHandlerInvoker(EntityManagerFactory entityManagerFactory) {
        AnnotatedHibernateEventListenerInvoker invoker = new AnnotatedHibernateEventListenerInvoker();
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.prependListeners(EventType.PRE_UPDATE, invoker);
        registry.prependListeners(EventType.PRE_DELETE, invoker);
        registry.prependListeners(EventType.PRE_INSERT, invoker);
        registry.prependListeners(EventType.POST_UPDATE, invoker);
        registry.prependListeners(EventType.POST_DELETE, invoker);
        registry.prependListeners(EventType.POST_INSERT, invoker);
        registry.prependListeners(EventType.PRE_COLLECTION_RECREATE, invoker);
        registry.prependListeners(EventType.PRE_COLLECTION_REMOVE, invoker);
        registry.prependListeners(EventType.PRE_COLLECTION_UPDATE, invoker);
        registry.prependListeners(EventType.POST_COLLECTION_RECREATE, invoker);
        registry.prependListeners(EventType.POST_COLLECTION_REMOVE, invoker);
        registry.prependListeners(EventType.POST_COLLECTION_UPDATE, invoker);
        registry.prependListeners(EventType.SAVE_UPDATE, invoker);
        return invoker;
    }

}
