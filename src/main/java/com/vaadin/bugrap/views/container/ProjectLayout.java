package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.config.ContextWrapper;
import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.*;
import com.vaadin.bugrap.views.component.overview.ReportsOverviewLayout;
import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.bugrap.views.pages.ReportDetailPage;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
import java.util.stream.Collectors;

public class ProjectLayout extends VerticalLayout {
    private static final String SPLITTER_HIDDEN_CLASS = "secondary-hidden";
    //services
    private final ProjectService projectService;
    private final ReportService reportService;

    private Project project;
    private List<ProjectVersion> projectVersions;
    private ProjectVersion projectVersion;
    private List<Report> reports = new ArrayList<>();
    private final Reporter currentUser;

    long closedReportCount = 0;
    long openedReportCount = 0;
    long unAssignedReportCount = 0;

    //Views
    private final ReportGrid reportGrid;
    private DistributionBar distributionBar;
    private ProjectVersionComboBox projectVersionComboBox;
    private final ProjectToolbarLayout projectToolbarLayout;
    private final SplitLayout gridSplitLayout;
    private final ReportsOverviewLayout reportsOverviewLayout;
    private final BugrapRepository.ReportsQuery query = new BugrapRepository.ReportsQuery();
    private String textSearchQuery;

    private final Set<GridColumn> gridColumnSet = new HashSet<>();

    private Set<Report> selectedReports = new HashSet<>();

    public ProjectLayout(Reporter currentUser){
        this.projectService = ContextWrapper.getBean(ProjectService.class);
        this.reportService = ContextWrapper.getBean(ReportService.class);
        this.currentUser = currentUser;

        query.reportAssignee = currentUser;
        query.reportStatuses = Collections.singleton(Report.Status.OPEN);
        reportsOverviewLayout = new ReportsOverviewLayout();
        projectToolbarLayout = new ProjectToolbarLayout();


        VerticalLayout gridDistributionContainerLayout = new VerticalLayout();
        gridDistributionContainerLayout.setClassName("inner-panel");
        gridDistributionContainerLayout.setHeight(100, Unit.PERCENTAGE);
        gridDistributionContainerLayout.setPadding(true);
        gridDistributionContainerLayout.setMargin(false);

        initReportVersionsAndDistributionBar(gridDistributionContainerLayout);

        ReportStatusLayout reportStatusLayout = new ReportStatusLayout();
        gridDistributionContainerLayout.add(reportStatusLayout);
        reportStatusLayout.setSelectedGridColumns(gridColumnSet);

        Arrays.stream(GridColumn.values()).forEach(gridColumn -> {
            if(gridColumn.isInitialVisible()){
                gridColumnSet.add(gridColumn);
            }
        });
        reportStatusLayout.updateGridColumns();

        reportGrid = new ReportGrid();
        reportGrid.createGridColumns(projectVersion == null|| projectVersion.getId() == -1);
        reportGrid.setItems(reports);

        gridSplitLayout = new SplitLayout(reportGrid, new Scroller(reportsOverviewLayout));
        gridSplitLayout.setClassName(SPLITTER_HIDDEN_CLASS);
        gridSplitLayout.setWidth(100, Unit.PERCENTAGE);
        gridSplitLayout.setSplitterPosition(100);
        gridSplitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        gridDistributionContainerLayout.add(gridSplitLayout);

        add(projectToolbarLayout);
        add(gridDistributionContainerLayout);
        setClassName("project-layout");

        initializeEvents(reportStatusLayout);



    }
    private void initializeEvents(ReportStatusLayout reportStatusLayout){
        //events
        UI.getCurrent().addShortcutListener((ShortcutEventListener) event -> {
            if(selectedReports.size() == 1){
                openReportInNewTab(selectedReports.iterator().next());
            }
        }, Key.ENTER, KeyModifier.CONTROL);

        reportStatusLayout.setAssigneeChangeListener(reporter -> {
            query.reportAssignee = reporter;
            onReportQueryChanged();
        });
        reportStatusLayout.setStatusChangeListener(statuses -> {
            query.reportStatuses = statuses;
            onReportQueryChanged();
        });

        reportGrid.addSelectionListener((SelectionListener<Grid<Report>, Report>) event -> onSelectedReportsChanged(event.getAllSelectedItems()));
        reportGrid.addItemClickListener((ComponentEventListener<ItemClickEvent<Report>>) event -> {
            reportGrid.getSelectionModel().deselectAll();
            reportGrid.getSelectionModel().select(event.getItem());
        });
        reportGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Report>>) event -> openReportInNewTab(event.getItem()));
        reportsOverviewLayout.setReportUpdateListener(updatedReports -> {
            onReportQueryChanged();

            reports.forEach(report -> updatedReports.stream().filter(updatedReport -> report.getId() == updatedReport.getId()).findFirst()
                    .ifPresent(u -> reportGrid.getSelectionModel().select(report)));
        });
        projectToolbarLayout.setSearchTextChangeListener(textQuery -> {
            textSearchQuery = textQuery;
            onReportQueryChanged();
        });


        reportStatusLayout.setGridColumnChangeListener(gridColumn -> {
            if(gridColumnSet.contains(gridColumn)){
                gridColumnSet.remove(gridColumn);
            }else{
                gridColumnSet.add(gridColumn);
            }
            reportStatusLayout.updateGridColumns();
            reportGrid.setColumns(gridColumnSet);

        });
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
        List<Report> foundReports = reportService.findReports(query);
        if(StringUtils.isNotEmpty(textSearchQuery)){
            foundReports = foundReports.stream().filter(report ->
                    StringUtils.containsIgnoreCase(report.getSummary(), textSearchQuery) ||
                            StringUtils.containsIgnoreCase(report.getDescription(), textSearchQuery)).collect(Collectors.toList());
        }
        this.reports = foundReports;
        reportGrid.setItems(foundReports);
    }

    public void setProject(Project project){
        this.project = project;
        query.project = project;

        onReportQueryChanged();
        fetchProjectVersions();
        fetchReportCounts();

        projectVersionComboBox.setItems(projectVersions);
        projectVersionComboBox.setValue(projectVersion);
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

        reportsOverviewLayout.setReportsAndReporter(selectedReports, currentUser);
        if(selectedReports.isEmpty()){
            gridSplitLayout.setClassName(SPLITTER_HIDDEN_CLASS);
            gridSplitLayout.setSplitterPosition(100);
        }else{
            gridSplitLayout.removeClassName(SPLITTER_HIDDEN_CLASS);
            int heightPercentage;
            if(selectedReports.size() > 1){
                heightPercentage = 80;
            }else{
                heightPercentage = Math.min(40 + reports.size(), 50);
            }
            //TODO is this correct way to implement the row size ?
            gridSplitLayout.setSplitterPosition(heightPercentage);
        }
    }

    /**
     * initialize and add project versions and distribution bar to given parent component.
     * @param parentComponent Parent component to add
     */
    private void initReportVersionsAndDistributionBar(VerticalLayout parentComponent){
        projectVersionComboBox = new ProjectVersionComboBox();

        projectVersionComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<ProjectVersion>, ProjectVersion>>) event -> {
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
        reportDistributionLayout.add(projectVersionComboBox);
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
