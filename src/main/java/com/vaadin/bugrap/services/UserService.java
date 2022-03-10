package com.vaadin.bugrap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;

/**
 * Data access layer for users. {@link Reporter}
 */
@Service
public class UserService {
    private BugrapRepository bugrapRepository;
    @Autowired
    public void setBugrapRepository(BugrapRepository bugrapRepository){
        this.bugrapRepository = bugrapRepository;
    }
    public Reporter getUser(String username){
        return bugrapRepository.getUser(username);
    }
    public List<Reporter> getUsers(){
        return List.copyOf(bugrapRepository.findReporters());
    }
}
