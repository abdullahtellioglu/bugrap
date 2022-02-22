package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.grid.Grid;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Comparator;

public class ReportGrid extends Grid<Report> {

    public ReportGrid(){
        setSelectionMode(SelectionMode.MULTI);
        addColumn(Report::getPriority).setHeader("Priority").setComparator(Comparator.comparing(Report::getPriority));
        addColumn(Report::getType).setHeader("Type");
        addColumn(Report::getSummary).setHeader("Summary");

    }
}
