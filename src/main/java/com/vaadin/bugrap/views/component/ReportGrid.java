package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.utils.DateUtils;
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

    private void initializeColumns(){
        versionColumn = addColumn(createReportVersionComponentRenderer()).setHeader("Version");
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

        priorityColumn = addColumn(createPriorityComponentRenderer()).setHeader("Priority");
        priorityColumn.setComparator(Comparator.comparing(Report::getPriority));


        addColumn(Report::getType).setHeader("Type").setComparator(Comparator.comparing(Report::getType));
        addColumn(Report::getSummary).setHeader("Summary").setComparator(Comparator.comparing(Report::getSummary));
        addColumn(createAssigneeComponentRenderer()).setHeader("Assigned to").setComparator((o1, o2) -> {
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
        addColumn(createReportLastModifiedComponentRenderer()).setHeader("Last modified").setComparator(Comparator.comparing(Report::getTimestamp));
        addColumn(createReportTimeStampComponentRenderer()).setHeader("Reported").setComparator(Comparator.comparing(Report::getReportedTimestamp));

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
        sort(sortOrderList);
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
