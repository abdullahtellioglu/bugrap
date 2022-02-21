package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.views.container.ProjectLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@PageTitle("Projects")
@Route(value = "projects")
@RouteAlias(value = "")
public class HomePage extends VerticalLayout {
    private final ProjectService projectService;

    public HomePage(){
        this.projectService = new ProjectService();
        projectService.getActiveProjects().forEach(project -> {
            ProjectLayout projectLayout = new ProjectLayout(project);
            add(projectLayout);
        });
    }

}
