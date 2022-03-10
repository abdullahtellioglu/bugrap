package com.vaadin.bugrap.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * ContextWrapper is a spring component that is used to access Application Context or Bean statically.
 */
@Component
public class ContextWrapper implements ApplicationContextAware {

    private static ApplicationContext ac;
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ContextWrapper.ac = applicationContext;
    }
    public static <T> T getBean(Class<T> type){
        return ac.getBean(type);
    }

}
