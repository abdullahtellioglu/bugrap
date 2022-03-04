package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.services.CommentService;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.views.component.CommentAttachmentLayout;
import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.bugrap.views.pages.ReportDetailPage;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportsOverviewLayout extends VerticalLayout implements OverviewUpdateBar.ReportsUpdateListener {
    private final ReportService reportService;
    private final ProjectService projectService;
    private final CommentService commentService;
    private final UserService userService;

    private Set<Report> reports;
    boolean massModificationModeOn;
    private final HorizontalLayout reportInfoContainerLayout = new HorizontalLayout();
    private final OverviewUpdateBar overviewUpdateBar = new OverviewUpdateBar();
    private final Span primaryLabel;
    private final Span secondaryLabel;
    private final Anchor openInNewTabLabel;
    private final CommentList commentList;
    private final CommentAttachmentLayout commentAttachmentLayout;
    private ReportUpdateListener reportUpdateListener;

    private Reporter reporter;

    public void setReportUpdateListener(ReportUpdateListener reportUpdateListener) {
        this.reportUpdateListener = reportUpdateListener;
    }

    public ReportsOverviewLayout(){
        reportService = new ReportService();
        projectService = new ProjectService();
        userService = new UserService();
        commentService = new CommentService();

        List<Reporter> users = userService.getUsers();

        commentList = new CommentList();
        commentList.setMaxHeight(400, Unit.PIXELS);
        commentList.setWidth(100, Unit.PERCENTAGE);

        setClassName("reports-overview");
        primaryLabel = new Span();
        primaryLabel.setClassName("primary-label");
        secondaryLabel = new Span();
        secondaryLabel.setClassName("secondary-label");


        reportInfoContainerLayout.setWidth(100, Unit.PERCENTAGE);
        reportInfoContainerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout labelContainerLayout = new HorizontalLayout(primaryLabel, secondaryLabel);
        reportInfoContainerLayout.add(labelContainerLayout);
        openInNewTabLabel = new Anchor("", VaadinIcon.EXTERNAL_LINK.create(), new Label("Open"));
        openInNewTabLabel.setTarget("_blank");

        reportInfoContainerLayout.add(openInNewTabLabel);
        add(reportInfoContainerLayout);


        overviewUpdateBar.setListener(this);
        overviewUpdateBar.setReporters(users);
        add(overviewUpdateBar);


        add(commentList);
        commentAttachmentLayout = new CommentAttachmentLayout();
        commentAttachmentLayout.setPadding(false);
        commentAttachmentLayout.setSaveClickListener((ComponentEventListener<ClickEvent<Button>>) event -> onCommentSave());
        add(commentAttachmentLayout);
    }
    private void onCommentSave(){
        if(!reports.iterator().hasNext() || reports.size() > 1){
            throw new IllegalStateException("Only 1 report needs to be overviewed.");
        }
        Report report = reports.iterator().next();
        GroupedComment groupedComment = commentAttachmentLayout.getGroupedComment(report, this.reporter);
        commentService.saveComment(report, groupedComment);
        commentList.setComments(commentService.getGroupedComments(report));
        commentAttachmentLayout.clear();
    }
    public void setReportsAndReporter(Set<Report> reports, Reporter reporter){
        this.reports = reports;
        this.reporter = reporter;
        if(!reports.isEmpty()){
            Report next = reports.iterator().next();
            Project project = next.getProject();
            List<ProjectVersion> projectVersions = projectService.getProjectVersions(project);
            this.overviewUpdateBar.setProjectVersions(projectVersions);
        }
        initLayout();
        setInitialValues();
    }

    private void initLayout(){
        if(reports.size() > 1){
            initMassModificationMode();
        }else{
            initSingleModificationMode();
        }
    }

    private void initSingleModificationMode(){
        massModificationModeOn = false;
        commentAttachmentLayout.setVisible(true);
        secondaryLabel.addClassName("hidden");
        openInNewTabLabel.removeClassName("hidden");
        Report report = reports.iterator().next();
        primaryLabel.setText(report.getSummary());

        RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
        String url = routeConfiguration.getUrl(ReportDetailPage.class, report.getId());
        openInNewTabLabel.setHref(url);
        commentList.setComments(commentService.getGroupedComments(report));
    }
    private void initMassModificationMode(){
        massModificationModeOn = true;
        commentAttachmentLayout.setVisible(false);
        commentList.setComments(new ArrayList<>());
        openInNewTabLabel.addClassName("hidden");
        secondaryLabel.removeClassName("hidden");
        primaryLabel.setText(String.format("%s items selected", reports.size()));
        secondaryLabel.setText("Select a single report to view contents");
    }
    private void setInitialValues(){
        Report.Priority priority = null;
        Report.Type type = null;
        Report.Status status = null;
        Reporter reporter = null;
        ProjectVersion projectVersion = null;
        // find the distinct priority
        Set<Report.Priority> prioritySet = reports.stream().map(Report::getPriority).collect(Collectors.toSet());
        if(prioritySet.size() == 1){
            priority = prioritySet.iterator().next();
        }
        //find the distinct type
        Set<Report.Type> typeSet = reports.stream().map(Report::getType).collect(Collectors.toSet());
        if(typeSet.size() == 1){
            type = typeSet.iterator().next();
        }
        //find the distinct status.
        Set<Report.Status> statusSet = reports.stream().map(Report::getStatus).collect(Collectors.toSet());
        if(statusSet.size() == 1){
            status = statusSet.iterator().next();
        }
        Set<Reporter> reporterSet = reports.stream().map(Report::getAssigned).collect(Collectors.toSet());
        if(reporterSet.size() == 1){
            reporter = reporterSet.iterator().next();
        }

        Set<ProjectVersion> projectVersionSet = reports.stream().map(Report::getVersion).collect(Collectors.toSet());
        if(projectVersionSet.size() == 1){
            projectVersion = projectVersionSet.iterator().next();
        }

        overviewUpdateBar.setOverview(priority, type, status, reporter, projectVersion);

    }

    @Override
    public void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter assigned, ProjectVersion version) {
        reports.forEach(report -> {
            report.setPriority(priority);
            report.setType(type);
            report.setStatus(status);
            report.setAssigned(assigned);
            report.setVersion(version);
            reportService.save(report);
        });
        reportUpdateListener.onUpdated(reports);
        Notification.show("Reports updated successfully!");
    }

    public interface ReportUpdateListener {
        void onUpdated(Set<Report> reports);
    }
}
