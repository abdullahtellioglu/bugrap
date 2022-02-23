package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectVersionSelect extends HorizontalLayout {
    private final Select<ProjectVersion> versionSelect;
    public ProjectVersionSelect() {
        versionSelect = new Select<>();
        setClassName("version-combo-container");
        Label label = new Label("Reports for");
        add(label);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(versionSelect);

    }
    public void setItems(List<ProjectVersion> projectVersions){
        this.versionSelect.setItems(projectVersions);
    }
    public void setValue(ProjectVersion projectVersion){
        this.versionSelect.setValue(projectVersion);

    }
    public void addValueChangeListener(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Select<ProjectVersion>, ProjectVersion>> valueChangeListener){
        this.versionSelect.addValueChangeListener(valueChangeListener);
    }
}
