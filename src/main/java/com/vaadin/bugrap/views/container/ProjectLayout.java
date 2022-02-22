package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.views.component.DistributionBar;
import com.vaadin.bugrap.views.component.ProjectVersionComboBox;
import com.vaadin.bugrap.views.component.ReportStatusLayout;
import com.vaadin.bugrap.views.component.ToolbarLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectLayout extends VerticalLayout {
    private final Project project;
    private final ProjectService projectService;
    public ProjectLayout(Project project){
        this.project = project;
        this.projectService = new ProjectService();
        setClassName("project-layout");
        addAndExpand(new ToolbarLayout());

        VerticalLayout panel = new VerticalLayout();
        panel.setClassName("inner-panel");
        panel.setPadding(false);
        panel.setMargin(false);
        Component reportVersionsAndDistributionBar = getReportVersionsAndDistributionBar();
        panel.addAndExpand(reportVersionsAndDistributionBar);
        panel.add(new ReportStatusLayout());
        add(panel);
    }

    private Component getReportVersionsAndDistributionBar(){
        List<ProjectVersion> projectVersions = projectService.getProjectVersions(project);
        if(projectVersions.size() > 1){
            ProjectVersion allProjectVersion = new ProjectVersion();
            allProjectVersion.setId(-1);
            allProjectVersion.setProject(project);
            allProjectVersion.setVersion("All versions");

            projectVersions.add(0, allProjectVersion);
        }
        HorizontalLayout reportDistributionLayout = new HorizontalLayout();
        reportDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        reportDistributionLayout.setAlignItems(Alignment.CENTER);
        reportDistributionLayout.setJustifyContentMode(JustifyContentMode.START);



        ProjectVersionComboBox versionComboBox = new ProjectVersionComboBox();
        versionComboBox.setItems(projectVersions);


        DistributionBar distributionBar = new DistributionBar(1, 15, 10);


        reportDistributionLayout.add(versionComboBox);
        reportDistributionLayout.add(distributionBar);
        reportDistributionLayout.setMargin(true);
        return reportDistributionLayout;
    }
}
