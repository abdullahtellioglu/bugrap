package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.bugrap.domain.entities.Project;

import java.util.List;

@UIScope
@SpringComponent
public class ChartDialog extends Dialog {
    private final Chart chart;
    private final ProjectService projectService;
    private final ReportService reportService;

    public ChartDialog(ProjectService projectService, ReportService reportService) {
        super();
        this.projectService = projectService;
        this.reportService = reportService;

        chart = new Chart(ChartType.COLUMN);

        Configuration configuration = chart.getConfiguration();
        configuration.setTitle("Project Reports");

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Report Information");
        configuration.addyAxis(y);

        XAxis xAxis = new XAxis();
        xAxis.setCrosshair(new Crosshair());
        xAxis.setTitle("Projects");
        configuration.addxAxis(xAxis);

        setWidth(80, Unit.PERCENTAGE);
        setMinHeight(50, Unit.PERCENTAGE);
    }

    public void initializeData() {
        List<Project> activeProjects = projectService.getActiveProjects();
        chart.getParent().ifPresent(parent -> {
            if (parent.equals(ChartDialog.this)) {
                remove(chart);
            }
        });

        Configuration configuration = chart.getConfiguration();

        XAxis xAxis = configuration.getxAxis();
        xAxis.setCategories(activeProjects.stream().map(Project::getName).toArray(String[]::new));

        ListSeries openListSeries = new ListSeries("Open");
        ListSeries closedListSeries = new ListSeries("Closed");
        ListSeries unAssignedListSeries = new ListSeries("Un-assigned");
        for (Project project : activeProjects) {
            long countOpenedReports = reportService.getCountOpenedReports(project);
            long countClosedReports = reportService.getCountClosedReports(project);
            long countUnAssignedReports = reportService.getCountUnAssignedReports(project);

            openListSeries.addData(countOpenedReports);
            closedListSeries.addData(countClosedReports);
            unAssignedListSeries.addData(countUnAssignedReports);

        }
        configuration.setSeries(openListSeries, closedListSeries, unAssignedListSeries);
        add(chart);
    }
}
