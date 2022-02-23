package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;
import java.util.Optional;

public class ProjectLayout extends VerticalLayout {
    //services
    private final ProjectService projectService;


    //entities
    private final Project project;
    private List<ProjectVersion> projectVersions;
    private ProjectVersion projectVersion;
    private List<Report> reports;

    long closedReportCount = 0;
    long openedReportCount = 0;
    long unAssignedReportCount = 0;

    //Views
    private ReportGrid reportGrid;
    private DistributionBar distributionBar;
    private ProjectVersionSelect projectVersionSelect;
    private ReportStatusLayout reportStatusLayout;
    private final BugrapRepository.ReportsQuery query = new BugrapRepository.ReportsQuery();
    private Reporter currentUser;

    public ProjectLayout(Project project, Reporter currentUser){
        this.project = project;
        this.projectService = new ProjectService();
        this.currentUser = currentUser;
        query.project = project;
        query.reportAssignee = currentUser;

        fetchProjectVersions();
        fetchReportCounts();

        reportGrid = new ReportGrid();

        setClassName("project-layout");
        addAndExpand(new ToolbarLayout());

        VerticalLayout panel = new VerticalLayout();
        panel.setClassName("inner-panel");
        panel.setPadding(false);
        panel.setMargin(false);
        Component reportVersionsAndDistributionBar = getReportVersionsAndDistributionBar();
        panel.addAndExpand(reportVersionsAndDistributionBar);

        reportStatusLayout = new ReportStatusLayout();
        reportStatusLayout.setCurrentUser(currentUser);
        reportStatusLayout.setAssigneeChangeListener(reporter -> {
            query.reportAssignee = reporter;
            reports = projectService.findReports(query);
            reportGrid.setItems(reports);
        });
        panel.add(reportStatusLayout);

        reports = projectService.findReports(query);


        reportGrid.setItems(reports);
        panel.add(reportGrid);
        add(panel);
    }

    private void fetchProjectVersions(){
        projectVersions = projectService.getProjectVersions(project);
        if(projectVersions.size() > 1){
            ProjectVersion allProjectVersion = new ProjectVersion();
            allProjectVersion.setId(-1);
            allProjectVersion.setProject(project);
            allProjectVersion.setVersion("All versions");
            projectVersions.add(0, allProjectVersion);
            this.projectVersion = allProjectVersion;
            int lastSelectedProjectVersion = CookieUtils.getLastSelectedProjectVersion(project, RequestUtils.getCurrentHttpRequest());
            projectVersions.stream().filter(pv -> pv.getId() == lastSelectedProjectVersion).findFirst()
                    .ifPresent(cookieVersion -> this.projectVersion = cookieVersion);

            return;
        }
        if(projectVersions.size() == 1){
            this.projectVersion = projectVersions.get(0);
        }
    }

    private Component getReportVersionsAndDistributionBar(){
        HorizontalLayout reportDistributionLayout = new HorizontalLayout();
        reportDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        reportDistributionLayout.setAlignItems(Alignment.CENTER);
        reportDistributionLayout.setJustifyContentMode(JustifyContentMode.START);

        projectVersionSelect = new ProjectVersionSelect();
        projectVersionSelect.setItems(projectVersions);
        projectVersionSelect.setValue(projectVersion);

        projectVersionSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<ProjectVersion>, ProjectVersion>>) event -> {
            ProjectVersion value = event.getValue();
            this.projectVersion = value;
            fetchReportCounts();

            onProjectVersionChange(value);
        });

        distributionBar = new DistributionBar(closedReportCount, openedReportCount, unAssignedReportCount);

        reportDistributionLayout.add(projectVersionSelect);
        reportDistributionLayout.add(distributionBar);
        reportDistributionLayout.setMargin(true);
        return reportDistributionLayout;
    }

    private void fetchReportCounts(){
        if(projectVersion == null || projectVersion.getId() == -1){
            closedReportCount = projectService.getCountClosedReports(project);
            openedReportCount = projectService.getCountOpenedReports(project);
            unAssignedReportCount = 10;
//            unAssignedProjectCount = projectService.getCountUnAssignedReports(project);
        }else{
            closedReportCount = projectService.getCountClosedReports(projectVersion);
            openedReportCount = projectService.getCountOpenedReports(projectVersion);
            unAssignedReportCount = 10;
//            unAssignedProjectCount = projectService.getCountUnAssignedReports(projectVersion);
        }
    }
    private void onProjectVersionChange(ProjectVersion projectVersion){
        if(projectVersion.getId() == -1){
            query.projectVersion = null;
        }else{
            query.projectVersion = projectVersion;
        }

        CookieUtils.addLastSelectedProjectVersion(project, projectVersion, RequestUtils.getCurrentHttpResponse());

        List<Report> reports = projectService.findReports(query);
        reportGrid.setItems(reports);
        //TODO fix these values
        distributionBar.setValues(closedReportCount, openedReportCount, unAssignedReportCount);
    }
    public void setCurrentUser(Reporter user){
        this.currentUser = user;
    }


}
