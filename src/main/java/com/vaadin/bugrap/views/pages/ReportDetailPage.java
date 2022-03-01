package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.CommentService;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.services.UserService;
import com.vaadin.bugrap.views.component.ReportDetailBreadcrumb;
import com.vaadin.bugrap.views.component.overview.CommentList;
import com.vaadin.bugrap.views.component.overview.OverviewUpdateBar;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.router.*;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

@PageTitle("Report Detail")
@Route(value = "/report")
@RouteAlias(value = "/report")
public class ReportDetailPage extends VerticalLayout implements HasUrlParameter<Long> {
    private final ProjectService projectService;
    private final ReportService reportService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReportDetailBreadcrumb reportDetailBreadcrumb;
    private final Label reportNameLabel;
    private final OverviewUpdateBar overviewUpdateBar;
    private final CommentList commentList;
    private final RichTextEditor commentRichTextEditor;

    private Project project;
    private Report report;

    public ReportDetailPage(){
        projectService = new ProjectService();
        reportService = new ReportService();
        userService = new UserService();
        commentService = new CommentService();
        setSpacing(false);
        setPadding(false);
        setClassName("report-detail-page");
        //TODO in design Comment Rows right layout do not match with Revert button bottom right. Is it correct or ?


        reportDetailBreadcrumb = new ReportDetailBreadcrumb();

        add(reportDetailBreadcrumb);



        reportNameLabel = new Label();
        reportNameLabel.setClassName("report-name");
        overviewUpdateBar = new OverviewUpdateBar();
        commentList = new CommentList();

        VerticalLayout innerPanel = new VerticalLayout(reportNameLabel, overviewUpdateBar, commentList);
        innerPanel.setPadding(true);
        innerPanel.setWidth(100, Unit.PERCENTAGE);
        add(innerPanel);


        HorizontalLayout reviewAttachmentHorizontalLayout = new HorizontalLayout();
        commentRichTextEditor = new RichTextEditor("Initial value ");
        //TODO this is pro editor. If I use
        reviewAttachmentHorizontalLayout.add(commentRichTextEditor);


        VerticalLayout reviewAttachmentParentVerticalLayout = new VerticalLayout();
        reviewAttachmentParentVerticalLayout.add(reviewAttachmentHorizontalLayout);


        add(reviewAttachmentParentVerticalLayout);

    }

    private void setReport(Report report){
        this.report = report;
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(), report.getVersion() != null ? report.getVersion().getVersion() : "No version");
        overviewUpdateBar.setProjectVersions(projectService.getProjectVersions(report.getProject()));
        overviewUpdateBar.setReporters(userService.getUsers());
        overviewUpdateBar.setStatus(report.getStatus());
        overviewUpdateBar.setReporter(report.getAuthor());
        overviewUpdateBar.setType(report.getType());
        overviewUpdateBar.setPriority(report.getPriority());
        overviewUpdateBar.setListener((priority, type, status, reporter, version) -> {
            //TODO do what ??
        });
        reportNameLabel.setText(report.getSummary());

        commentList.setComments(commentService.getGroupedComments(report));
        commentRichTextEditor.setValue(report.getDescription());
    }

    @Override
    public void setParameter(BeforeEvent event, Long reportId) {
        Report report = reportService.getReport(reportId);
        if(report != null){
            setReport(report);
        }else{
            //SHOW SOME DESCRIPTION TO USER IF NULL.
        }
        System.out.println(reportId);


    }
}
