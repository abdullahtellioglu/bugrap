package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

/**
 * Project Version is a wrapper component to display label in the left of combobox instead of displaying on top
 */
public class ProjectVersionComboBox extends HorizontalLayout {
    private final ComboBox<ProjectVersion> versionComboBox = new ComboBox<>();

    public ProjectVersionComboBox() {
        versionComboBox.setId("version-combo-box");

        Label label = new Label("Reports for");

        add(label);
        add(versionComboBox);

        setClassName("version-combo-container");
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    public void setItems(List<ProjectVersion> projectVersions) {
        this.versionComboBox.setItems(projectVersions);
    }

    public void setValue(ProjectVersion projectVersion) {
        this.versionComboBox.setValue(projectVersion);

    }

    public void addValueChangeListener(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<ProjectVersion>, ProjectVersion>> valueChangeListener) {
        this.versionComboBox.addValueChangeListener(valueChangeListener);
    }
}
