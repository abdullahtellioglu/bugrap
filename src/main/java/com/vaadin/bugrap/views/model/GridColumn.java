package com.vaadin.bugrap.views.model;

import java.util.Arrays;

public enum GridColumn {
    VERSION("Version",true, false),
    PRIORITY("Priority",true, false),
    TYPE("Type", true, true),
    SUMMARY("Summary", true, true),
    STATUS("Status", false, true),
    ASSIGNED_TO("Assigned to", true, true),
    LAST_MODIFIED("Last modified", true, true),
    REPORTED("Reported", true, true);

    private final boolean initialVisible;
    private final boolean changeable;
    private final String label;
    GridColumn(String label, boolean initialVisible, boolean changeable) {
        this.label = label;
        this.initialVisible = initialVisible;
        this.changeable = changeable;
    }

    public boolean isInitialVisible() {
        return initialVisible;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public String getLabel() {
        return label;
    }
    public static GridColumn find(String name){
        return Arrays.stream(GridColumn.values()).filter(gridColumn -> gridColumn.name().equals(name)).findFirst().orElse(null);
    }
}
