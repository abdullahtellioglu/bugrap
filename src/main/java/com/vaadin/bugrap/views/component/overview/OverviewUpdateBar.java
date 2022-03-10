package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.component.PriorityBar;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.Serializable;
import java.util.List;

/**
 * Overview Update Bar is a component which has priority, type, status, reporter and version selects. <br/>
 * In mass modification mode, requirements are removed, otherwise all fields are required. <br/><br/>
 * If user clicks on save changes button {@link OverviewUpdateBar#listener} is invoked.
 */
public class OverviewUpdateBar extends HorizontalLayout {
    private static final String BORDERED_THEME_NAME = "bordered";
    private static final String REQUIRED_PROPERTY = "required";

    private ReportsUpdateListener listener;

    private Binder<Overview> binder;
    //views
    private final Select<Report.Priority> prioritySelect = new Select<>();
    private final ComboBox<Report.Type> typeComboBox = new ComboBox<>();
    private final ComboBox<Report.Status> statusComboBox = new ComboBox<>();
    private final ComboBox<Reporter> reporterComboBox = new ComboBox<>();
    private final ComboBox<ProjectVersion> versionComboBox = new ComboBox<>();

    private Overview initialOverview;


    public OverviewUpdateBar() {
        binder = new Binder<>();
        prioritySelect.addClassName(BORDERED_THEME_NAME);

        initPrioritySelect();
        initTypeComboBox();
        initStatusComboBox();
        initReporterComboBox();
        initVersionComboBox();

        HorizontalLayout selectContainers = new HorizontalLayout(prioritySelect, typeComboBox, statusComboBox, reporterComboBox, versionComboBox);

        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button revertButton = new Button("Revert", VaadinIcon.ROTATE_LEFT.create());

        add(selectContainers);
        add(new HorizontalLayout(saveChangesButton, revertButton));

        initializeEvents(revertButton, saveChangesButton);
        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setAlignItems(Alignment.BASELINE);

    }

