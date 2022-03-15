package com.vaadin.bugrap.views.component;

import com.vaadin.bugrap.util.TestUtils;
import com.vaadin.bugrap.views.model.GridColumn;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.grid.testbench.GridColumnElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.testbench.TestBenchTestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectVersionComboBoxTest extends TestBenchTestCase {
    @Before
    public void setup() {
        setDriver(new ChromeDriver());
        getDriver().get(TestUtils.TEST_URL);
        waitUntil(TestUtils.waitHomePageLoadCondition());
    }

    @Test
    public void selectAllVersionsAndCheckVersionColumnExists() {
        ComboBoxElement comboBoxElement = $(ComboBoxElement.class).id("version-combo-box");
        comboBoxElement.selectByText("All versions");
        GridElement grid = $(GridElement.class).first();
        GridColumnElement column = grid.getColumn(GridColumn.VERSION.getLabel());
        boolean displayed = column.getHeaderCell().isDisplayed();
        assertTrue(displayed);
        assertTrue(column.getHeaderCell().getWrappedElement().getText().contains("1"));
        GridColumnElement priorityCol = grid.getColumn(GridColumn.PRIORITY.getLabel());
        assertTrue(priorityCol.getHeaderCell().getWrappedElement().getText().contains("2"));

    }

    @Test
    public void selectAVersionAndCheckVersionColumnNotExists() {
        ComboBoxElement comboBoxElement = $(ComboBoxElement.class).id("version-combo-box");
        List<String> options = comboBoxElement.getOptions();
        GridElement grid = $(GridElement.class).first();
        options.stream().filter(f -> !StringUtils.equals("All versions", f)).findFirst().ifPresent(aVersion -> {
            comboBoxElement.selectByText(aVersion);
            List<GridColumnElement> allColumns = grid.getVisibleColumns();
            boolean foundGridVersion = allColumns.stream().anyMatch(f -> f.getHeaderCell().getText().equals(GridColumn.VERSION.getLabel()));
            assertFalse(foundGridVersion);
        });
        GridColumnElement column = grid.getColumn(GridColumn.PRIORITY.getLabel());
        GridTHTDElement headerCell = column.getHeaderCell();
        //TODO can not access shadow dow in selenium 3.1
        assertTrue(headerCell.getWrappedElement().getText().contains("1"));
    }


    @After
    public void tearDown() throws Exception {
        getDriver().quit();
    }


}
