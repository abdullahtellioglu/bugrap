package com.vaadin.bugrap.views.component;

import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

public class ProjectVersionComboBox extends ComboBox<ProjectVersion> {
    public ProjectVersionComboBox() {
        setLabel("Reports for");
        setAllowCustomValue(false);

    }
}
