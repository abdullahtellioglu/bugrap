package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.style.Color;
import com.vaadin.flow.component.charts.model.style.Theme;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class ProjectToolbarLayout extends HorizontalLayout {

    private final Button reportBugButton;
    private final Button requestFeatureButton;
    private final Button manageProjectButton;
    private final TextField searchTextField;
    public ProjectToolbarLayout() {
        setClassName("project-toolbar");
        setWidth(100, Unit.PERCENTAGE);
        this.setJustifyContentMode(JustifyContentMode.BETWEEN);



        this.reportBugButton = new Button("Report a bug", new Icon(VaadinIcon.BUG));
        this.requestFeatureButton = new Button("Request a feature", new Icon(VaadinIcon.LIGHTBULB));
        this.manageProjectButton = new Button("Manage project", new Icon(VaadinIcon.ACCESSIBILITY));
        reportBugButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            Notification.show("Not implemented yet");
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(reportBugButton);
        horizontalLayout.add(requestFeatureButton);
        horizontalLayout.add(manageProjectButton);



        this.add(horizontalLayout);

        this.searchTextField = new TextField();
        searchTextField.setPlaceholder("Search");
        searchTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchTextField.setClearButtonVisible(true);
        searchTextField.addValueChangeListener(e -> {
            String value =  e.getValue();
            System.out.println(value);
        });


        this.add(searchTextField);


    }

    public void setProjectCount(int count){
        //TODO how to set secondary badge into Button
    }

}
