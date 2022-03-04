package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public class ProjectVersionComboBox extends HorizontalLayout {
    private final ComboBox<ProjectVersion> versionComboBox;
    public ProjectVersionComboBox() {

        setClassName("version-combo-container");
        setJustifyContentMode(JustifyContentMode.CENTER);
        versionComboBox = new ComboBox<>();
        add(versionComboBox);
        Label label = new Label("Reports for");
        add(label);



    }
    public void setItems(List<ProjectVersion> projectVersions){
        this.versionComboBox.setItems(projectVersions);
    }
    public void setValue(ProjectVersion projectVersion){
        this.versionComboBox.setValue(projectVersion);

    }
    public void addValueChangeListener(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<ProjectVersion>, ProjectVersion>> valueChangeListener){
        this.versionComboBox.addValueChangeListener(valueChangeListener);
    }
}
