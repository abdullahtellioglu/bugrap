package com.vaadin.bugrap.services;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final BugrapRepository bugrapRepository;
    public UserService() {
        bugrapRepository = new BugrapRepository();
    }

    public Reporter getUser(String username){
        return bugrapRepository.getUser(username);
    }
    public List<Reporter> getUsers(){
        //TODO fix
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
