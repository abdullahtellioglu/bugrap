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

        reportNameSpan = new Span();

        versionSpan = new Span();
        VerticalLayout versionLabelLayout = new VerticalLayout();
        versionLabelLayout.setPadding(true);
        versionLabelLayout.setJustifyContentMode(JustifyContentMode.START);
        versionLabelLayout.add(versionSpan);

        Div div = new Div();
        div.setClassName("breadcrumb-label");
        div.add(reportNameSpan);

        add(div, versionLabelLayout);

        setClassName("report-detail-breadcrumb");
        setJustifyContentMode(JustifyContentMode.START);
        setWidth(100, Unit.PERCENTAGE);
        setPadding(false);
    }
    public void setReportNameAndVersionName(String reportName, String versionName){
        reportNameSpan.setText(reportName);
        versionSpan.setText(versionName);
    }
}
