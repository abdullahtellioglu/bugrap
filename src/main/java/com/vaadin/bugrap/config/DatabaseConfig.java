package com.vaadin.bugrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.bugrap.domain.BugrapRepository;

/**
 * Creates the bugrap repository as a bean.
 */
@Configuration
public class DatabaseConfig {

    @Bean
    public BugrapRepository bugrapRepository(){
        return new BugrapRepository();
    }
}
