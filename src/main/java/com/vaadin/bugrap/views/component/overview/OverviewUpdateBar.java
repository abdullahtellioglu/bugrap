package com.vaadin.bugrap.views.component.overview;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import org.vaadin.bugrap.domain.entities.Report;

public class OverviewUpdateBar extends HorizontalLayout {
    private Select<Report.Priority> prioritySelect = new Select<>();
    public OverviewUpdateBar() {

    }
}
