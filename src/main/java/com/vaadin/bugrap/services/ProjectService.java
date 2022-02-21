package com.vaadin.bugrap.services;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectService {
    private final BugrapRepository bugrapRepository;
    public ProjectService(){
        this.bugrapRepository = new BugrapRepository();
    }
    public List<ProjectVersion> getProjectVersions(Project project){
        return bugrapRepository.getProjectVersions(project);
    }
    public List<Project> getActiveProjects(){
        return bugrapRepository.getActiveProjects();
    }
}
