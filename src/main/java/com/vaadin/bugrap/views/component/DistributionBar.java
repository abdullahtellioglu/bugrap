package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;
import java.util.stream.Collectors;

//@Tag("distribution-bar")
public class DistributionBar extends HorizontalLayout {
    private final Div closedChunkDiv;
    private final Div assignedChunkDiv;
    private final Div unAssignedChunkDiv;

    public DistributionBar(){
        setSpacing(false);
        setClassName("distribution-bar");
        closedChunkDiv = createChunk( "closed");
        assignedChunkDiv = createChunk( "assigned");
        unAssignedChunkDiv = createChunk( "un-assigned");
        setDefaultVerticalComponentAlignment(Alignment.START);
    }
    public DistributionBar(long closed, long assigned, long unAssigned){
        this();
        updateChunk(closed, closedChunkDiv);
        updateChunk(assigned, assignedChunkDiv);
        updateChunk(unAssigned, unAssignedChunkDiv);
    }

    private Div createChunk( String className){
        Div chunk = new Div();
        chunk.setClassName(className);

        Label label = new Label();
        chunk.add(label);
        add(chunk);

        updateChunk(0, chunk);
        return chunk;
    }
    public void setClosedValue(long value){
        updateChunk(value, closedChunkDiv);
    }
    public void setAssignedValue(long value){
        updateChunk(value, assignedChunkDiv);
    }
    public void setUnAssignedValue(long value){
        updateChunk(value, unAssignedChunkDiv);
    }
    private void updateChunk(long value, Div chunk){

        chunk.setMinWidth(value * 30, Unit.PIXELS);
        chunk.removeClassNames("has-value", "no-value");
        chunk.addClassName(value > 0 ? "has-value" : "no-value");
        chunk.getChildren().filter( child -> child instanceof Label).findFirst().ifPresent(label -> ((Label) label).setText(String.valueOf(value)));
    }
}
