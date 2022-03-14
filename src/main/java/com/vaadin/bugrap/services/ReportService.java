package com.vaadin.bugrap.services;

import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.List;


/**
 * Data access layer for reports. {@link Report}
 */
@Service
public class ReportService {
    private final BugrapRepository bugrapRepository;

    public ReportService(BugrapRepository bugrapRepository) {
        this.bugrapRepository = bugrapRepository;
    }

    public long getCountClosedReports(Project project) {
        return bugrapRepository.countClosedReports(project);
    }

    public long getCountClosedReports(ProjectVersion projectVersion) {
        return bugrapRepository.countClosedReports(projectVersion);
    }

    public long getCountOpenedReports(Project project) {
        return bugrapRepository.countOpenedReports(project);
    }

    public long getCountOpenedReports(ProjectVersion projectVersion) {
        return bugrapRepository.countOpenedReports(projectVersion);
    }

    public long getCountUnAssignedReports(Project project) {
        List<ProjectVersion> projectVersions = bugrapRepository.getProjectVersions(project);
        return projectVersions.stream().mapToLong(bugrapRepository::countUnassignedReports).sum();
    }

    public long getCountUnAssignedReports(ProjectVersion projectVersion) {
        return bugrapRepository.countUnassignedReports(projectVersion);
    }

    public Report save(Report report) {
        return bugrapRepository.save(report);
    }


    public Report getReport(long id) {
        return bugrapRepository.getReport(id);
    }

    public List<Report> findReports(BugrapRepository.ReportsQuery query) {
        return bugrapRepository.findReports(query);
    }
}
