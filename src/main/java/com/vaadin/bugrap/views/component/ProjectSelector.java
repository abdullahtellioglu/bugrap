package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.vaadin.bugrap.domain.entities.Project;

import java.util.List;
import java.util.function.Consumer;

/**
 * Project selector is a component that user can display active projects and select one of them to display. <br/>
 */
@UIScope
@SpringComponent
public class ProjectSelector extends HorizontalLayout {
    private final Select<Project> projectSelect;
    private final Button managerLabel;
    private final Button closeButton;
    private final ChartDialog chartDialog;
    private Consumer<Project> listener;

    public ProjectSelector(ChartDialog chartDialog) {
        super();
        this.chartDialog = chartDialog;
        setClassName("project-selector");
        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setPadding(true);

        managerLabel = new Button();
        managerLabel.setIcon(VaadinIcon.USER.create());
        managerLabel.setClassName("manager-label");
        closeButton = new Button(VaadinIcon.POWER_OFF.create());

        projectSelect = new Select<>();

        Button showChartBtn = new Button(VaadinIcon.CHART.create());

        HorizontalLayout rightContainer = new HorizontalLayout(showChartBtn, managerLabel, closeButton);
        add(projectSelect);
        add(rightContainer);
        add(chartDialog);
        showChartBtn.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            chartDialog.initializeData();
            chartDialog.open();
        });

        projectSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<Project>, Project>>) event -> {
            Project value = event.getValue();
            if (listener != null) {
                listener.accept(value);
            }
        });
    }

    public void setProjectSelectListener(Consumer<Project> listener) {
        this.listener = listener;
    }

    /**
     * Updates the manager label
     *
     * @param name manager label
     */
    public void setManagerName(String name) {
        this.managerLabel.setText(name);
    }

    /**
     * Updates active projects in Select. If there is none selected currently, first project selection event is triggered.
     *
     * @param projects List of projects.
     */
    public void setActiveProjects(List<Project> projects) {
        projectSelect.setItems(projects);
        if (projectSelect.getValue() == null && !projects.isEmpty() && listener != null) {
            projectSelect.setValue(projects.get(0));
            listener.accept(projects.get(0));
        }
    }

}
