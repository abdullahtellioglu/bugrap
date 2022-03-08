package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@Tag("distribution-bar")
@JsModule("./distribution-bar.ts")
public class DistributionBar extends Component implements FlexComponent {

    public DistributionBar(){
        setAlignItems(Alignment.START);

    }
    public DistributionBar(long closed, long assigned, long unAssigned){
        this();
        setValues(closed, assigned, unAssigned);
    }
    public void setValues(long closed, long assigned, long unAssigned){
        getElement().callJsFunction("setValues", (int)closed, (int)assigned, (int)unAssigned);
    }
}
