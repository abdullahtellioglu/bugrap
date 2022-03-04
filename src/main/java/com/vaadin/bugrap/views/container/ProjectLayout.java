package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.*;
import com.vaadin.bugrap.views.component.overview.ReportsOverviewLayout;
import com.vaadin.bugrap.views.pages.ReportDetailPage;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.RouteConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProjectLayout extends VerticalLayout {
    //services
    private final ProjectService projectService;
    private final ReportService reportService;

    private Project project;
    private List<ProjectVersion> projectVersions;
    private ProjectVersion projectVersion;
    private List<Report> reports = new ArrayList<>();
    private Reporter currentUser;

    long closedReportCount = 0;
    long openedReportCount = 0;
    long unAssignedReportCount = 0;

    //Views
    private ReportGrid reportGrid;
    private DistributionBar distributionBar;
    private ProjectVersionSelect projectVersionSelect;
    private final ProjectToolbarLayout projectToolbarLayout;
    private final SplitLayout gridSplitLayout;
    private final ReportsOverviewLayout reportsOverviewLayout = new ReportsOverviewLayout();
    private final BugrapRepository.ReportsQuery query = new BugrapRepository.ReportsQuery();
    private String textSearchQuery;


    private Set<Report> selectedReports = new HashSet<>();

    public ProjectLayout(Reporter currentUser){
        this.projectService = new ProjectService();
        this.reportService = new ReportService();
        this.currentUser = currentUser;

        UI.getCurrent().addShortcutListener((ShortcutEventListener) event -> {
            if(selectedReports.size() == 1){
                openReportInNewTab(selectedReports.iterator().next());
            }
        }, Key.ENTER, KeyModifier.CONTROL);

        query.reportAssignee = currentUser;
        query.reportStatuses = Collections.singleton(Report.Status.OPEN);

        projectToolbarLayout = new ProjectToolbarLayout();

        projectToolbarLayout.setSearchTextChangeListener(textQuery -> {
            textSearchQuery = textQuery;
            onReportQueryChanged();
        });
        setClassName("project-layout");
        add(projectToolbarLayout);

        ReportStatusLayout reportStatusLayout = new ReportStatusLayout();
        reportStatusLayout.setAssigneeChangeListener(reporter -> {
            query.reportAssignee = reporter;
            onReportQueryChanged();
        });
        reportStatusLayout.setStatusChangeListener(statuses -> {
            query.reportStatuses = statuses;
            onReportQueryChanged();
        });


        VerticalLayout gridDistributionContainerLayout = new VerticalLayout();
        gridDistributionContainerLayout.setClassName("inner-panel");
        gridDistributionContainerLayout.setHeight(100, Unit.PERCENTAGE);
        gridDistributionContainerLayout.setPadding(true);
        gridDistributionContainerLayout.setMargin(false);

        initReportVersionsAndDistributionBar(gridDistributionContainerLayout);


        gridDistributionContainerLayout.add(reportStatusLayout);

        reportsOverviewLayout.setReportUpdateListener(updatedReports -> {
            onReportQueryChanged();

            reports.forEach(report -> {
                updatedReports.stream().filter(updatedReport -> report.getId() == updatedReport.getId()).findFirst().ifPresent(u -> {
                    reportGrid.getSelectionModel().select(report);
                });
            });

//            onSelectedReportsChanged(new HashSet<>());
        });



        reportGrid = new ReportGrid();
        reportGrid.createGridColumns(projectVersion == null|| projectVersion.getId() == -1);
        reportGrid.setItems(reports);
        reportGrid.addSelectionListener((SelectionListener<Grid<Report>, Report>) event -> {
            onSelectedReportsChanged(event.getAllSelectedItems());
        });
        reportGrid.addItemClickListener((ComponentEventListener<ItemClickEvent<Report>>) event -> {
            reportGrid.getSelectionModel().deselectAll();
            reportGrid.getSelectionModel().select(event.getItem());
        });
        reportGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Report>>) event -> {
            openReportInNewTab(event.getItem());
        });

        gridSplitLayout = new SplitLayout(reportGrid, reportsOverviewLayout);
        gridSplitLayout.setClassName("secondary-hidden");
        gridSplitLayout.setWidth(100, Unit.PERCENTAGE);
        gridSplitLayout.setSplitterPosition(100);
        gridSplitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        gridDistributionContainerLayout.add(gridSplitLayout);



        add(gridDistributionContainerLayout);

    }

    private void openReportInNewTab(Report report){
        RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
        String url = routeConfiguration.getUrl(ReportDetailPage.class, report.getId());
        getUI().ifPresent(ui -> ui.getPage().open(url, "_blank"));
    }


    /**
     * Fetches reports and filters by search query if exists.<br/>
     * Fill the grid with selected items.
     */
    private void onReportQueryChanged(){
        List<Report> reports = reportService.findReports(query);
        if(StringUtils.isNotEmpty(textSearchQuery)){
            reports = reports.stream().filter(report ->
                    StringUtils.containsIgnoreCase(report.getSummary(), textSearchQuery) ||
                            StringUtils.containsIgnoreCase(report.getDescription(), textSearchQuery)).collect(Collectors.toList());
        }
        this.reports = reports;
        reportGrid.setItems(reports);
    }

    public void setProject(Project project){
        this.project = project;
        query.project = project;

        onReportQueryChanged();
        fetchProjectVersions();
        fetchReportCounts();

        projectVersionSelect.setItems(projectVersions);
        projectVersionSelect.setValue(projectVersion);
    }

    private void fetchProjectVersions(){
        projectVersions = projectService.getProjectVersions(project);
        if(projectVersions.size() > 1){
            ProjectVersion allProjectVersion = new ProjectVersion();
            allProjectVersion.setId(-1);
            allProjectVersion.setProject(project);
            allProjectVersion.setVersion("All versions");
            projectVersions.add(0, allProjectVersion);
            this.projectVersion = allProjectVersion;
            int lastSelectedProjectVersion = CookieUtils.getLastSelectedProjectVersion(project, RequestUtils.getCurrentHttpRequest());
            projectVersions.stream().filter(pv -> pv.getId() == lastSelectedProjectVersion).findFirst()
                    .ifPresent(cookieVersion -> this.projectVersion = cookieVersion);

            return;
        }
        if(projectVersions.size() == 1){
            this.projectVersion = projectVersions.get(0);
        }
    }

    private void onSelectedReportsChanged(Set<Report> selectedReports){
        this.selectedReports = selectedReports;

        if(selectedReports.isEmpty()){
            gridSplitLayout.setClassName("secondary-hidden");
            gridSplitLayout.setSplitterPosition(100);
        }else{
            reportsOverviewLayout.setReportsAndReporter(selectedReports, currentUser);
            gridSplitLayout.removeClassName("secondary-hidden");
            int heightPercentage;
            if(selectedReports.size() > 1){
                heightPercentage = 75;
            }else{
                heightPercentage = Math.min(40 + reports.size(), 60);
            }
            //TODO is this correct way to implement the row size ?
            gridSplitLayout.setSplitterPosition(heightPercentage);
        }
    }

    private void initReportVersionsAndDistributionBar(VerticalLayout parentComponent){
        projectVersionSelect = new ProjectVersionSelect();

        projectVersionSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<ProjectVersion>, ProjectVersion>>) event -> {
            ProjectVersion value = event.getValue();
            if(value == null){
                return;
            }
            this.projectVersion = value;
            fetchReportCounts();
            onProjectVersionChange(value);
        });


        distributionBar = new DistributionBar(closedReportCount, openedReportCount, unAssignedReportCount);

        HorizontalLayout reportDistributionLayout = new HorizontalLayout();
        reportDistributionLayout.setWidth(100, Unit.PERCENTAGE);
        reportDistributionLayout.setAlignItems(Alignment.CENTER);
        reportDistributionLayout.setJustifyContentMode(JustifyContentMode.START);
        reportDistributionLayout.add(projectVersionSelect);
        reportDistributionLayout.addAndExpand(distributionBar);

        parentComponent.add(reportDistributionLayout);
    }

    private void fetchReportCounts(){
        if(projectVersion == null || projectVersion.getId() == -1){
            closedReportCount = reportService.getCountClosedReports(project);
            openedReportCount = reportService.getCountOpenedReports(project);
            unAssignedReportCount = reportService.getCountUnAssignedReports(project);
        }else{
            closedReportCount = reportService.getCountClosedReports(projectVersion);
            openedReportCount = reportService.getCountOpenedReports(projectVersion);
            unAssignedReportCount = reportService.getCountUnAssignedReports(projectVersion);
        }
    }
    private void onProjectVersionChange(ProjectVersion projectVersion){
        if(projectVersion.getId() == -1){
            query.projectVersion = null;
            reportGrid.createGridColumns(true);
        }else{
            query.projectVersion = projectVersion;
            reportGrid.createGridColumns(false);
        }
        onReportQueryChanged();
        CookieUtils.addLastSelectedProjectVersion(project, projectVersion, RequestUtils.getCurrentHttpResponse());

        distributionBar.setValues(closedReportCount, openedReportCount, unAssignedReportCount);
    }


    public void setOpenedReportCount(long reportCount) {
        projectToolbarLayout.setOpenedReportCount(reportCount);
    }
}
