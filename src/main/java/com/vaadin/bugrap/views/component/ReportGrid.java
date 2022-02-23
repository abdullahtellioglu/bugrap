package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.utils.DateUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Comparator;
import java.util.Date;

public class ReportGrid extends Grid<Report> {

    public ReportGrid(){
        setSelectionMode(SelectionMode.MULTI);
        addColumn(createPriorityComponentRenderer()).setHeader("Priority").setComparator(Comparator.comparing(Report::getPriority));
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
        //TODO last modified and reported time ? There is no updatetimestamp. Instead I used getTimeStamp
        addColumn(createReportLastModifiedComponentRenderer()).setHeader("Last modified").setComparator(Comparator.comparing(Report::getTimestamp));
        addColumn(createReportTimeStampComponentRenderer()).setHeader("Reported").setComparator(Comparator.comparing(Report::getReportedTimestamp));
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
