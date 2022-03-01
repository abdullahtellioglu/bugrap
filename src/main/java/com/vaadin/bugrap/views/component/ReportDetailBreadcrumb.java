package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ReportDetailBreadcrumb extends HorizontalLayout {
    private final Label reportNameLabel;
    private final Label versionLabel;

    public ReportDetailBreadcrumb(){
        setClassName("report-detail-breadcrumb");
        setJustifyContentMode(JustifyContentMode.START);
        setWidth(100, Unit.PERCENTAGE);
        setPadding(false);
        Div div = new Div();
        div.setWidth(50, Unit.PERCENTAGE);

        div.setClassName("breadcrumb-label");
        reportNameLabel = new Label();

        versionLabel = new Label();

        div.add(reportNameLabel);
        add(div, versionLabel);
    }
    public void setReportNameAndVersionName(String reportName, String versionName){
        reportNameLabel.setText(reportName);
        versionLabel.setText(versionName);
    }
}
