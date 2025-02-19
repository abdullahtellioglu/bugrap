package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.CommentService;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.views.component.CommentAttachmentLayout;
import com.vaadin.bugrap.views.component.ReportDetailBreadcrumb;
import com.vaadin.bugrap.views.component.overview.CommentList;
import com.vaadin.bugrap.views.component.overview.OverviewUpdateBar;
import com.vaadin.bugrap.views.container.NotFoundLayout;
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
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

/**
 * Displaying report detail page. User can add comment, download attachments and change status of given report.
 */
@PageTitle("Report Detail")
@Route(value = "/report")
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
    private final NotFoundLayout notFoundLayout = new NotFoundLayout();
    private Report report;


    public ReportDetailPage(ProjectService projectService, ReportService reportService, UserService userService, CommentService commentService) {
        this.projectService = projectService;
        this.reportService = reportService;
        this.userService = userService;
        this.commentService = commentService;

        reportDetailBreadcrumb = new ReportDetailBreadcrumb();

        reportNameLabel = new Label();
        reportNameLabel.setClassName("report-name");
        overviewUpdateBar = new OverviewUpdateBar();
        overviewUpdateBar.setListener(this);

        VerticalLayout reportNameContainer = new VerticalLayout(reportNameLabel, overviewUpdateBar);
        reportNameContainer.setPadding(true);

        commentList = new CommentList();
        commentList.setCommentRowPadding(true);
        commentList.setCommentRowMargin(true);

        VerticalLayout innerPanel = new VerticalLayout(reportNameContainer, commentList);
        innerPanel.setPadding(false);
        innerPanel.setHeight(100, Unit.PERCENTAGE);
        innerPanel.setWidth(100, Unit.PERCENTAGE);
Button button = new Button("Button");
innerPanel.add(button);

        VerticalLayout topLayout = new VerticalLayout(reportDetailBreadcrumb, innerPanel);
        topLayout.setHeight("calc(100% - 520px)");
        topLayout.setPadding(false);
        topLayout.setMargin(false);

        commentAttachmentLayout.setClassName("bottom-panel");
        commentAttachmentLayout.setSaveClickListener((ComponentEventListener<ClickEvent<Button>>) event -> onSaveCommentClick());

        add(topLayout, commentAttachmentLayout);
        notFoundLayout.setVisible(false);
        add(notFoundLayout);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setSpacing(false);
        setPadding(false);
        setClassName("report-detail-page");

    }

    private void onSaveCommentClick() {
        String currentUserName = CookieUtils.getCurrentUserName();
        Reporter user = null;
        if (currentUserName != null) {
            user = userService.getUser(currentUserName);
        }
        if (user == null) {
            Notification.show("Session timeout. Please open home page and return");
            return;
        }

        GroupedComment groupedComment = commentAttachmentLayout.getGroupedComment(report, user);
        commentService.saveComment(report, groupedComment);
        commentAttachmentLayout.clear();
        commentList.setComments(commentService.getGroupedComments(report));

    }

    private void setReport(Report report) {
        this.report = report;
        reportDetailBreadcrumb.setVersionName(report.getVersion() != null ? report.getVersion().getVersion() : "No version");
        overviewUpdateBar.setProjectVersions(projectService.getProjectVersions(report.getProject()));
        overviewUpdateBar.setReporters(userService.getUsers());
        overviewUpdateBar.setOverview(report.getPriority(), report.getType(), report.getStatus(), report.getAssigned(), report.getVersion(), false);

        reportNameLabel.setText(report.getSummary());

        commentList.setComments(commentService.getGroupedComments(report));
    }

    /**
     * Query parameter initializer for report Id. If report or project is not found hides all elements and shows the {@link NotFoundLayout}
     *
     * @param event    Internal event
     * @param reportId Report Id
     */
    @Override
    public void setParameter(BeforeEvent event, Long reportId) {
        Report report = reportService.getReport(reportId);

        if (report != null) {
            Project project = report.getProject();
            if (project != null) {
                reportDetailBreadcrumb.setProjectName(project.getName());
            } else {
                reportDetailBreadcrumb.setVisible(false);
                notFoundLayout.setVisible(true);
            }
            overviewUpdateBar.setVisible(true);
            commentAttachmentLayout.setVisible(true);
            setReport(report);
        } else {
            reportDetailBreadcrumb.setVisible(false);
            overviewUpdateBar.setVisible(false);
            commentAttachmentLayout.setVisible(false);
            notFoundLayout.setVisible(true);

            Notification.show("Please select a valid report to display details").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Overview panel save changes listener implementation. <br/>
     * Saving report into database.<b> In this step all fields needs to be passed requirements, null checks to save.</b> <br/>
     * If version is changed, it updates the breadcrumb version as well.
     *
     * @param priority Report priority
     * @param type     Report type
     * @param status   Report status
     * @param reporter Reporter
     * @param version  Project version.
     */
    @Override
    public void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion version) {
        report.setPriority(priority);
        report.setType(type);
        report.setStatus(status);
        report.setAssigned(reporter);
        report.setVersion(version);
        this.report = reportService.save(report);
        reportDetailBreadcrumb.setVersionName(report.getVersion() != null ? report.getVersion().getVersion() : "No version");
        Notification.show("Report updated successfully").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
