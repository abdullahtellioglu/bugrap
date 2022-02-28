package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.component.PriorityBar;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;

public class OverviewUpdateBar extends HorizontalLayout {
    private ReportsUpdateListener listener;

    //views
    private final ComboBox<Report.Priority> prioritySelect = new ComboBox<>();
    private final ComboBox<Report.Type> typeComboBox = new ComboBox<>();
    private final ComboBox<Report.Status> statusComboBox = new ComboBox<>();
    private final ComboBox<Reporter> reporterComboBox = new ComboBox<>();
    private final ComboBox<ProjectVersion> versionComboBox = new ComboBox<>();

    //for revert action, these values are stored.
    private Report.Priority initialPriority;
    private Report.Type initialType;
    private Report.Status initialStatus;
    private Reporter initialReporter;
    private ProjectVersion initialProjectVersion;


    public OverviewUpdateBar() {
        setWidth(100, Unit.PERCENTAGE);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setAlignItems(Alignment.BASELINE);

        initPrioritySelect();
        initTypeComboBox();
        initStatusComboBox();
        initReporterComboBox();
        initVersionComboBox();


        HorizontalLayout selectContainers = new HorizontalLayout(prioritySelect, typeComboBox, statusComboBox, reporterComboBox, versionComboBox);


        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button revertButton = new Button("Revert", VaadinIcon.ROTATE_LEFT.create());
        HorizontalLayout buttonContainers = new HorizontalLayout(saveChangesButton, revertButton);

        revertButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            prioritySelect.setValue(initialPriority);
            typeComboBox.setValue(initialType);
            statusComboBox.setValue(initialStatus);
            reporterComboBox.setValue(initialReporter);
            versionComboBox.setValue(initialProjectVersion);
        });

        saveChangesButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if(listener != null){
                listener.onUpdate(
                        prioritySelect.getValue(),
                        typeComboBox.getValue(),
                        statusComboBox.getValue(),
                        reporterComboBox.getValue(),
                        versionComboBox.getValue());
            }
        });

        add(selectContainers);
        add(buttonContainers);
    }


    public void setListener(ReportsUpdateListener listener) {
        this.listener = listener;
    }

    public void setProjectVersions(List<ProjectVersion> versions){
        this.versionComboBox.setItems(versions);
    }
    public void setReporters(List<Reporter> reporters){
        this.reporterComboBox.setItems(reporters);
    }
    public void setPriority(Report.Priority priority){
        initialPriority = priority;
        prioritySelect.setValue(priority);
    }
    public void setType(Report.Type type){
        initialType = type;
        typeComboBox.setValue(type);
    }
    public void setStatus(Report.Status status){
        initialStatus = status;
        statusComboBox.setValue(status);
    }
    public void setReporter(Reporter reporter){
        initialReporter = reporter;
        reporterComboBox.setValue(reporter);
    }
    public void setVersion(ProjectVersion version){
        initialProjectVersion = version;
        versionComboBox.setValue(version);
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
        typeComboBox.setItems(Report.Type.values());

    }
    private void initStatusComboBox(){
        statusComboBox.setLabel("Status");
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
        reporterComboBox.setRenderer(new ComponentRenderer<>((SerializableFunction<Reporter, Component>) reporter -> {
            if(reporter != null){
                return new Span(reporter.getName());
            }
            return null;
        }));
    }
    private void initVersionComboBox(){
        versionComboBox.setLabel("Version");
        versionComboBox.setRenderer(new ComponentRenderer<>((SerializableFunction<ProjectVersion, Component>) projectVersion -> {
            if(projectVersion != null){
                return new Span(projectVersion.getVersion());
            }
            return new Span();
        }));
    }
    public interface ReportsUpdateListener {
        void onUpdate(Report.Priority priority, Report.Type type, Report.Status status, Reporter reporter, ProjectVersion version);
    }
}
