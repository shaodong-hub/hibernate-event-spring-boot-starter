package com.github.hibernate.event.tool;

import com.github.hibernate.event.common.EventHandlerMethod;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

/**
 * @author 石少东
 * @date 2020-11-11 15:22
 * @since 1.0
 */


public class HibernateEventListenerInvoker implements PostCollectionRecreateEventListener, PostCollectionRemoveEventListener,
        PostCollectionUpdateEventListener, PreCollectionRecreateEventListener,
        PreCollectionRemoveEventListener, PreCollectionUpdateEventListener, SaveOrUpdateEventListener, PreInsertEventListener, PreDeleteEventListener, PreUpdateEventListener,
        PostInsertEventListener,
        PostDeleteEventListener,
        PostUpdateEventListener {

    private static final long serialVersionUID = -7604282574518275654L;

    public final static MultiValueMap<Class<?>, EventHandlerMethod> HANDLER_METHODS = new LinkedMultiValueMap<>();

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
        if (HANDLER_METHODS.get(event.getClass()) == null) {
            return false;
        }
        for (EventHandlerMethod handlerMethod : HANDLER_METHODS.get(event.getClass())) {
            if (ClassUtils.isAssignable(handlerMethod.getTargetType(), entity.getClass())) {
                ReflectionUtils.invokeMethod(handlerMethod.getMethod(), handlerMethod.getBean(), entity, event);
            }
        }
        return false;
    }
}
