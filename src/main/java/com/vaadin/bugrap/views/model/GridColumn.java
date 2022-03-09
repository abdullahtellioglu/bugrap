package com.vaadin.bugrap.views.model;

import java.util.Arrays;

/**
 * Represent columns in Report Grid. Enum's name is used for ID for given column.
 * <table>
 *     <tr>
 *         <td>Label</td>
 *         <td>Column name</td>
 *     </tr>
 *     <tr>
 *         <td>Initially visible</td>
 *         <td>If set true, user can see column on page load.</td>
 *     </tr>
 *     <tr>
 *         <td>Changeable</td>
 *         <td>If set true, user can column show/hide. {@link com.vaadin.bugrap.views.component.ReportStatusLayout} {@link com.vaadin.bugrap.views.component.ReportGrid} </td>
 *     </tr>
 * </table>
 */
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

    /**
     * Finds the grid column based on enum's name
     * @param name {@link GridColumn#name()}
     * @return GridColumn or null if not found.
     */
    public static GridColumn find(String name){
        return Arrays.stream(GridColumn.values()).filter(gridColumn -> gridColumn.name().equals(name)).findFirst().orElse(null);
    }
}
