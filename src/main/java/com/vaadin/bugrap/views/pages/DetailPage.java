package com.vaadin.bugrap.views.pages;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@PageTitle("Projects")
@Route(value = "/project")
@RouteAlias(value = "/project")
public class DetailPage extends VerticalLayout implements HasUrlParameter<Integer> {

    public DetailPage(){
        add(new Label("Detail page"));
    }

    @Override
    public void setParameter(BeforeEvent event, Integer projectId) {
        System.out.println(projectId);
    }
}