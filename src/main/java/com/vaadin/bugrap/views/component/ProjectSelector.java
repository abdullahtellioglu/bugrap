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

public class ProjectSelector extends HorizontalLayout {
    private final Select<Project> projectSelect;
    private final Label managerLabel;
    private final Button closeButton;
    public void setProjectSelectListener(ProjectSelectListener listener){
        this.listener = listener;
    }

    private ProjectSelectListener listener;

    public ProjectSelector() {
        super();
        setClassName("project-selector");
        setWidth(100, Unit.PERCENTAGE);
        projectSelect = new Select<>();
        managerLabel = new Label();
        managerLabel.setClassName("manager-label");
        closeButton = new Button(VaadinIcon.POWER_OFF.create());

        Icon icon = new Icon(VaadinIcon.USER);
        icon.setSize("small");
        setJustifyContentMode(JustifyContentMode.BETWEEN);

        HorizontalLayout rightContainer = new HorizontalLayout(icon, managerLabel, closeButton);

        add(projectSelect);
        add(rightContainer);
        projectSelect.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<Project>, Project>>) event -> {
            Project value = event.getValue();
            if(listener != null){
                listener.onSelect(value);
            }
        });
        setPadding(true);
    }
    public void setManagerName(String name){
        this.managerLabel.setText(name);
    }
    public void setActiveProjects(List<Project> projects){
        projectSelect.setItems(projects);
        if(projectSelect.getValue() == null && !projects.isEmpty() && listener != null){
            projectSelect.setValue(projects.get(0));
            listener.onSelect(projects.get(0));
        }
    }
    private void initUI(){

    }

    public interface ProjectSelectListener {
        void onSelect(Project project);
    }

}
