package org.fmalik.automation.v4.speedImprovements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebCoreElement extends Element {
    private final WebDriver webDriver;
    private final WebElement webElement;
    private final By by;

    public WebCoreElement(WebDriver webDriver, WebElement webElement, By by) {
        this.webDriver = webDriver;
        this.webElement = webElement;
        this.by = by;
    }

    @Override
    public By getBy() {
        return by;
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public Boolean isEnabled() {
        return webElement.isEnabled();
    }

    @Override
    public Boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    @Override
    public void typeText(String text) {
        sleep(5);
        webElement.clear();
        webElement.sendKeys(text);
    }

    @Override
    public void click() {
        waitToBeClickable(by);
        webElement.click();
    }

    @Override
    public String getAttribute(String attributeName) {
        return webElement.getAttribute(attributeName);
    }

    private void sleep(int seconds){
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitToBeClickable(By by_ref){
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        webDriverWait.until((ExpectedConditions.elementToBeClickable(by_ref)));
    }
}
