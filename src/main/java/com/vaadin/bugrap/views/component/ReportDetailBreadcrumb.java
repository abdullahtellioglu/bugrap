package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportDetailBreadcrumb extends HorizontalLayout {
    private final Span reportNameSpan;
    private final Span versionSpan;

    public ReportDetailBreadcrumb(){
        setClassName("report-detail-breadcrumb");
        setJustifyContentMode(JustifyContentMode.START);
        setWidth(100, Unit.PERCENTAGE);
        setPadding(false);
        Div div = new Div();
        div.setWidth(50, Unit.PERCENTAGE);

        div.setClassName("breadcrumb-label");
        reportNameSpan = new Span();



        versionSpan = new Span();
        VerticalLayout versionLabelLayout = new VerticalLayout();
        versionLabelLayout.setWidth(50, Unit.PERCENTAGE);
        versionLabelLayout.setPadding(true);
        versionLabelLayout.setJustifyContentMode(JustifyContentMode.START);
        versionLabelLayout.add(versionSpan);

        div.add(reportNameSpan);

        add(div, versionLabelLayout);
    }
    public void setReportNameAndVersionName(String reportName, String versionName){
        reportNameSpan.setText(reportName);
        versionSpan.setText(versionName);
    }
}
