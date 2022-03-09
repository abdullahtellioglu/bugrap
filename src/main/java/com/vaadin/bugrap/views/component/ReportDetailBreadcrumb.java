package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ReportDetailBreadcrumb extends HorizontalLayout {
    private final Span projectNameSpan;
    private final Span versionSpan;

    public ReportDetailBreadcrumb(){

        projectNameSpan = new Span();

        versionSpan = new Span();
        VerticalLayout versionLabelLayout = new VerticalLayout();
        versionLabelLayout.setPadding(true);
        versionLabelLayout.setJustifyContentMode(JustifyContentMode.START);
        versionLabelLayout.add(versionSpan);

        Div div = new Div();
        div.setClassName("breadcrumb-label");
        div.add(projectNameSpan);

        add(div, versionLabelLayout);

        setClassName("report-detail-breadcrumb");
        setJustifyContentMode(JustifyContentMode.START);
        setWidth(100, Unit.PERCENTAGE);
        setPadding(false);
    }
    public void setProjectName(String projectName){
        projectNameSpan.setText(projectName);
    }
    public void setVersionName(String versionName){
        versionSpan.setText(versionName);
    }
}
