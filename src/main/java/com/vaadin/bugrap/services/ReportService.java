package com.vaadin.bugrap.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.List;



@Service
public class ReportService {
    private BugrapRepository bugrapRepository;

    @Autowired
    public void setBugrapRepository(BugrapRepository bugrapRepository){
        this.bugrapRepository = bugrapRepository;
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


    public Report getReport(long id){
        return bugrapRepository.getReport(id);
    }

    public List<Report> findReports(BugrapRepository.ReportsQuery query){
        return bugrapRepository.findReports(query);
    }
}
