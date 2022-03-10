package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import org.vaadin.bugrap.domain.entities.Project;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Project selector is a component that user can display active projects and select one of them to display. <br/>
 *
 */
public class ProjectSelector extends HorizontalLayout {
    private final Select<Project> projectSelect;
    private final Button managerLabel;
    private final Button closeButton;

    private Consumer<Project> listener;

    public ProjectSelector() {
        super();
        setClassName("project-selector");
        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setPadding(true);

        managerLabel = new Button();
        managerLabel.setIcon(VaadinIcon.USER.create());
        managerLabel.setClassName("manager-label");
        closeButton = new Button(VaadinIcon.POWER_OFF.create());

        projectSelect = new Select<>();
        HorizontalLayout rightContainer = new HorizontalLayout(managerLabel, closeButton);
        add(projectSelect);
        add(rightContainer);
        projectSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<Project>, Project>>) event -> {
            Project value = event.getValue();
            if(listener != null){
                listener.accept(value);
            }
        });
    }
    public void setProjectSelectListener(Consumer<Project> listener){
        this.listener = listener;
    }
    /**
     * Updates the manager label
     * @param name manager label
     */
    public void setManagerName(String name){
        this.managerLabel.setText(name);
    }

    /**
     * Updates active projects in Select. If there is none selected currently, first project selection event is triggered.
     * @param projects List of projects.
     */
    public void setActiveProjects(List<Project> projects){
        projectSelect.setItems(projects);
        if(projectSelect.getValue() == null && !projects.isEmpty() && listener != null){
            projectSelect.setValue(projects.get(0));
            listener.accept(projects.get(0));
        }
    }

}
