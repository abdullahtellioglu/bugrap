package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

/**
 * Distribution bar is a component that displays closed, assigned and unAssigned report counts.<br/>
 * Component decides the width of a given bar is based on total(=closed + assigned + unassigned). <b>Width = (value / total) * 100</b> <br/> <br/>
 * If given count is 0, it hides the progress instead of displaying 0.
 */
@Tag("distribution-bar")
@JsModule("./distribution-bar.ts")
public class DistributionBar extends Component implements FlexComponent {
    /**
     * Creates empty distribution bar.
     */
    public DistributionBar(){
        setAlignItems(Alignment.START);
    }

    /**
     * Create distribution bar with given numbers.
     * @param closed closed report count
     * @param assigned assigned report count
     * @param unAssigned unAssigned report count
     */
    public DistributionBar(long closed, long assigned, long unAssigned){
        this();
        setValues(closed, assigned, unAssigned);
    }

    /**
     * Calls the component's function to set values.
     * @param closed closed report count
     * @param assigned assigned report count
     * @param unAssigned unAssigned report count
     */
    public void setValues(long closed, long assigned, long unAssigned){
        getElement().callJsFunction("setValues", (int)closed, (int)assigned, (int)unAssigned);
    }
}
