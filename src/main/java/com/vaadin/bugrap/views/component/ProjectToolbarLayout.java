package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.style.Color;
import com.vaadin.flow.component.charts.model.style.Theme;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.function.Consumer;

public class ProjectToolbarLayout extends HorizontalLayout {

    private final Button reportBugButton;
    private final Button requestFeatureButton;
    private final Button manageProjectButton;
    private final TextField searchTextField;

    private final Span manageProjectCountSpan;

    private Consumer<String> searchTextChangeListener;

    public ProjectToolbarLayout() {
        setClassName("project-toolbar");
        setWidth(100, Unit.PERCENTAGE);
        this.setJustifyContentMode(JustifyContentMode.BETWEEN);



        this.reportBugButton = new Button("Report a bug", new Icon(VaadinIcon.BUG));
        this.requestFeatureButton = new Button("Request a feature", new Icon(VaadinIcon.LIGHTBULB));

        HorizontalLayout manageButtonInternalContainer = new HorizontalLayout();
        manageButtonInternalContainer.setClassName("manage-button-container");
        Icon cogIcon = VaadinIcon.COG.create();

        manageButtonInternalContainer.add(cogIcon);
        Label manageProjectLabel = new Label("Manage project");
        manageButtonInternalContainer.add(manageProjectLabel);

        manageProjectCountSpan = new Span("10");
        manageButtonInternalContainer.add(manageProjectCountSpan);
        this.manageProjectButton = new Button(manageButtonInternalContainer);
        this.manageProjectButton.setThemeName("icon-text-badge-button");




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
            if(searchTextChangeListener != null){
                searchTextChangeListener.accept(value);
            }
        });
        this.add(searchTextField);
    }

    public void setSearchTextChangeListener(Consumer<String> searchTextChangeListener) {
        this.searchTextChangeListener = searchTextChangeListener;
    }

    public void setProjectCount(int count){
        //TODO all open reports in project.
        manageProjectCountSpan.setText(String.valueOf(count));
    }

}
