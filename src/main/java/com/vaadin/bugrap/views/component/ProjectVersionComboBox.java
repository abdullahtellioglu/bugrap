package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectVersionComboBox extends HorizontalLayout {
    private final ComboBox<ProjectVersion> comboBox;
    public ProjectVersionComboBox() {
        comboBox = new ComboBox<>();
        setClassName("version-combo-container");
        Label label = new Label("Reports for");
        add(label);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(comboBox);

    }
    public void setItems(List<ProjectVersion> projectVersions){
        this.comboBox.setItems(projectVersions);
    }
    public void setValue(ProjectVersion projectVersion){
        this.comboBox.setValue(projectVersion);

    }
    public void addValueChangeListener(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<ProjectVersion>, ProjectVersion>> valueChangeListener){
        this.comboBox.addValueChangeListener(valueChangeListener);
    }
}
