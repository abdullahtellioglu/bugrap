package com.vaadin.bugrap.services;

import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.ReportStatus;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

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
        return bugrapRepository.countUnassignedReports(project);
    }
    public long getCountUnAssignedReports(ProjectVersion projectVersion){
        return bugrapRepository.countUnassignedReports(projectVersion);
    }


    public List<Report> findReports(BugrapRepository.ReportsQuery query){
        return bugrapRepository.findReports(query);
    }
}
