package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.views.component.DistributionBar;
import com.vaadin.bugrap.views.component.ProjectVersionComboBox;
import com.vaadin.bugrap.views.component.ToolbarLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

public class ProjectLayout extends VerticalLayout {
    private final Project project;
    private final ProjectService projectService;
    public ProjectLayout(Project project){
        this.project = project;
        this.projectService = new ProjectService();

        addAndExpand(new ToolbarLayout());

        HorizontalLayout reportDistributionLayout = new HorizontalLayout();
        ProjectVersionComboBox versionComboBox = new ProjectVersionComboBox();
        versionComboBox.setItems(projectService.getProjectVersions(project));

        DistributionBar distributionBar = new DistributionBar();


        reportDistributionLayout.add(versionComboBox);
        reportDistributionLayout.add(distributionBar);

        add(reportDistributionLayout);
    }
}
