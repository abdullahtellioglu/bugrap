package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ReportStatusLayout extends HorizontalLayout {

    public ReportStatusLayout() {
        setClassName("report-status");
        setMargin(true);
        Label assigneesLabel = new Label("Assignees");
        add(assigneesLabel);

        HorizontalLayout assigneeButtonContainer = new HorizontalLayout();

        assigneeButtonContainer.setPadding(false);
        assigneeButtonContainer.setMargin(false);
        assigneeButtonContainer.setSpacing(false);

        Button onlyMeButton = new Button("Only me");
        onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button everyoneButton = new Button("Everyone");


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
