package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.function.Consumer;

/**
 * Project toolbar is a component that has report bug button, request feature button, manage project button and a search field for text search.<br/>
 * If user searches some text from text field, it invokes {@link ProjectToolbarLayout#searchTextChangeListener}. <br/<br/>
 * <b>ReportBug, RequestFeature, ManageProject buttons are displaying but events are not implemented</b><br/>
 */
@SpringComponent
@UIScope
public class ProjectToolbarLayout extends HorizontalLayout {
    private static final String NOT_IMPLEMENTED = "Not implemented";
    private final Span manageProjectCountSpan;
    private final Button reportBugButton;
    private final Button requestFeatureButton;
    private final Button manageProjectButton;
    private final TextField searchTextField;
    private Consumer<String> searchTextChangeListener;

    public ProjectToolbarLayout() {

        reportBugButton = new Button("Report a bug", new Icon(VaadinIcon.BUG));
        requestFeatureButton = new Button("Request a feature", new Icon(VaadinIcon.LIGHTBULB));

        Icon cogIcon = VaadinIcon.COG.create();
        Label manageProjectLabel = new Label("Manage project");
        manageProjectCountSpan = new Span("10");
        HorizontalLayout manageButtonInternalContainer = new HorizontalLayout(cogIcon, manageProjectLabel, manageProjectCountSpan);
        manageButtonInternalContainer.setClassName("manage-button-container");
        manageProjectButton = new Button(manageButtonInternalContainer);
        manageProjectButton.setThemeName("icon-text-badge-button");

        searchTextField = new TextField();
        searchTextField.setPlaceholder("Search");
        searchTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchTextField.setClearButtonVisible(true);

        add(new HorizontalLayout(reportBugButton, requestFeatureButton, manageProjectButton));
        add(searchTextField);


        setClassName("project-toolbar");
        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);

        initializeEvents();
    }

    private void initializeEvents() {
        reportBugButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> Notification.show(NOT_IMPLEMENTED));
        requestFeatureButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> Notification.show(NOT_IMPLEMENTED));
        manageProjectButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> Notification.show(NOT_IMPLEMENTED));
        searchTextField.addValueChangeListener(e -> {
            String value = e.getValue();
            if (searchTextChangeListener != null) {
                searchTextChangeListener.accept(value);
            }
        });
    }

    public void setSearchTextChangeListener(Consumer<String> searchTextChangeListener) {
        this.searchTextChangeListener = searchTextChangeListener;
    }

    public void setOpenedReportCount(long count) {
        manageProjectCountSpan.setText(String.valueOf(count));
    }

}
