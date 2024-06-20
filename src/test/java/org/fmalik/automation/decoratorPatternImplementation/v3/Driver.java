package org.fmalik.automation.decoratorPatternImplementation.v3;

import org.openqa.selenium.By;

import java.util.List;

public abstract class Driver {
    public abstract void start(Browser browser);
    public abstract void quit();
    public abstract void gotToUrl(String url);
    public abstract Element findElement(By locator);
    public abstract List<Element> findElements(By locator);
}
