package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.utils.DateUtils;
import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.CellFocusEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.event.SortEvent;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;

/**
 * ReportGrid is a component to display Reports. All columns are defined in {@link GridColumn}, and columns visibility can be changed in {@link ReportStatusLayout}.<br/>
 * If user selects All Versions <i>the Version column</i> is shown otherwise it is hidden.<br/>
 */
public class ReportGrid extends Grid<Report> {

    private Column<Report> versionColumn;
    private Column<Report> priorityColumn;

    public ReportGrid() {
        setSelectionMode(SelectionMode.MULTI);
        setMultiSort(true);
        addCellFocusListener((ComponentEventListener<CellFocusEvent<Report>>) event -> {
            if (event.getColumn().isEmpty()) {
                //for selection checkboxes
                return;
            }
            Set<Report> selectedItems = getSelectionModel().getSelectedItems();
            if (selectedItems.size() > 1) {
                return;
            }
            event.getItem().ifPresent(report -> {
                if (selectedItems.iterator().hasNext()) {
                    Report current = selectedItems.iterator().next();
                    if (current.getId() != report.getId()) {
                        deselect(current);
                    }
                }
                select(report);
            });

        });


        initializeColumns();
    }

    private static ComponentRenderer<Component, Report> createReportVersionComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if (report != null && report.getVersion() != null) {
                return new Span(report.getVersion().getVersion());
            }
            return new Span();
        });
    }

    private static ComponentRenderer<Component, Report> createReportLastModifiedComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            //is timestamp correct ?
            Date timestamp = report.getTimestamp();
            return new Span(DateUtils.getRelativeFormat(timestamp));
        });
    }

    private static ComponentRenderer<Component, Report> createReportTimeStampComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            Date reportedTimestamp = report.getReportedTimestamp();
            return new Span(DateUtils.getRelativeFormat(reportedTimestamp));
        });
    }

    private static ComponentRenderer<Component, Report> createAssigneeComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if (report != null && report.getAssigned() != null && report.getAssigned().getName() != null) {
                return new Span(report.getAssigned().getName());
            }
            return new Span();
        });
    }

    private static ComponentRenderer<Component, Report> createPriorityComponentRenderer() {
        return new ComponentRenderer<>((SerializableFunction<Report, Component>) report -> {
            if (report.getPriority() != null) {
                return new PriorityBar(report.getPriority());
            }
            return null;

        });
    }

    public void setColumns(Set<GridColumn> gridColumns) {
        getColumns().forEach(column -> column.getId().ifPresent(columnId -> {
            GridColumn foundGridColumn = GridColumn.find(columnId);
            if (foundGridColumn != null && foundGridColumn.isChangeable()) {
                column.setVisible(gridColumns.contains(foundGridColumn));
            }
        }));
        boolean updateRequired = getSortOrder().stream().anyMatch(sortedColumn -> !sortedColumn.getSorted().isVisible());
        if (updateRequired) {
            List<GridSortOrder<Report>> previousSortOrder = getSortOrder();
            List<GridSortOrder<Report>> currentSortOrder = new ArrayList<>(previousSortOrder);
            currentSortOrder.removeIf(reportGridSortOrder -> !reportGridSortOrder.getSorted().isVisible());
            updateSorting(currentSortOrder);
        }
    }

    private void initializeColumns() {
        versionColumn = addColumn(createReportVersionComponentRenderer()).setHeader(GridColumn.VERSION.getLabel());
        versionColumn.setId(GridColumn.VERSION.name());
        versionColumn.setComparator(Comparator.nullsLast(Comparator.comparing(Report::getVersion, Comparator.nullsLast(Comparator.naturalOrder()))));

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
        assignedToColumn.setComparator(
                Comparator.comparing(a -> Optional.ofNullable(a.getAssigned()).map(Reporter::getName).orElse(null),
                        Comparator.nullsLast(Comparator.naturalOrder())));

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

    /**
     * If any version is selected or all version is selected, this method show/hide the version column and re-order all sorting operations.
     *
     * @param allVersionSelected true if all version is selected to display otherwise false
     */
    public void createGridColumns(boolean allVersionSelected) {
        List<GridSortOrder<Report>> sortOrderList = new ArrayList<>();
        if (allVersionSelected) {
            versionColumn.setVisible(true);
            GridSortOrder<Report> versionOrder = new GridSortOrder<>(versionColumn, SortDirection.ASCENDING);
            sortOrderList.add(versionOrder);
        } else {
            versionColumn.setVisible(false);
        }

        GridSortOrder<Report> priorityOrder = new GridSortOrder<>(priorityColumn, SortDirection.DESCENDING);
        sortOrderList.add(priorityOrder);

        updateSorting(sortOrderList);
    }

    /**
     * Updating the indicators
     *
     * @param sortOrderList list of sorted orders
     */
    private void updateSorting(List<GridSortOrder<Report>> sortOrderList) {
        clearSorting();
        List<QuerySortOrder> sortProperties = new ArrayList<>();
        sortOrderList.stream().map(
                order -> order.getSorted().getSortOrder(order.getDirection()))
                .forEach(s -> s.forEach(sortProperties::add));
        getDataCommunicator().setBackEndSorting(sortProperties);

        fireEvent(new SortEvent<>(this, new ArrayList<>(sortOrderList),
                false));
        sort(sortOrderList);

    }

    /**
     * Clears the current sorting.
     */
    public void clearSorting() {
        sort(new ArrayList<>());
    }
}
