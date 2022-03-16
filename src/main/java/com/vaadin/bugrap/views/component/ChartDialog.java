package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UIScope
@SpringComponent
public class ChartDialog extends Dialog {
    private final Chart taskChart;
    private final Chart statusChart;
    private final Tab taskInfoTab;
    private final Tab statusInfoTab;
    private final VerticalLayout content;
    private final ProjectService projectService;
    private final ReportService reportService;

    public ChartDialog(ProjectService projectService, ReportService reportService) {
        super();
        this.projectService = projectService;
        this.reportService = reportService;

        taskChart = new Chart(ChartType.COLUMN);
        statusChart = new Chart(ChartType.COLUMN);

        content = new VerticalLayout();
        content.setSpacing(false);

        taskInfoTab = new Tab("Tasks Information");
        statusInfoTab = new Tab("Status Information");

        Tabs tabs = new Tabs(taskInfoTab, statusInfoTab);

        add(tabs);
        add(content);

        content.add(taskChart);

        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab().equals(taskInfoTab)) {
                content.removeAll();
                content.add(taskChart);
            } else if (event.getSelectedTab().equals(statusInfoTab)) {
                content.removeAll();
                content.add(statusChart);
            }
        });

        setWidth(80, Unit.PERCENTAGE);
        setMinHeight(50, Unit.PERCENTAGE);
        initializeTaskChart();
        initializeStatusChart();

    }

    public void initializeStatusChart() {
        Configuration configuration = statusChart.getConfiguration();

        Tooltip statusChartTooltip = new Tooltip();
        statusChartTooltip.setShared(true);
        configuration.setTooltip(statusChartTooltip);

        List<Project> activeProjects = projectService.getActiveProjects();
        XAxis xAxis = configuration.getxAxis();
        xAxis.setCategories(activeProjects.stream().map(Project::getName).toArray(String[]::new));
        Map<Report.Status, ListSeries> statusListSeriesMap = new LinkedHashMap<>();
        for (Report.Status status : Report.Status.values()) {
            statusListSeriesMap.put(status, new ListSeries(status.toString()));
        }
        activeProjects.forEach(project -> {
            BugrapRepository.ReportsQuery reportsQuery = new BugrapRepository.ReportsQuery();
            reportsQuery.project = project;
            List<Report> reports = reportService.findReports(reportsQuery);
            Map<Report.Status, List<Report>> groupedByStatus = reports.stream().filter(f -> f.getStatus() != null)
                    .collect(Collectors.groupingBy(Report::getStatus));

            groupedByStatus.forEach((status, groupReports) -> statusListSeriesMap.get(status).addData(groupReports.size()));
        });
        configuration.setSeries(new ArrayList<>(statusListSeriesMap.values()));
    }

    private void initializeTaskChart() {
        Configuration configuration = taskChart.getConfiguration();

        PlotOptionsColumn plotOptionsColumn = new PlotOptionsColumn();
        plotOptionsColumn.setStacking(Stacking.PERCENT);
        configuration.setPlotOptions(plotOptionsColumn);

        Tooltip taskChartTooltip = new Tooltip();
        taskChartTooltip.setShared(true);
        configuration.setTooltip(taskChartTooltip);

        List<Project> activeProjects = projectService.getActiveProjects();

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
    }
}
