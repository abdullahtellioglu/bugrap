package com.vaadin.bugrap.services;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

public class UserService {
    private final BugrapRepository bugrapRepository;
    public UserService() {
        bugrapRepository = new BugrapRepository();
    }

    public Reporter getUser(String username){
        return bugrapRepository.getUser(username);
    }
}
