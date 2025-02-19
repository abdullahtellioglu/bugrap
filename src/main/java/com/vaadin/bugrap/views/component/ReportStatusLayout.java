package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.*;
import java.util.function.Consumer;

/**
 * Report status component contains assignee, status and column selection buttons. <br/>
 * User changes filters of table in this component. <br/>
 * If assignee changes it invokes event {@link ReportStatusLayout#assigneeChangeListener} <br/>
 * If status changes it invokes event {@link ReportStatusLayout#statusChangeListener} <br/>
 * If column changes it invokes event {@link ReportStatusLayout#gridColumnChangeListener} <br/>
 * <b>If user selects all kinds status, it invokes statusChangeListener with null value instead of empty set</b>
 */
public class ReportStatusLayout extends HorizontalLayout {
    private static final String MIN_BUTTON_WIDTH = "calc(var(--lumo-button-size) * 3)";
    private final Button onlyMeButton;
    private final Button everyoneButton;
    private final Button openStatusBtn;
    private final Button allKindsStatusBtn;
    private final Button customStatusBtn;
    private final ContextMenu customContextMenu;
    private final ContextMenu columnsContextMenu;
    private final Set<Report.Status> selectedStatusSet = new HashSet<>();
    private Consumer<Reporter> assigneeChangeListener;
    private Consumer<Set<Report.Status>> statusChangeListener;
    private Consumer<GridColumn> gridColumnChangeListener;
    private Runnable gridSelectionClearClickListener;
    private Runnable gridSortingClearClickListener;
    private Reporter currentUser;
    private Set<GridColumn> selectedGridColumns;

    public ReportStatusLayout() {
        onlyMeButton = new Button("Only me");
        onlyMeButton.setId("only-me-btn");
        onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        onlyMeButton.setWidth(MIN_BUTTON_WIDTH);

        everyoneButton = new Button("Everyone");
        everyoneButton.setId("everyone-btn");
        everyoneButton.setMinWidth(MIN_BUTTON_WIDTH);
HorizontalLayout assigneeButtonContainer = new HorizontalLayout(onlyMeButton, everyoneButton);

        assigneeButtonContainer.setClassName("assignee-container");
        assigneeButtonContainer.setPadding(false);
        assigneeButtonContainer.setMargin(false);
        assigneeButtonContainer.setSpacing(false);

        Label statusLabel = new Label("Status");
        statusLabel.setClassName("status-label");

        openStatusBtn = new Button("Open");
        openStatusBtn.setId("open-status-btn");
        openStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        openStatusBtn.setMinWidth(MIN_BUTTON_WIDTH);

        allKindsStatusBtn = new Button("All kinds");
        allKindsStatusBtn.setId("all-kinds-btn");
        allKindsStatusBtn.setMinWidth(MIN_BUTTON_WIDTH);

        customStatusBtn = new Button();
        customStatusBtn.setText("Custom...");
        customStatusBtn.setId("custom-btn");
        customContextMenu = new ContextMenu(customStatusBtn);

        customContextMenu.addAttachListener((ComponentEventListener<AttachEvent>) event ->
                customContextMenu.getElement().setAttribute("onclick", "event.preventDefault()"));
        customContextMenu.setOpenOnClick(true);

        HorizontalLayout statusButtonContainer = new HorizontalLayout(openStatusBtn, allKindsStatusBtn, customStatusBtn);
        statusButtonContainer.setClassName("status-button-container");
        statusButtonContainer.setPadding(false);
        statusButtonContainer.setMargin(false);
        statusButtonContainer.setSpacing(false);

        Button editColumnsBtn = new Button(VaadinIcon.PENCIL.create());
        editColumnsBtn.setId("edit-columns-btn");

        columnsContextMenu = new ContextMenu(editColumnsBtn);
        columnsContextMenu.setOpenOnClick(true);
        columnsContextMenu.addAttachListener((ComponentEventListener<AttachEvent>) event ->
                columnsContextMenu.getElement().setAttribute("onclick", "event.preventDefault()"));

        Button clearSelectionBtn = new Button(VaadinIcon.CLOSE_CIRCLE.create());
        clearSelectionBtn.setId("clear-selection-btn");
        clearSelectionBtn.getElement().setProperty("title", "Clear selections");

        Button clearSortBtn = new Button(new Icon("lumo", "unordered-list"));
        clearSortBtn.setId("clear-sort-btn");
        clearSortBtn.getElement().setProperty("title", "Clear sorting");

        HorizontalLayout gridOptionBtnContainer = new HorizontalLayout(editColumnsBtn, clearSelectionBtn, clearSortBtn);
        gridOptionBtnContainer.setPadding(false);
        gridOptionBtnContainer.setSpacing(false);
        add(new HorizontalLayout(new Label("Assignees"), assigneeButtonContainer, statusLabel, statusButtonContainer), gridOptionBtnContainer);

        initializeStatusContextMenu();
        initializeEvents(clearSelectionBtn, clearSortBtn);
        initializeColumnsContextMenu();

        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setClassName("report-status");

        selectedStatusSet.add(Report.Status.OPEN);
    }


