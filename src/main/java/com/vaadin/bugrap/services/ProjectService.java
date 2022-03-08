package com.vaadin.bugrap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.ReportStatus;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.Serializable;
import java.util.List;

@Service
public class ProjectService {
    private BugrapRepository bugrapRepository;
    @Autowired
    public void setBugrapRepository(BugrapRepository bugrapRepository){
        this.bugrapRepository = bugrapRepository;
    }
    public Project getProject(long id){
        return bugrapRepository.getProject(id);
    }
    public List<ProjectVersion> getProjectVersions(Project project){
        return bugrapRepository.getProjectVersions(project);
    }
    public List<Project> getActiveProjects(){
        return bugrapRepository.getActiveProjects();
    }




}
