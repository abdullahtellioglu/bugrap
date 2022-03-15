package com.vaadin.bugrap.services;

import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

/**
 * Data access layer for projects. {@link Project}
 */
@Service
public class ProjectService {
    private final BugrapRepository bugrapRepository;

    public ProjectService(BugrapRepository bugrapRepository) {
        this.bugrapRepository = bugrapRepository;
    }

    public List<ProjectVersion> getProjectVersions(Project project) {
        return bugrapRepository.getProjectVersions(project);
    }

    public List<Project> getActiveProjects() {
        return bugrapRepository.getActiveProjects();
    }


}
