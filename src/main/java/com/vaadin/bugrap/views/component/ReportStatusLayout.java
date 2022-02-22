package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ReportStatusLayout extends HorizontalLayout {
    private Button onlyMeButton;
    private Button everyoneButton;


    private Button selectedAssigneeButton;

    public ReportStatusLayout() {
        setClassName("report-status");
        setMargin(true);
        Label assigneesLabel = new Label("Assignees");
        add(assigneesLabel);

        HorizontalLayout assigneeButtonContainer = new HorizontalLayout();

        assigneeButtonContainer.setPadding(false);
        assigneeButtonContainer.setMargin(false);
        assigneeButtonContainer.setSpacing(false);

        onlyMeButton = new Button("Only me");
        onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        onlyMeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            everyoneButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        everyoneButton = new Button("Everyone");
        everyoneButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                onlyMeButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                everyoneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        //TODO how to get user ?

        assigneeButtonContainer.add(onlyMeButton, everyoneButton);

        add(assigneeButtonContainer);

        Label statusLabel = new Label("Status");
        statusLabel.setClassName("status-label");
        add(statusLabel);

        HorizontalLayout statusButtonContainer = new HorizontalLayout();
        statusButtonContainer.setPadding(false);
        statusButtonContainer.setMargin(false);
        statusButtonContainer.setSpacing(false);

        Button openBtn = new Button("Open");
        openBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button allKinds = new Button("All kinds");
        Button customBtn = new Button("Custom...");

        statusButtonContainer.add(openBtn, allKinds, customBtn);
        add(statusButtonContainer);
    }
}
