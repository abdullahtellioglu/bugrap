package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.ProjectSelector;
import com.vaadin.bugrap.views.container.ProjectLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.checkerframework.common.util.report.qual.ReportUse;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@PageTitle("Projects")
@Route(value = "projects")
@RouteAlias(value = "")
public class HomePage extends VerticalLayout {
    private final ProjectService projectService;
    private final ReportService reportService;
    private final ProjectSelector projectSelector;
    private final UserService userService;
    private ProjectLayout projectLayout;
    public HomePage(){
        this.projectService = new ProjectService();
        this.reportService = new ReportService();
        userService = new UserService();
        setClassName("home-page");
        setSpacing(false);
        setPadding(false);
        projectSelector = new ProjectSelector();
        List<Project> activeProjects = projectService.getActiveProjects();
        add(projectSelector);
        // we dont have login screen. That's why we need to find the current user from assignees if it is first run, otherwise read username from cookie
        Reporter currentUser = createDummyUserFromListOrReadFromCookie(activeProjects.get(0));
        projectLayout = new ProjectLayout(currentUser);

        projectSelector.setProjectSelectListener(project -> {
            projectLayout.setProjectCount(activeProjects.size());
            projectLayout.setProject(project);
            projectSelector.setManagerName(project.getManager().getName());
        });
        add(projectLayout);


        projectSelector.setActiveProjects(activeProjects);
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
        Reporter user = null;
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
