package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.services.ReportService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportsOverviewLayout extends VerticalLayout implements OverviewUpdateBar.ReportsUpdateListener {
    private final ReportService reportService;

    private Set<Report> reports;
    boolean massModificationModeOn;
    private final HorizontalLayout reportInfoContainerLayout = new HorizontalLayout();
    private final OverviewUpdateBar overviewUpdateBar = new OverviewUpdateBar();
    private final Label primaryLabel;
    private final Label secondaryLabel;
    private final Label openInNewTabLabel;

    public ReportsOverviewLayout(){
        reportService = new ReportService();

        setClassName("reports-overview");
        primaryLabel = new Label();
        secondaryLabel = new Label();


        reportInfoContainerLayout.setWidth(100, Unit.PERCENTAGE);
        reportInfoContainerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        HorizontalLayout labelContainerLayout = new HorizontalLayout(primaryLabel, secondaryLabel);
        reportInfoContainerLayout.add(labelContainerLayout);
        openInNewTabLabel = new Label("Open");
        reportInfoContainerLayout.add(openInNewTabLabel);
        add(reportInfoContainerLayout);
        add(overviewUpdateBar);

    }
    public void setReports(Set<Report> reports){
        this.reports = reports;
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
        secondaryLabel.addClassName("hidden");
        openInNewTabLabel.removeClassName("hidden");
        Report report = reports.iterator().next();
        primaryLabel.setText(report.getSummary());

    }
    private void initMassModificationMode(){
        massModificationModeOn = true;
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
        //TODO check distinct is required
        Set<Reporter> reporterSet = reports.stream().map(Report::getAssigned).collect(Collectors.toSet());
        if(reporterSet.size() == 1){
            reporter = reporterSet.iterator().next();
        }

        Set<ProjectVersion> projectVersionSet = reports.stream().map(Report::getVersion).collect(Collectors.toSet());
        if(projectVersionSet.size() == 1){
            projectVersion = projectVersionSet.iterator().next();
        }

        overviewUpdateBar.setPriority(priority);
        overviewUpdateBar.setType(type);
        overviewUpdateBar.setStatus(status);
        overviewUpdateBar.setReporter(reporter);
        overviewUpdateBar.setVersion(projectVersion);

    }

    @Override
    public void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter assigned, ProjectVersion version) {
        //TODO check validation ??
        reports.forEach(report -> {
            report.setPriority(priority);
            report.setType(type);
            report.setStatus(status);
            report.setAssigned(assigned);
            report.setVersion(version);
            reportService.save(report);
        });
        Notification.show("Reports updated successfully!");
        //TODO invalidate grid.
    }
}
