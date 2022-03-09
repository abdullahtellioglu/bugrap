package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.Report;

/**
 * Creates a kinda progress bar for given priority. It creates progress count based on {@link Report.Priority}'s ordinal.
 */
public class PriorityBar extends HorizontalLayout {
    public PriorityBar(Report.Priority priority){
        setClassName("priority-bar");
        setSpacing(false);
        setPadding(false);
        setMargin(false);

        setPriority(priority);
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
