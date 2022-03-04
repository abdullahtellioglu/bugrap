package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.CommentService;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.CommentAttachmentLayout;
import com.vaadin.bugrap.views.component.ReportDetailBreadcrumb;
import com.vaadin.bugrap.views.component.overview.CommentList;
import com.vaadin.bugrap.views.component.overview.OverviewUpdateBar;
import com.vaadin.bugrap.views.model.GroupedComment;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

@PageTitle("Report Detail")
@Route(value = "/report")
@RouteAlias(value = "/report")
public class ReportDetailPage extends VerticalLayout implements HasUrlParameter<Long>, OverviewUpdateBar.ReportsUpdateListener {
    private final ProjectService projectService;
    private final ReportService reportService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReportDetailBreadcrumb reportDetailBreadcrumb;
    private final Label reportNameLabel;
    private final OverviewUpdateBar overviewUpdateBar;
    private final CommentList commentList;
    private final CommentAttachmentLayout commentAttachmentLayout = new CommentAttachmentLayout();

    private Report report;


    //TODO should display notification if user clicks comment.
    public ReportDetailPage(){
        projectService = new ProjectService();
        reportService = new ReportService();
        userService = new UserService();
        commentService = new CommentService();
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setSpacing(false);
        setPadding(false);
        setClassName("report-detail-page");


        reportDetailBreadcrumb = new ReportDetailBreadcrumb();
        add(reportDetailBreadcrumb);

        reportNameLabel = new Label();
        reportNameLabel.setClassName("report-name");
        overviewUpdateBar = new OverviewUpdateBar();
        overviewUpdateBar.setListener(this);
        commentList = new CommentList();
        //TODO is is correct way to do it ?
        //TODO make scrollbar to align top of bottom panel.
        commentList.setMaxHeight(430, Unit.PIXELS);


        VerticalLayout innerPanel = new VerticalLayout(reportNameLabel, overviewUpdateBar, commentList);
        innerPanel.setPadding(true);
        innerPanel.setHeight(100, Unit.PERCENTAGE);
        innerPanel.setWidth(100, Unit.PERCENTAGE);
        add(innerPanel);




        commentAttachmentLayout.setClassName("bottom-panel");
        commentAttachmentLayout.setSaveClickListener((ComponentEventListener<ClickEvent<Button>>) event -> onSaveClick());

        add(commentAttachmentLayout);

    }
    private void onSaveClick() {
        String currentUserName = CookieUtils.getCurrentUserName(RequestUtils.getCurrentHttpRequest());
        Reporter user = null;
        if(currentUserName != null){
            user = userService.getUser(currentUserName);
        }
        if(user == null){
            Notification.show("Session timeout. Please open home page and return");
            return;
        }

        GroupedComment groupedComment = commentAttachmentLayout.getGroupedComment(report, user);
        commentService.saveComment(report, groupedComment);
        commentAttachmentLayout.clear();
        commentList.setComments(commentService.getGroupedComments(report));

    }

    private void setReport(Report report){
        this.report = report;
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(), report.getVersion() != null ? report.getVersion().getVersion() : "No version");
        overviewUpdateBar.setProjectVersions(projectService.getProjectVersions(report.getProject()));
        overviewUpdateBar.setReporters(userService.getUsers());
        overviewUpdateBar.setOverview(report.getPriority(), report.getType(), report.getStatus(), report.getAssigned(), report.getVersion());

        reportNameLabel.setText(report.getSummary());

        commentList.setComments(commentService.getGroupedComments(report));
    }

    @Override
    public void setParameter(BeforeEvent event, Long reportId) {
        Report report = reportService.getReport(reportId);
        if(report != null){
            overviewUpdateBar.setVisible(true);
            commentAttachmentLayout.setVisible(true);
            setReport(report);
        } else {
            overviewUpdateBar.setVisible(false);
            commentAttachmentLayout.setVisible(false);
            reportDetailBreadcrumb.setReportNameAndVersionName("Invalid Report", "");

            Notification invalidReportNotification = new Notification("Please select a valid report to display details");
            invalidReportNotification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            invalidReportNotification.open();
        }
    }

    @Override
    public void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion version) {
        report.setPriority(priority);
        report.setType(type);
        report.setStatus(status);
        report.setAssigned(reporter);
        report.setVersion(version);
        this.report =  reportService.save(report);
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(),report.getVersion() != null ? report.getVersion().getVersion() : "No version" );
        Notification.show("Report updated successfully");
    }
}
