package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.views.component.*;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectLayout extends VerticalLayout {
    private final ProjectService projectService;
    private final Project project;


    private List<ProjectVersion> projectVersions;
    private ProjectVersion projectVersion;

    long closedReportCount = 0;
    long openedReportCount = 0;
    long unAssignedReportCount = 0;

    //Views
    private ReportGrid reportGrid;
    private DistributionBar distributionBar;
    private ProjectVersionComboBox projectVersionComboBox;

    public ProjectLayout(Project project){
        this.project = project;
        this.projectService = new ProjectService();
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
        panel.add(new ReportStatusLayout());

        reportGrid.setItems(projectService.findReports(new BugrapRepository.ReportsQuery()));
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
            this.projectVersion = allProjectVersion;
            projectVersions.add(0, allProjectVersion);

        }
    }

    private Component getReportVersionsAndDistributionBar(){
        HorizontalLayout reportDistributionLayout = new HorizontalLayout();
        reportDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        reportDistributionLayout.setAlignItems(Alignment.CENTER);
        reportDistributionLayout.setJustifyContentMode(JustifyContentMode.START);

        projectVersionComboBox = new ProjectVersionComboBox();
        projectVersionComboBox.setItems(projectVersions);
        projectVersionComboBox.setValue(projectVersion);

        projectVersionComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<ProjectVersion>, ProjectVersion>>) event -> {
            ProjectVersion value = event.getValue();
            this.projectVersion = value;
            fetchReportCounts();
            //TODO where to select last selected value? Cache or DB ?

            onProjectVersionChange(value);
        });

        distributionBar = new DistributionBar(closedReportCount, openedReportCount, unAssignedReportCount);

        reportDistributionLayout.add(projectVersionComboBox);
        reportDistributionLayout.add(distributionBar);
        reportDistributionLayout.setMargin(true);
        return reportDistributionLayout;
    }
    private void fetchReportCounts(){
        if(projectVersion == null || projectVersion.getId() == -1){
            closedReportCount = projectService.getCountClosedReports(project);
            openedReportCount = projectService.getCountOpenedReports(project);
//            unAssignedProjectCount = projectService.getCountUnAssignedReports(project);
        }else{
            closedReportCount = projectService.getCountClosedReports(projectVersion);
            openedReportCount = projectService.getCountOpenedReports(projectVersion);
//            unAssignedProjectCount = projectService.getCountUnAssignedReports(projectVersion);
        }
    }
    private void onProjectVersionChange(ProjectVersion projectVersion){


        //TODO fix these values

        distributionBar.setAssignedValue(openedReportCount);
        distributionBar.setClosedValue(closedReportCount);
        distributionBar.setUnAssignedValue(unAssignedReportCount);
    }
}
