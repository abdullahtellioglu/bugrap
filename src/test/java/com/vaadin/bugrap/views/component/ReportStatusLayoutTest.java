package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.util.TestUtils;
import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.contextmenu.testbench.ContextMenuElement;
import com.vaadin.flow.component.grid.testbench.GridColumnElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.grid.testbench.GridTRElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class ReportStatusLayoutTest extends TestBenchTestCase {
    @Before
    public void setup() {
        setDriver(new ChromeDriver());
        getDriver().get(TestUtils.TEST_URL);
        waitUntil(TestUtils.waitHomePageLoadCondition());
    }

    @Test
    public void clickOnlyMeBtnAndCheckGridItemContainsOnlyAssignedToMe() {
        String currentUserName = TestUtils.getCurrentUserName(getDriver());

        ButtonElement onlyMeBtn = $(ButtonElement.class).id("only-me-btn");
        onlyMeBtn.click();
        GridElement gridElement = $(GridElement.class).first();
        GridColumnElement column = gridElement.getColumn(GridColumn.ASSIGNED_TO.getLabel());
        int rowCount = gridElement.getRowCount();
        List<String> assigneeNames = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            GridTRElement row = gridElement.getRow(i);
            GridTHTDElement cell = row.getCell(column);
            String text = cell.getText();
            assigneeNames.add(text);
        }
        Assert.assertTrue(assigneeNames.stream().allMatch(k -> StringUtils.equals(currentUserName, k)));
    }

    @Test
    public void clickEveryOneBtnAndCheckGridItemContainsEveryone() {
        String currentUserName = TestUtils.getCurrentUserName(getDriver());
        ButtonElement everyOneBtn = $(ButtonElement.class).id("everyone-btn");
        everyOneBtn.click();
        GridElement gridElement = $(GridElement.class).first();
        GridColumnElement column = gridElement.getColumn(GridColumn.ASSIGNED_TO.getLabel());
        int rowCount = gridElement.getRowCount();
        List<String> assigneeNames = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            GridTRElement row = gridElement.getRow(i);
            GridTHTDElement cell = row.getCell(column);
            String text = cell.getText();
            assigneeNames.add(text);
        }
        //assignee to me
        Assert.assertTrue(assigneeNames.stream().anyMatch(k -> StringUtils.equals(currentUserName, k)));
        // assignee to not just me
        Assert.assertTrue(assigneeNames.stream().anyMatch(k -> !StringUtils.equals(currentUserName, k)));
    }

    @Test
    public void clickCustomStatusBtnAndSelectAll() {
        ButtonElement customBtn = $(ButtonElement.class).id("custom-btn");
        customBtn.click();

        ContextMenuElement contextMenuElement = $(ContextMenuElement.class).first();
        getDriver().findElement(By.id("overlay"));
        List<TestBenchElement> items = contextMenuElement.$("vaadin-context-menu-item").all();
        items.forEach(item -> {
            if (!item.hasAttribute("menu-item-checked")) {
                item.click();
            }
        });

    }

    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }

}
