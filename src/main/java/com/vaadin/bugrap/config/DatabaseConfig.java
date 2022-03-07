package com.vaadin.bugrap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaadin.bugrap.domain.BugrapRepository;

@Configuration
public class DatabaseConfig {

    @Bean
    public BugrapRepository bugrapRepository(){
        return new BugrapRepository();
    }
}