    private void initializeEvents(Button revertButton, Button saveChangesButton){
        revertButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            binder.readBean(initialOverview.copy());
        });


        addAttachListener((ComponentEventListener<AttachEvent>) attachEvent -> attachEvent.getUI().addShortcutListener((ShortcutEventListener) event -> {
            if(initialOverview == null){
                return;
            }
            BinderValidationStatus<Overview> validationResult = binder.validate();
            if(!validationResult.isOk()){
                return;
            }
            Overview overview = binder.getBean();
            if(listener != null){
                listener.onUpdate(
                        overview.getPriority(),
                        overview.getType(),
                        overview.getStatus(),
                        overview.getReporter(),
                        overview.getVersion());
            }


        }, Key.KEY_S, KeyModifier.CONTROL));

        saveChangesButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            BinderValidationStatus<Overview> validationResult = binder.validate();
            if(!validationResult.isOk()){
                return;
            }
            Overview overview = binder.getBean();

            if(listener != null){
                listener.onUpdate(
                        overview.getPriority(),
                        overview.getType(),
                        overview.getStatus(),
                        overview.getReporter(),
                        overview.getVersion());
            }
        });
    }

    /**
     * Listener for save changes button.
     * @param listener
     */
    public void setListener(ReportsUpdateListener listener) {
        this.listener = listener;
    }

    /**
     * Project version items.
     * @param versions
     */
    public void setProjectVersions(List<ProjectVersion> versions){
        this.versionComboBox.setItems(versions);
    }

    /**
     * Reporter combobox items.
     * @param reporters list of users
     */
    public void setReporters(List<Reporter> reporters){
        this.reporterComboBox.setItems(reporters);
    }

    /**
     * Clears the select boxes.
     */
    public void clearOverview(){
        this.initialOverview = null;
    }

    /**
     * Initialize component with given parameter.
     *
     * @param priority Priority
     * @param type Type
     * @param status Status
     * @param reporter Reporter user
     * @param projectVersion Report's project version
     * @param massModificationMode Initialize the binder. If true, all fields are not required otherwise all fields are required.
     */
    public void setOverview(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion projectVersion, boolean massModificationMode){
        Overview overview = new Overview();
        overview.setPriority(priority);
        overview.setType(type);
        overview.setStatus(status);
        overview.setReporter(reporter);
        overview.setVersion(projectVersion);

        binder = new Binder<>();
        initializeBinder(massModificationMode);

        binder.setBean(overview);
        this.initialOverview = overview.copy();
    }
    private void initializeBinder(boolean massModificationMode){
        //TODO CHECK VALIDATION
        if(!massModificationMode){
            prioritySelect.getElement().setProperty(REQUIRED_PROPERTY, true);
            typeComboBox.getElement().setProperty(REQUIRED_PROPERTY, true);
            statusComboBox.getElement().setProperty(REQUIRED_PROPERTY, true);
            reporterComboBox.getElement().setProperty(REQUIRED_PROPERTY, true);
            versionComboBox.getElement().setProperty(REQUIRED_PROPERTY, true);
        }else{
            prioritySelect.getElement().setProperty(REQUIRED_PROPERTY, false);
            typeComboBox.getElement().setProperty(REQUIRED_PROPERTY, false);
            statusComboBox.getElement().setProperty(REQUIRED_PROPERTY, false);
            reporterComboBox.getElement().setProperty(REQUIRED_PROPERTY, false);
            versionComboBox.getElement().setProperty(REQUIRED_PROPERTY, false);
        }
        Binder.BindingBuilder<Overview, Report.Priority> overviewPriorityBindingBuilder = binder.forField(prioritySelect);
        if(!massModificationMode){
            overviewPriorityBindingBuilder.asRequired("Priority is required");
        }
        overviewPriorityBindingBuilder.bind(Overview::getPriority, Overview::setPriority);

        Binder.BindingBuilder<Overview, Report.Type> overviewTypeBindingBuilder = binder.forField(typeComboBox);
        if(!massModificationMode){
            overviewTypeBindingBuilder.asRequired("Type is required");
        }
        overviewTypeBindingBuilder.bind(Overview::getType, Overview::setType);

        Binder.BindingBuilder<Overview, Report.Status> overviewStatusBindingBuilder = binder.forField(statusComboBox);
        if(!massModificationMode){
            overviewStatusBindingBuilder.asRequired("Status is required");
        }
        overviewStatusBindingBuilder.bind(Overview::getStatus, Overview::setStatus);

        Binder.BindingBuilder<Overview, Reporter> overviewReporterBindingBuilder = binder.forField(reporterComboBox);
        if(!massModificationMode){
            overviewReporterBindingBuilder.asRequired("Assignee is required");
        }
        overviewReporterBindingBuilder.bind(Overview::getReporter, Overview::setReporter);

        Binder.BindingBuilder<Overview, ProjectVersion> overviewProjectVersionBindingBuilder = binder.forField(versionComboBox);
        if(!massModificationMode){
            overviewProjectVersionBindingBuilder.asRequired("Version is required");
        }
        overviewProjectVersionBindingBuilder.bind(Overview::getVersion, Overview::setVersion);

    }

    private void initPrioritySelect(){
        prioritySelect.setLabel("Priority");

        prioritySelect.setItems(Report.Priority.values());
        prioritySelect.setRenderer(new ComponentRenderer<>((SerializableFunction<Report.Priority, Component>) priority -> {
            if(priority != null){
                return new PriorityBar(priority);
            }
            return new Span();
        }));
    }
    private void initTypeComboBox(){
        typeComboBox.setLabel("Type");
        typeComboBox.addThemeName(BORDERED_THEME_NAME);
        typeComboBox.setItems(Report.Type.values());

    }
    private void initStatusComboBox(){
        statusComboBox.setLabel("Status");
        statusComboBox.addThemeName(BORDERED_THEME_NAME);
        statusComboBox.setItems(Report.Status.values());

        statusComboBox.setRenderer(new ComponentRenderer<>((SerializableFunction<Report.Status, Component>) status -> {
            if(status != null){
                return new Span(status.toString());
            }
            return null;
        }));
    }
    private void initReporterComboBox(){
        reporterComboBox.setLabel("Assignee");
        reporterComboBox.addThemeName(BORDERED_THEME_NAME);
        reporterComboBox.setRenderer(new ComponentRenderer<>((SerializableFunction<Reporter, Component>) reporter -> {
            if(reporter != null){
                return new Span(reporter.getName());
            }
            return null;
        }));
    }
    private void initVersionComboBox(){
        versionComboBox.setLabel("Version");
        versionComboBox.addThemeName(BORDERED_THEME_NAME);

        versionComboBox.setRenderer(new ComponentRenderer<>((SerializableFunction<ProjectVersion, Component>) projectVersion -> {
            if(projectVersion != null){
                return new Span(projectVersion.getVersion());
            }
            return new Span();
        }));
    }
    public interface ReportsUpdateListener extends Serializable {
        void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion version);
    }

    /**
     * Model is used for binding inputs.
     */
    public static class Overview implements Serializable {
        private Report.Priority priority;
        private Report.Type type;
        private Report.Status status;
        private Reporter reporter;
        private ProjectVersion version;

        public Overview copy(){
            Overview overview = new Overview();
            overview.priority = priority;
            overview.type = type;
            overview.status = status;
            overview.reporter = reporter;
            overview.version = version;
            return overview;
        }

        public Report.Priority getPriority() {
            return priority;
        }

        public void setPriority(Report.Priority priority) {
            this.priority = priority;
        }

        public Report.Type getType() {
            return type;
        }

        public void setType(Report.Type type) {
            this.type = type;
        }

        public Report.Status getStatus() {
            return status;
        }

        public void setStatus(Report.Status status) {
            this.status = status;
        }

        public Reporter getReporter() {
            return reporter;
        }

        public void setReporter(Reporter reporter) {
            this.reporter = reporter;
        }

        public ProjectVersion getVersion() {
            return version;
        }

        public void setVersion(ProjectVersion version) {
            this.version = version;
        }
    }
}
