package com.vaadin.bugrap.views.component;


import com.vaadin.bugrap.util.TestUtils;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.testbench.TestBenchTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class ProjectToolbarLayoutTest extends TestBenchTestCase {
    @Before
    public void setup() {
        setDriver(new ChromeDriver());
        getDriver().get(TestUtils.TEST_URL);
        waitUntil(TestUtils.waitHomePageLoadCondition());
    }

    @Test
    public void checkBugShowsNotification() throws InterruptedException {
        clickAndWaitNotImplementedNotification($(ButtonElement.class).id("report-bug"));
        waitUntil(count -> $(NotificationElement.class).all().isEmpty());
        clickAndWaitNotImplementedNotification($(ButtonElement.class).id("request-feature-btn"));
        waitUntil(count -> $(NotificationElement.class).all().isEmpty());
        clickAndWaitNotImplementedNotification($(ButtonElement.class).id("manage-project-btn"));
    }

    private void clickAndWaitNotImplementedNotification(ButtonElement buttonElement) throws InterruptedException {
        buttonElement.click();
        Thread.sleep(500);
        List<NotificationElement> elements = $(NotificationElement.class).all();
        Assert.assertTrue(elements.stream().anyMatch(f -> f.getText().equals("Not implemented")));
    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }
}
