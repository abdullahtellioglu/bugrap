package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.Report;

public class PriorityBar extends HorizontalLayout {
    public PriorityBar(Report.Priority priority){
        setClassName("priority-bar");
        setPriority(priority);
        setSpacing(false);
        setPadding(false);
        setMargin(false);
    }
    public void setPriority(Report.Priority priority){
        removeAll();
        int ordinal = priority.ordinal() + 1;
        for (int i = 0; i < ordinal; i++) {
            Div div = new Div();
            div.setClassName("priority-cell");

            add(div);
        }
    }
}
