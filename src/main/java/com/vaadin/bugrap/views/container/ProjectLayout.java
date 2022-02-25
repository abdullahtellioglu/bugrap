package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.services.ProjectService;
import com.vaadin.bugrap.services.ReportService;
import com.vaadin.bugrap.utils.CookieUtils;
import com.vaadin.bugrap.utils.RequestUtils;
import com.vaadin.bugrap.views.component.*;
import com.vaadin.bugrap.views.component.overview.ReportsOverviewLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.selection.SelectionListener;
import org.vaadin.bugrap.domain.BugrapRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;

public class ProjectLayout extends VerticalLayout {
    //services
    private final ProjectService projectService;
    private final ReportService reportService;


    //entities
    private final Project project;
    private List<ProjectVersion> projectVersions;
    private ProjectVersion projectVersion;
    private List<Report> reports;
    private Reporter currentUser;

    long closedReportCount = 0;
    long openedReportCount = 0;
    long unAssignedReportCount = 0;

    //Views
    private ReportGrid reportGrid;
    private DistributionBar distributionBar;
    private ProjectVersionSelect projectVersionSelect;
    private ReportStatusLayout reportStatusLayout;
    private ProjectToolbarLayout projectToolbarLayout;
    private SplitLayout gridSplitLayout;
    private final ReportsOverviewLayout reportsOverviewLayout = new ReportsOverviewLayout();
    private final BugrapRepository.ReportsQuery query = new BugrapRepository.ReportsQuery();


    private Set<Report> selectedReports = new HashSet<>();

    public ProjectLayout(Project project, Reporter currentUser){
        this.project = project;
        this.projectService = new ProjectService();
        this.reportService = new ReportService();
        this.currentUser = currentUser;
        query.project = project;
        query.reportAssignee = currentUser;
        query.reportStatuses = Collections.singleton(Report.Status.OPEN);

        fetchProjectVersions();
        fetchReportCounts();


        projectToolbarLayout = new ProjectToolbarLayout();
        setClassName("project-layout");
        add(projectToolbarLayout);

        reportStatusLayout = new ReportStatusLayout();
        reportStatusLayout.setCurrentUser(currentUser);
        reportStatusLayout.setAssigneeChangeListener(reporter -> {
            query.reportAssignee = reporter;
            reports = reportService.findReports(query);
            reportGrid.setItems(reports);
        });
        reportStatusLayout.setStatusChangeListener(statuses -> {
            query.reportStatuses = statuses;
            reports = reportService.findReports(query);
            reportGrid.setItems(reports);
        });

        reports = reportService.findReports(query);



        VerticalLayout gridDistributionContainerLayout = new VerticalLayout();
        gridDistributionContainerLayout.setClassName("inner-panel");
        gridDistributionContainerLayout.setHeight(100, Unit.PERCENTAGE);
        gridDistributionContainerLayout.setPadding(true);
        gridDistributionContainerLayout.setMargin(false);

        initReportVersionsAndDistributionBar(gridDistributionContainerLayout);


        gridDistributionContainerLayout.add(reportStatusLayout);

        reportsOverviewLayout.setReportUpdateListener(updatedReports -> {
            //TODO do something with updated reports ?
            List<Report> reports = reportService.findReports(query);
            reportGrid.setItems(reports);
            onSelectedReportsChanged(new HashSet<>());
        });



        reportGrid = new ReportGrid();
        reportGrid.createGridColumns(projectVersion.getId() == -1);
        reportGrid.setItems(reports);
        reportGrid.addSelectionListener((SelectionListener<Grid<Report>, Report>) event -> {
            onSelectedReportsChanged(event.getAllSelectedItems());
        });
        reportGrid.addItemClickListener((ComponentEventListener<ItemClickEvent<Report>>) event -> {
            reportGrid.getSelectionModel().deselectAll();
            reportGrid.getSelectionModel().select(event.getItem());
            onSelectedReportsChanged(Collections.singleton(event.getItem()));
        });
        reportGrid.addItemDoubleClickListener((ComponentEventListener<ItemDoubleClickEvent<Report>>) event -> {
            //OPEN new tab.
            getUI().ifPresent(ui -> ui.getPage().open("/report/"+event.getItem().getId(), "_blank"));
        });

        gridSplitLayout = new SplitLayout(reportGrid, reportsOverviewLayout);
        gridSplitLayout.setClassName("secondary-hidden");
        gridSplitLayout.setWidth(100, Unit.PERCENTAGE);
        gridSplitLayout.setSplitterPosition(100);
        gridSplitLayout.setOrientation(SplitLayout.Orientation.VERTICAL);
        gridDistributionContainerLayout.add(gridSplitLayout);



        add(gridDistributionContainerLayout);

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
            reportsOverviewLayout.setReports(selectedReports);
            gridSplitLayout.removeClassName("secondary-hidden");
            gridSplitLayout.setSplitterPosition(70);
        }
    }

    private void initReportVersionsAndDistributionBar(VerticalLayout parentComponent){
        projectVersionSelect = new ProjectVersionSelect();
        projectVersionSelect.setItems(projectVersions);
        projectVersionSelect.setValue(projectVersion);

        projectVersionSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<ProjectVersion>, ProjectVersion>>) event -> {
            ProjectVersion value = event.getValue();
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

        CookieUtils.addLastSelectedProjectVersion(project, projectVersion, RequestUtils.getCurrentHttpResponse());

        List<Report> reports = reportService.findReports(query);
        reportGrid.setItems(reports);
        //TODO fix these values
        distributionBar.setValues(closedReportCount, openedReportCount, unAssignedReportCount);
    }
    public void setCurrentUser(Reporter user){
        this.currentUser = user;
    }


    public void setProjectCount(int projectCount) {
        projectToolbarLayout.setProjectCount(projectCount);
    }
}
