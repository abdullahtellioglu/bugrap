package com.vaadin.bugrap.views.pages;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.views.component.ReportDetailBreadcrumb;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;

@PageTitle("Report Detail")
@Route(value = "/report")
@RouteAlias(value = "/report")
public class ReportDetailPage extends VerticalLayout implements HasUrlParameter<Long> {
    private final ProjectService projectService;
    private final ReportService reportService;

    private final ReportDetailBreadcrumb reportDetailBreadcrumb;
    private Project project;
    private Report report;

    public ReportDetailPage(){
        projectService = new ProjectService();
        reportService = new ReportService();
        setSpacing(false);
        setPadding(false);
        setClassName("report-detail-page");

        reportDetailBreadcrumb = new ReportDetailBreadcrumb();

        add(reportDetailBreadcrumb);
    }

    private void setReport(Report report){
        this.report = report;
        reportDetailBreadcrumb.setReportNameAndVersionName(report.getSummary(), report.getVersion() != null ? report.getVersion().getVersion() : "No version");
    }

    @Override
    public void setParameter(BeforeEvent event, Long reportId) {
        Report report = reportService.getReport(reportId);
        if(report != null){
            setReport(report);
        }else{
            //SHOW SOME DESCRIPTION TO USER IF NULL.
            //TODO do what ??
        }
        System.out.println(reportId);


    }
}
