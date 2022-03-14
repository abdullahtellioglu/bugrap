package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.views.pages.HomePage;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Full screen not found report layout.
 */
public class NotFoundLayout extends VerticalLayout {
    public NotFoundLayout(){
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
        setClassName("not-found-layout");

        Image image = new Image("images/not-found-illustrator.jpeg", "Not found");
        image.setWidth(300, Unit.PIXELS);
        image.setHeight(300, Unit.PIXELS);

        H1 headerSpan = new H1("Report not found");
        headerSpan.setClassName("header");

        H4 descriptionSpan = new H4("Report is probably deleted. Please select another report to display details");
        descriptionSpan.setClassName("description");

        Button goHomeButton = new Button("Main Page");
        goHomeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(image);
        add(headerSpan);
        add(descriptionSpan);
        add(goHomeButton);

        goHomeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            getUI().ifPresent(ui -> ui.navigate(HomePage.class));
        });

    }
}
