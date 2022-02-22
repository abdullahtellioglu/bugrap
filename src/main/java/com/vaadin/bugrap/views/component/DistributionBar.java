package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

//@Tag("distribution-bar")
public class DistributionBar extends HorizontalLayout {

    public DistributionBar(int closed, int assigned, int unAssigned){
        setSpacing(false);
        setClassName("distribution-bar");
        createChunk(closed, "closed");
        createChunk(assigned, "assigned");
        createChunk(unAssigned, "un-assigned");
        setDefaultVerticalComponentAlignment(Alignment.START);

    }
    private void createChunk(int value, String className){
        if(value == 0){
            return;
        }
        Div chunk = new Div();
        //TODO fix max width
        chunk.setMinWidth(value * 30, Unit.PIXELS);
        chunk.setClassName(className);

        Label label = new Label(String.valueOf(value));
        chunk.add(label);
        add(chunk);

    }
}
