package com.vaadin.bugrap.util;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.Nullable;

public class TestUtils {
    //maybe imported from environment
    public static final String TEST_URL = "http://127.0.0.1:8080";

    private TestUtils() {

    }

    public static ExpectedCondition<Object> waitHomePageLoadCondition() {
        return new ExpectedCondition<>() {
            @Override
            public @Nullable
            WebElement apply(@Nullable WebDriver webDriver) {
                if (webDriver == null) {
                    return null;
                }
                WebElement element = webDriver.findElement(By.className("home-page"));
                return element;
            }
        };
    }

    public static String getCurrentUserName(WebDriver driver) {
        if (driver == null) {
            return null;
        }
        org.openqa.selenium.Cookie userNameCookie = driver.manage().getCookieNamed("user_name");
        if (userNameCookie == null) {
            return null;
        }
        return userNameCookie.getValue();
    }

    public static WebElement getShadowRootElement(WebElement element, WebDriver driver) {
        WebElement ele = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);
        return ele;
    }
}
