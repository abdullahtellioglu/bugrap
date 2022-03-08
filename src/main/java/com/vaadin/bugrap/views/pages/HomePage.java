package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.ProjectSelector;
import com.vaadin.bugrap.views.container.ProjectLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;
import java.util.Optional;

@PageTitle("Projects")
@Route(value = "projects")
@RouteAlias(value = "")
public class HomePage extends VerticalLayout {
    private final ReportService reportService;
    private final ProjectSelector projectSelector;
    private final UserService userService;
    private final ProjectLayout projectLayout;


    public HomePage(ReportService reportService, ProjectService projectService, UserService userService){
        this.reportService = reportService;
        this.userService = userService;

        projectSelector = new ProjectSelector();
        List<Project> activeProjects = projectService.getActiveProjects();

        // we dont have login screen. That's why we need to find the current user from assignees if it is first run, otherwise read username from cookie
        Reporter currentUser = createDummyUserFromListOrReadFromCookie(activeProjects.get(0));
        projectLayout = new ProjectLayout(currentUser);

        projectSelector.setProjectSelectListener(project -> {
            projectLayout.setOpenedReportCount(reportService.getCountOpenedReports(project));
            projectLayout.setProject(project);
            projectSelector.setManagerName(project.getManager().getName());
        });
        add(projectSelector);
        add(projectLayout);
        projectSelector.setActiveProjects(activeProjects);

        setClassName("home-page");
        setSpacing(false);
        setPadding(false);
    }
    private Reporter getDummyUserFromProject(Project project){
        BugrapRepository.ReportsQuery reportsQuery = new BugrapRepository.ReportsQuery();
        reportsQuery.project = project;
        List<Report> reports = this.reportService.findReports(reportsQuery);

        String currentUserName = CookieUtils.getCurrentUserName(RequestUtils.getCurrentHttpRequest());
        if(currentUserName == null){
            Optional<Report> foundNotUnAssignedReport = reports.stream().filter(f -> f.getAssigned() != null).findFirst();
            if(foundNotUnAssignedReport.isPresent()){
                return foundNotUnAssignedReport.get().getAssigned();
            }
        }
        return null;
    }
    private Reporter createDummyUserFromListOrReadFromCookie(Project project){
        String currentUserName = CookieUtils.getCurrentUserName(RequestUtils.getCurrentHttpRequest());
        Reporter user;
        if(currentUserName != null){
            user = userService.getUser(currentUserName);
        }else{
            user = getDummyUserFromProject(project);
            if(user == null){
                return null;
            }
            String name = user.getName();
            CookieUtils.putCurrentUserName(name, RequestUtils.getCurrentHttpResponse());
        }
        return user;

    }

}
