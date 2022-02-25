package com.vaadin.bugrap.views.component.overview;

import com.vaadin.bugrap.views.component.PriorityBar;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.List;

public class OverviewUpdateBar extends HorizontalLayout {
    private ReportsUpdateListener listener;

    //views
    private final Select<Report.Priority> prioritySelect = new Select<>();
    private final Select<Report.Type> typeSelect = new Select<>();
    private final Select<Report.Status> statusSelect = new Select<>();
    private final Select<Reporter> reporterSelect = new Select<>();
    private final Select<ProjectVersion> versionSelect = new Select<>();

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
        initTypeSelect();
        initStatusSelect();
        initReporterSelect();
        initVersionSelect();


        HorizontalLayout selectContainers = new HorizontalLayout(prioritySelect, typeSelect, statusSelect, reporterSelect, versionSelect);


        Button saveChangesButton = new Button("Save Changes");
        saveChangesButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button revertButton = new Button("Revert", VaadinIcon.ROTATE_LEFT.create());
        HorizontalLayout buttonContainers = new HorizontalLayout(saveChangesButton, revertButton);

        revertButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            prioritySelect.setValue(initialPriority);
            typeSelect.setValue(initialType);
            statusSelect.setValue(initialStatus);
            reporterSelect.setValue(initialReporter);
            versionSelect.setValue(initialProjectVersion);
        });

        saveChangesButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            if(listener != null){
                listener.onUpdate(
                        prioritySelect.getValue(),
                        typeSelect.getValue(),
                        statusSelect.getValue(),
                        reporterSelect.getValue(),
                        versionSelect.getValue());
            }
        });

        add(selectContainers);
        add(buttonContainers);
    }


    public void setListener(ReportsUpdateListener listener) {
        this.listener = listener;
    }

    public void setProjectVersions(List<ProjectVersion> versions){
        this.versionSelect.setItems(versions);
    }
    public void setPriority(Report.Priority priority){
        initialPriority = priority;
        prioritySelect.setValue(priority);
    }
    public void setType(Report.Type type){
        initialType = type;
        typeSelect.setValue(type);
    }
    public void setStatus(Report.Status status){
        initialStatus = status;
        statusSelect.setValue(status);
    }
    public void setReporter(Reporter reporter){
        initialReporter = reporter;
        reporterSelect.setValue(reporter);
    }
    public void setVersion(ProjectVersion version){
        initialProjectVersion = version;
        versionSelect.setValue(version);
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
    private void initTypeSelect(){
        typeSelect.setLabel("Type");
        typeSelect.setItems(Report.Type.values());

    }
    private void initStatusSelect(){
        statusSelect.setLabel("Status");
        statusSelect.setItems(Report.Status.values());
        statusSelect.setRenderer(new ComponentRenderer<>((SerializableFunction<Report.Status, Component>) status -> {
            if(status != null){
                return new Span(status.toString());
            }
            return null;
        }));
    }
    private void initReporterSelect(){
        reporterSelect.setLabel("Assignee");
        // TODO how to set all reporters
        reporterSelect.setRenderer(new ComponentRenderer<>((SerializableFunction<Reporter, Component>) reporter -> {
            if(reporter != null){
                reporter.getName();
            }
            return null;
        }));
    }
    private void initVersionSelect(){
        versionSelect.setLabel("Version");
        versionSelect.setRenderer(new ComponentRenderer<>((SerializableFunction<ProjectVersion, Component>) projectVersion -> {
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
