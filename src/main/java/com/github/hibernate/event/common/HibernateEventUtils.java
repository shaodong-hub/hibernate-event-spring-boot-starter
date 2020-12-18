package com.github.hibernate.event.common;

import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Arrays;

/**
 * @author 石少东
 * @date 2020-11-11 15:22
 * @since 1.0
 */


public class HibernateEventUtils {

    public static int getPropertyIndex(AbstractPreDatabaseOperationEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PreUpdateEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PreInsertEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PreDeleteEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PostUpdateEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PostInsertEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(PostDeleteEvent event, String property){
        return getPropertyIndex(event.getPersister(), property);
    }

    public static int getPropertyIndex(SaveOrUpdateEvent event, String property){
        return getPropertyIndex(event.getEntry().getPersister(), property);
    }

    public static int getPropertyIndex(EntityPersister resister, String property){
        return Arrays.asList(resister.getPropertyNames()).indexOf(property);
    }
}
