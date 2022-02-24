package com.vaadin.bugrap.views.component.overview;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.Set;

public class ReportsOverviewLayout extends VerticalLayout {
    private Set<Report> reports;
    boolean massModificationModeOn;
    private final HorizontalLayout labelContainer = new HorizontalLayout();

    public ReportsOverviewLayout(){
        add(labelContainer);
    }
    public void setReports(Set<Report> reports){
        this.reports = reports;
        initLayout();
    }

    private void initLayout(){

        if(reports.size() > 1){
            initMassModificationMode();
        }else{
            initSingleModificationMode();
        }
    }
    private void initSingleModificationMode(){
        massModificationModeOn = false;
        Report report = reports.iterator().next();
        labelContainer.removeAll();

        Label label = new Label(report.getSummary());
        labelContainer.add(label);
    }
    private void initMassModificationMode(){
        massModificationModeOn = true;
        labelContainer.removeAll();
        Label selectedItemCount = new Label(String.format("%s items selected", reports.size()));
        Label infoLabel = new Label("Select a single report to view contents");
        labelContainer.add(selectedItemCount, infoLabel);

    }
}
