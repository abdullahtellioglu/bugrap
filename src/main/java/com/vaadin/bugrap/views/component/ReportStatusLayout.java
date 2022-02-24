package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;

public class ReportStatusLayout extends HorizontalLayout {
    private Button onlyMeButton;
    private Button everyoneButton;
    private Button openStatusBtn;
    private Button allKindsStatusBtn;
    private MenuBar customStatusMenuBar;
    private MenuItem customStatusMenuItem;

    private AssigneeChangeListener assigneeChangeListener;
    private StatusChangeListener statusChangeListener;
    private Reporter currentUser;

    private Set<Report.Status> selectedStatusSet = new HashSet<>();


    public void setAssigneeChangeListener(AssigneeChangeListener assigneeChangeListener) {
        this.assigneeChangeListener = assigneeChangeListener;
    }

    public void setCurrentUser(Reporter currentUser) {
        this.currentUser = currentUser;
    }

    public void setStatusChangeListener(StatusChangeListener statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
    }

    public ReportStatusLayout() {
        //initial value
        selectedStatusSet.add(Report.Status.OPEN);
        setClassName("report-status");
        Label assigneesLabel = new Label("Assignees");
        add(assigneesLabel);
        //Change - Tabs instead of buttons.
        onlyMeButton = new Button("Only me");
        onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        onlyMeButton.setWidth("calc(var(--lumo-button-size) * 3)");
        onlyMeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            onlyMeButton.removeClassName("shadow");
            everyoneButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            everyoneButton.addClassName("shadow");
            if(assigneeChangeListener != null){
                assigneeChangeListener.onChange(currentUser);
            }
        });

        everyoneButton = new Button("Everyone");
        everyoneButton.setMinWidth("calc(var(--lumo-button-size) * 3)");
        everyoneButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
                onlyMeButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                onlyMeButton.removeClassName("shadow");
                everyoneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                everyoneButton.addClassName("shadow");
                if(assigneeChangeListener != null){
                    assigneeChangeListener.onChange(null);
                }
        });

        HorizontalLayout assigneeButtonContainer = new HorizontalLayout();
        assigneeButtonContainer.setClassName("assignee-container");
        assigneeButtonContainer.setPadding(false);
        assigneeButtonContainer.setMargin(false);
        assigneeButtonContainer.setSpacing(false);

        assigneeButtonContainer.add(onlyMeButton, everyoneButton);

        add(assigneeButtonContainer);

        Label statusLabel = new Label("Status");
        statusLabel.setClassName("status-label");
        add(statusLabel);


        openStatusBtn = new Button("Open");
        openStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        openStatusBtn.setMinWidth("calc(var(--lumo-button-size) * 3)");
        openStatusBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            selectedStatusSet.clear();
            selectedStatusSet.add(Report.Status.OPEN);
            setThemeVariables();
            if(statusChangeListener != null){
                statusChangeListener.onChange(Collections.singleton(Report.Status.OPEN));
            }
        });
        allKindsStatusBtn = new Button("All kinds");
        allKindsStatusBtn.setMinWidth("calc(var(--lumo-button-size) * 3)");
        allKindsStatusBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            selectedStatusSet.clear();
            setThemeVariables();
            if(statusChangeListener != null){
                statusChangeListener.onChange(null);
            }
        });



        customStatusMenuBar = new MenuBar();
        customStatusMenuItem = customStatusMenuBar.addItem("Custom...");
        SubMenu customItemsSubMenu = customStatusMenuItem.getSubMenu();
        Report.Status[] statuses = Report.Status.values();
        for (Report.Status status : statuses) {
            MenuItem statusMenuItem = customItemsSubMenu.addItem(status.toString());
            statusMenuItem.addClickListener(statusMenuItemItemClickListener);
            statusMenuItem.setId(status.name());
            statusMenuItem.setCheckable(true);
            statusMenuItem.setChecked(selectedStatusSet.contains(status));
        }


        HorizontalLayout statusButtonContainer = new HorizontalLayout();
        statusButtonContainer.setClassName("status-button-container");
        statusButtonContainer.setPadding(false);
        statusButtonContainer.setMargin(false);
        statusButtonContainer.setSpacing(false);
        statusButtonContainer.add(openStatusBtn, allKindsStatusBtn, customStatusMenuBar);
        add(statusButtonContainer);
    }
    private ComponentEventListener<ClickEvent<MenuItem>> statusMenuItemItemClickListener = event -> {
        Optional<String> idOptional = event.getSource().getId();
        if(idOptional.isEmpty()){
           return;
        }
        String statusId = idOptional.get();
        Optional<Report.Status> optionalStatus = Arrays.stream(Report.Status.values()).filter(f -> f.name().equals(statusId)).findFirst();
        if(optionalStatus.isEmpty()){
            return;
        }
        Report.Status checkedStatus = optionalStatus.get();
        if(selectedStatusSet.contains(checkedStatus)){
            selectedStatusSet.remove(checkedStatus);
        }else{
            selectedStatusSet.add(checkedStatus);
        }

        setThemeVariables();
        if(statusChangeListener != null){
            statusChangeListener.onChange(selectedStatusSet.isEmpty() ? null : selectedStatusSet);
        }

        //use context menu for buttons
    };
    private void setThemeVariables(){
        if(selectedStatusSet.isEmpty()){
            openStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusMenuBar.removeThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        }else if(selectedStatusSet.size() == 1 && selectedStatusSet.iterator().next().equals(Report.Status.OPEN)){
            openStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusMenuBar.removeThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        }else{
            openStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusMenuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);
        }
        SubMenu subMenu = customStatusMenuItem.getSubMenu();
        subMenu.getItems().forEach(subMenuItem -> {
            Optional<String> idOptional = subMenuItem.getId();
            if(idOptional.isEmpty()){
                return;
            }
            String statusId = idOptional.get();
            Optional<Report.Status> optionalStatus = Arrays.stream(Report.Status.values()).filter(f -> f.name().equals(statusId)).findFirst();
            if(optionalStatus.isEmpty()){
                return;
            }
            Report.Status checkedStatus = optionalStatus.get();
            subMenuItem.setChecked(selectedStatusSet.contains(checkedStatus));
        });
    }


    public interface AssigneeChangeListener {
        void onChange(Reporter reporter);
    }
    public interface StatusChangeListener {
        void onChange(Set<Report.Status> statuses);
    }
}
