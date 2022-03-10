package com.vaadin.bugrap.views.container;

import com.vaadin.bugrap.views.pages.HomePage;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;

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
        Span headerSpan = new Span("Report not found");
        headerSpan.setClassName("header");
        Span descriptionSpan = new Span("Report is probably deleted. Please select another report to display details");
        descriptionSpan.setClassName("description");
        Button goHomeButton = new Button("Main Page");
        goHomeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(image);
        add(headerSpan);
        add(descriptionSpan);
        add(goHomeButton);

        goHomeButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) event -> {
            RouteConfiguration routeConfiguration = RouteConfiguration.forSessionScope();
            String url = routeConfiguration.getUrl(HomePage.class);
            UI.getCurrent().navigate(url);
        });

    }
}
