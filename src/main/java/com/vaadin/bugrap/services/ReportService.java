package com.vaadin.bugrap.services;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.List;

public class ReportService {
    private final BugrapRepository bugrapRepository;

    public ReportService() {
        this.bugrapRepository = new BugrapRepository();
    }

    public List<Report> getReports(ProjectVersion version){
        return bugrapRepository.getReportsForVersion(version);
    }

    public long getCountClosedReports(Project project){
        return bugrapRepository.countClosedReports(project);
    }
    public long getCountClosedReports(ProjectVersion projectVersion){
        return bugrapRepository.countClosedReports(projectVersion);
    }

    public long getCountOpenedReports(Project project){
        return bugrapRepository.countOpenedReports(project);
    }
    public long getCountOpenedReports(ProjectVersion projectVersion){
        return bugrapRepository.countOpenedReports(projectVersion);
    }
    public long getCountUnAssignedReports(Project project){
        List<ProjectVersion> projectVersions = bugrapRepository.getProjectVersions(project);
        long total = projectVersions.stream().mapToLong(bugrapRepository::countUnassignedReports).sum();
//        return bugrapRepository.countUnassignedReports(project);
        return total;
    }
    public long getCountUnAssignedReports(ProjectVersion projectVersion){
        return bugrapRepository.countUnassignedReports(projectVersion);
    }
    public Report save(Report report){
        return bugrapRepository.save(report);
    }


    public List<Report> findReports(BugrapRepository.ReportsQuery query){
        return bugrapRepository.findReports(query);
    }
}
