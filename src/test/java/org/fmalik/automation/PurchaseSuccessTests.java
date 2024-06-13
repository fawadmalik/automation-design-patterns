package org.fmalik.automation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PurchaseSuccessTests {
    private WebDriver driver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;

    @BeforeMethod
    public void testSetup(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void testTeardown(){
        driver.quit();
    }

    @Test(priority = 1)
    public void verifyPurchaseSuccessWithNewClient(){
        driver.navigate().to("http://demos.bellatrix.solutions/");
        System.out.println("Title: " + driver.getTitle());
        addRocketToShoppingCart();
        applyCoupon();
        increaseProductQuantity();

        WebElement proceedToCheckout = waitAndFindElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();
        WebElement billingFirstName = waitAndFindElement(By.id("billing_first_name"));
        billingFirstName.sendKeys("Anton");
        WebElement bilingLastName = waitAndFindElement(By.id("billing_last_name"));
        bilingLastName.sendKeys("Angelov");
        WebElement billingCompany = waitAndFindElement(By.id("billing_company"));
        billingCompany.sendKeys("Space Flowers");
        WebElement billingCountryWrapper = waitAndFindElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        WebElement billingCountryFilter = waitAndFindElement(By.className("select2-search__field"));
        billingCountryFilter.sendKeys("Germany");
        WebElement germanyOption  = waitAndFindElement(By.xpath("//*[contains(text(), 'Germany')]"));
        germanyOption.click();
        WebElement billingAddress1 = waitAndFindElement(By.id("billing_address_1"));
        billingAddress1.sendKeys("1 Willi Brandt Avenue TearGarten");
        WebElement billingAddress2 = waitAndFindElement(By.id("billing_address_2"));
        billingAddress2.sendKeys("Lotzowplatz");
        WebElement billingCity = waitAndFindElement(By.id("billing_city"));
        billingCity.sendKeys("Berlin");
        WebElement billingZip = waitAndFindElement(By.id("billing_postcode"));
        billingZip.clear();
        billingZip.sendKeys("10115");
        WebElement billingPhone = waitAndFindElement(By.id("billing_phone"));
        billingPhone.sendKeys("+0048888999281");
        WebElement billingEmail = waitAndFindElement(By.id("billing_email"));
        billingEmail.sendKeys("info@berlinspaceflowers.com");
        purchaseEmail = "info@berlinspaceflowers.com";
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement placeOrderButton = waitAndFindElement(By.id("place_order"));
        placeOrderButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement recievedMessage = waitAndFindElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(recievedMessage.getText(), "Order received");
    }

    @Test(priority = 2)
    public void verifyPurchaseSuccessWithExistingClient(){
        driver.navigate().to("http://demos.bellatrix.solutions/");
        addRocketToShoppingCart();
        applyCoupon();
        increaseProductQuantity();

        WebElement proceedToCheckout = waitAndFindElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();

        WebElement loginHereLink = waitAndFindElement(By.linkText("Click here to login"));
        loginHereLink.click();
        login(purchaseEmail);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement placeOrderButton = waitAndFindElement(By.id("place_order"));
        placeOrderButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement recievedMessage = waitAndFindElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(recievedMessage.getText(), "Order received");
        WebElement orderNumber = waitAndFindElement(By.xpath("//*[@id='post-7']//li[1]/strong"));
        purchaseOrderNumber = orderNumber.getText();
    }

    @Test(priority = 3)
    public void verifyCorrectOrderDataDisplayedInMyAccountOrderSection(){
        driver.navigate().to("http://demos.bellatrix.solutions/");
        WebElement myAccountLink = waitAndFindElement(By.linkText("My account"));
        myAccountLink.click();
        login(purchaseEmail);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement orders = waitAndFindElement(By.linkText("Orders"));
        orders.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<WebElement> viewButtons = waitAndFindElements(By.linkText("View"));
        viewButtons.get(0).click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement orderName = waitAndFindElement(By.xpath("//h1"));
        String expectedMessage = String.format("Order #%s", purchaseOrderNumber);
        Assert.assertEquals(expectedMessage, orderName.getText());
    }

    private String GetUserPasswordFromDB(String username) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }

    private WebElement waitAndFindElement(By by){
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    private List<WebElement> waitAndFindElements(By by){
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    private void waitToBeClickable(By by){
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(by));
    }

    private void login(String userName){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement userNameTextField = waitAndFindElement(By.id("username"));
        userNameTextField.sendKeys(userName);
        WebElement passwordField = waitAndFindElement(By.id("password"));
        passwordField.sendKeys(GetUserPasswordFromDB(userName));
        WebElement loginButton = waitAndFindElement(By.xpath("//button[@name='login']"));
        loginButton.click();
    }

    private void applyCoupon() {
        WebElement couponCodeTextField = waitAndFindElement(By.id("coupon_code"));
        couponCodeTextField.clear();
        couponCodeTextField.sendKeys("happybirthday");
        WebElement applyCouponButton = waitAndFindElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement messageAlert = waitAndFindElement(By.cssSelector("[class*='woocommerce-message']"));
        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");
    }

    private void increaseProductQuantity() {
        WebElement quantityBox = waitAndFindElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        quantityBox.sendKeys("2");

        waitToBeClickable(By.cssSelector("[value*='Update cart']"));
        WebElement updateCart = waitAndFindElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement totalSpan = waitAndFindElement(By.xpath("//*[@class='order-total']//span"));
        Assert.assertEquals("114.00€", totalSpan.getText());
    }

    private void addRocketToShoppingCart() {
        WebElement addToCartFalcon9 = waitAndFindElement(By.cssSelector("*[data-product_id*='28']"));
        addToCartFalcon9.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement viewCartButton = waitAndFindElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();
    }
}