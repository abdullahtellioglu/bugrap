package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.utils.DateUtils;
import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.CellFocusEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.*;

public class ReportGrid extends Grid<Report> {

    private Column<Report> versionColumn;
    private Column<Report> priorityColumn;

    public ReportGrid(){
        setSelectionMode(SelectionMode.MULTI);
        setMultiSort(true);
        //below listener is a listener for move between rows.
        addCellFocusListener((ComponentEventListener<CellFocusEvent<Report>>) event -> {
            if(event.getColumn().isEmpty()){
                //for selection checkboxes

                return;
            }

            Set<Report> selectedItems = getSelectionModel().getSelectedItems();
            if(selectedItems.size() > 1){
                return;
            }

            event.getItem().ifPresent(report -> {
                if(selectedItems.iterator().hasNext()){
                    Report current = selectedItems.iterator().next();
                    if(current.getId() != report.getId()){
                        deselect(current);
                    }
                }
                select(report);
            });

        });

        initializeColumns();
    }
    public void setColumns(Set<GridColumn> gridColumns){
        getColumns().forEach(column -> {
            column.getId().ifPresent(columnId -> {
                GridColumn foundGridColumn = GridColumn.find(columnId);
                if(foundGridColumn != null && foundGridColumn.isChangeable()){
                    column.setVisible(gridColumns.contains(foundGridColumn));
                }
            });
        });
    }

    private void initializeColumns(){
        versionColumn = addColumn(createReportVersionComponentRenderer()).setHeader(GridColumn.VERSION.getLabel());
        versionColumn.setId(GridColumn.VERSION.name());
        versionColumn.setComparator((o1, o2) -> {
            if(o1.getVersion() == null && o2.getVersion() != null){
                return 1;
            }
            if(o1.getVersion() != null && o2.getVersion() == null){
                return -1;
            }
            if(o1.getVersion() != null && o2.getVersion() != null){
                return o1.getVersion().getVersion().compareTo(o2.getVersion().getVersion());
            }
            return 0;
        });

        priorityColumn = addColumn(createPriorityComponentRenderer()).setHeader(GridColumn.PRIORITY.getLabel());
        priorityColumn.setId(GridColumn.PRIORITY.name());
        priorityColumn.setComparator(Comparator.comparing(Report::getPriority));


        Column<Report> type = addColumn(Report::getType).setHeader(GridColumn.TYPE.getLabel());
        type.setId(GridColumn.TYPE.name());
        type.setComparator(Comparator.comparing(Report::getType));

        Column<Report> statusColumn = addColumn(Report::getStatus).setHeader(GridColumn.STATUS.getLabel());
        statusColumn.setId(GridColumn.STATUS.name());
        statusColumn.setComparator(Comparator.comparing(Report::getStatus));

        Column<Report> reportColumn = addColumn(Report::getSummary).setHeader(GridColumn.SUMMARY.getLabel());
        reportColumn.setId(GridColumn.SUMMARY.name());
        reportColumn.setComparator(Comparator.comparing(Report::getSummary));

        Column<Report> assignedToColumn = addColumn(createAssigneeComponentRenderer()).setHeader(GridColumn.ASSIGNED_TO.getLabel());
        assignedToColumn.setId(GridColumn.ASSIGNED_TO.name());
        assignedToColumn.setComparator((o1, o2) -> {
            if(o1.getAssigned() == null && o2.getAssigned() != null){
                return 1;
            }
            if(o1.getAssigned() != null && o2.getAssigned() == null){
                return -1;
            }
            if(o1.getAssigned() != null && o2.getAssigned() != null){
                return o1.getAssigned().getName().compareTo(o2.getAssigned().getName());
            }
            return 0;
        });

        Column<Report> lastModifiedColumn = addColumn(createReportLastModifiedComponentRenderer()).setHeader(GridColumn.LAST_MODIFIED.getLabel());
        lastModifiedColumn.setId(GridColumn.LAST_MODIFIED.name());
        lastModifiedColumn.setComparator(Comparator.comparing(Report::getTimestamp));


        Column<Report> reportedColumn = addColumn(createReportTimeStampComponentRenderer()).setHeader(GridColumn.REPORTED.getLabel());
        reportedColumn.setId(GridColumn.REPORTED.name());
        reportedColumn.setComparator(Comparator.comparing(Report::getReportedTimestamp));

        //set visibility of columns initially.
        getColumns().forEach(column -> column.getId().ifPresent(columnId -> {
            GridColumn foundCol = GridColumn.find(columnId);
            column.setVisible(foundCol.isInitialVisible());
        }));
    }
    public void createGridColumns(boolean allVersionSelected){
        List<GridSortOrder<Report>> sortOrderList = new ArrayList<>();
        if(allVersionSelected){
            versionColumn.setVisible(true);
            GridSortOrder<Report> versionOrder = new GridSortOrder<>(versionColumn, SortDirection.ASCENDING);
            sortOrderList.add(versionOrder);
        }else{
            versionColumn.setVisible(false);
        }
        GridSortOrder<Report> priorityOrder = new GridSortOrder<>(priorityColumn, SortDirection.DESCENDING);
        sortOrderList.add(priorityOrder);
        updateSelectionModeOnClient();
        sort(sortOrderList);
        //TODO Bug if column visibility set to false, sort indicator remains the same.
    }

    private static ComponentRenderer<Component, Report> createReportVersionComponentRenderer(){
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if (report != null && report.getVersion() != null) {
                return new Span(report.getVersion().getVersion());
            }
            return new Span();
        });
    }

    private static ComponentRenderer<Component, Report> createReportLastModifiedComponentRenderer(){
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            //is timestamp correct ?
            Date timestamp = report.getTimestamp();
            return new Span(DateUtils.getRelativeFormat(timestamp));
        });
    }
    private static ComponentRenderer<Component, Report> createReportTimeStampComponentRenderer(){
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            Date reportedTimestamp = report.getReportedTimestamp();
            return new Span(DateUtils.getRelativeFormat(reportedTimestamp));
        });
    }

    private static ComponentRenderer<Component, Report> createAssigneeComponentRenderer(){
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if (report != null && report.getAssigned() != null && report.getAssigned().getName() != null) {
                return new Span(report.getAssigned().getName());
            }
            return new Span();
        });
    }

    private static ComponentRenderer<Component, Report> createPriorityComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if(report.getPriority() != null){
                return new PriorityBar(report.getPriority());
            }
            return null;

        });
    }
}
