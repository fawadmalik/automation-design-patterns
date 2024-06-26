package org.fmalik.automation.org.fmalik.automation.decoratorPatternImplementation.v4;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WebCoreDriver extends Driver {
    private org.openqa.selenium.WebDriver webDriver;
    private WebDriverWait webDriverWait;
    @Override
    public void start(Browser browser) {
        switch (browser){
            case CHROME:
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
//                webDriver = new EdgeDriver();
                break;
            case OPERA:
                WebDriverManager.operadriver().setup();
//                webDriver = new OperaDriver();
                break;
            case SAFARI:
//                webDriver = new SafariDriver();
                break;
            case INTERNET_EXPLORER:
                WebDriverManager.iedriver().setup();
//                webDriver = new InternetExplorerDriver();
                break;
            default:
                throw new IllegalArgumentException(browser.name());
        }

        webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
    }

    @Override
    public void quit() {
        webDriver.quit();
    }

    @Override
    public void goToUrl(String url) {
        webDriver.navigate().to(url);
    }

    @Override
    public Element findElement(By locator) {
        WebElement nativeWebElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Element element = new WebCoreElement(webDriver, nativeWebElement, locator);
        Element logElement = new LogElement(element);

        return logElement;
    }

    @Override
    public List<Element> findElements(By locator) {
        List<WebElement> nativeWebElements = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        List<Element> elements = new ArrayList<>();
        for (WebElement nativeWebElement : nativeWebElements){
            Element element = new WebCoreElement(webDriver, nativeWebElement, locator);
            Element logElement = new LogElement(element);
            elements.add(element);
        }
        return elements;
    }
}