    private void initializeEvents(Button clearSelectionBtn, Button clearSortingBtn) {
        openStatusBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            selectedStatusSet.clear();
            selectedStatusSet.add(Report.Status.OPEN);
            setThemeVariables();
            if (statusChangeListener != null) {
                statusChangeListener.accept(Collections.singleton(Report.Status.OPEN));
            }
        });
        allKindsStatusBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            selectedStatusSet.clear();
            setThemeVariables();
            if (statusChangeListener != null) {
                statusChangeListener.accept(null);
            }
        });

        onlyMeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            onlyMeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            everyoneButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            if (assigneeChangeListener != null) {
                assigneeChangeListener.accept(currentUser);
            }
        });
        everyoneButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            onlyMeButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            everyoneButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            if (assigneeChangeListener != null) {
                assigneeChangeListener.accept(null);
            }
        });
        clearSelectionBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (gridSelectionClearClickListener != null) {
                gridSelectionClearClickListener.run();
            }
        });
        clearSortingBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if (gridSortingClearClickListener != null) {
                gridSortingClearClickListener.run();
            }
        });
    }

    private void initializeColumnsContextMenu() {
        GridColumn[] columns = GridColumn.values();
        for (GridColumn column : columns) {
            MenuItem menuItem = columnsContextMenu.addItem(column.getLabel());
            menuItem.setId(column.name());
            menuItem.setCheckable(true);

            menuItem.setEnabled(column.isChangeable());
            menuItem.getElement().setAttribute("onclick", "event.stopPropagation()");
            menuItem.addClickListener((ComponentEventListener<ClickEvent<MenuItem>>) event -> event.getSource().getId().
                    flatMap(id -> Arrays.stream(columns).filter(f -> f.name().equals(id)).findFirst()).ifPresent(foundColumn -> {
                gridColumnChangeListener.accept(foundColumn);

            }));
        }
    }

    private void setCheckedStatusesGridColumns() {
        List<MenuItem> menuItems = columnsContextMenu.getItems();
        menuItems.forEach(menuItem -> {
            menuItem.getId().ifPresent(id -> {
                Optional<GridColumn> first = selectedGridColumns.stream().filter(gridColumn -> gridColumn.name().equals(id)).findFirst();
                menuItem.setChecked(first.isPresent());
            });
        });
        columnsContextMenu.getElement().executeJs("this.requestContentUpdate($0)", true);
    }


    public void setGridSortingClearClickListener(Runnable gridSortingClearClickListener) {
        this.gridSortingClearClickListener = gridSortingClearClickListener;
    }

    public void setGridSelectionClearClickListener(Runnable gridSelectionClearClickListener) {
        this.gridSelectionClearClickListener = gridSelectionClearClickListener;
    }

    public void setSelectedGridColumns(Set<GridColumn> gridColumns) {
        selectedGridColumns = gridColumns;
    }

    /**
     * Updates grid columns based on {@link ReportStatusLayout#selectedGridColumns} also {@link com.vaadin.bugrap.views.container.ProjectLayout}
     */
    public void updateGridColumns() {
        setCheckedStatusesGridColumns();
    }

    public void setGridColumnChangeListener(Consumer<GridColumn> gridColumnChangeListener) {
        this.gridColumnChangeListener = gridColumnChangeListener;
    }

    public void setAssigneeChangeListener(Consumer<Reporter> assigneeChangeListener) {
        this.assigneeChangeListener = assigneeChangeListener;
    }

    public void setCurrentUser(Reporter currentUser) {
        this.currentUser = currentUser;
    }

    public void setStatusChangeListener(Consumer<Set<Report.Status>> statusChangeListener) {
        this.statusChangeListener = statusChangeListener;
    }

    private void initializeStatusContextMenu() {
        Report.Status[] statuses = Report.Status.values();
        for (Report.Status status : statuses) {

            MenuItem statusMenuItem = customContextMenu.addItem(status.toString());

            statusMenuItem.getElement().setAttribute("onclick", "event.stopPropagation()");

            statusMenuItem.addClickListener((ComponentEventListener<ClickEvent<MenuItem>>) event -> {
                Optional<String> idOptional = event.getSource().getId();
                if (idOptional.isEmpty()) {
                    return;
                }
                String statusId = idOptional.get();
                Optional<Report.Status> optionalStatus = Arrays.stream(Report.Status.values()).filter(f -> f.name().equals(statusId)).findFirst();
                if (optionalStatus.isEmpty()) {
                    return;
                }
                Report.Status checkedStatus = optionalStatus.get();
                if (selectedStatusSet.contains(checkedStatus)) {
                    selectedStatusSet.remove(checkedStatus);
                } else {
                    selectedStatusSet.add(checkedStatus);
                }
                if (statusChangeListener != null) {
                    statusChangeListener.accept(selectedStatusSet.isEmpty() ? null : selectedStatusSet);
                }
                setThemeVariables();
                customContextMenu.getElement().executeJs("this.requestContentUpdate($0)", true);
            });

            statusMenuItem.setId(status.name());
            statusMenuItem.setCheckable(true);
            statusMenuItem.setChecked(selectedStatusSet.contains(status));

        }
    }

    private void setThemeVariables() {
        if (selectedStatusSet.isEmpty()) {
            openStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        } else if (selectedStatusSet.size() == 1 && Objects.equals(selectedStatusSet.iterator().next(), Report.Status.OPEN)) {
            openStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        } else {
            openStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            allKindsStatusBtn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            customStatusBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }

        customContextMenu.getItems().forEach(subMenuItem -> {
            Optional<String> idOptional = subMenuItem.getId();
            if (idOptional.isEmpty()) {
                return;
            }
            String statusId = idOptional.get();
            Optional<Report.Status> optionalStatus = Arrays.stream(Report.Status.values()).filter(f -> Objects.equals(f.name(), statusId)).findFirst();
            if (optionalStatus.isEmpty()) {
                return;
            }
            Report.Status checkedStatus = optionalStatus.get();
            subMenuItem.setChecked(selectedStatusSet.contains(checkedStatus));

        });

    }
public record LabelAndValue(String label, String value, boolean enabled) {

    LabelAndValue(String label, String value) {
        this(label, value, true);
    }
}
}
