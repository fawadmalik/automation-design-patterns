package org.fmalik.automation.v4.speedImprovements.waitForAjax;

import org.fmalik.automation.v4.speedImprovements.Browser;
import org.fmalik.automation.v4.speedImprovements.Driver;
import org.fmalik.automation.v4.speedImprovements.LoggingDriver;
import org.fmalik.automation.v4.speedImprovements.WebCoreDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AjaxSupportedPurchaseSuccessTests {
    private static String purchaseEmail;
    private static String purchaseOrderNumber;
    private Driver driver;

    @BeforeMethod
    public void testInit() {
        driver = new LoggingDriver(new WebCoreDriver());
        driver.start(Browser.CHROME);
    }

    @AfterMethod
    public void testCleanup() throws InterruptedException {
        driver.quit();
    }

    @Test(priority = 1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        driver.goToUrl("http://demos.bellatrix.solutions/");
        var addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        applyCoupon();
        increaseProductQuantity();

        var proceedToCheckout = driver.findElementAndMoveToIt(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();
        driver.waitUntilPageLoadsCompletely();

        var billingFirstName = driver.findElement(By.id("billing_first_name"));
        billingFirstName.typeText("Anton");
        var billingLastName = driver.findElement(By.id("billing_last_name"));
        billingLastName.typeText("Angelov");
        var billingCompany = driver.findElement(By.id("billing_company"));
        billingCompany.typeText("Space Flowers");
        var billingCountryWrapper = driver.findElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        var billingCountryFilter = driver.findElement(By.className("select2-search__field"));
        billingCountryFilter.typeText("Germany");
        var germanyOption = driver.findElement(By.xpath("//*[contains(text(),'Germany')]"));
        germanyOption.click();
        var billingAddress1 = driver.findElement(By.id("billing_address_1"));
        billingAddress1.typeText("1 Willi Brandt Avenue Tiergarten");
        var billingAddress2 = driver.findElement(By.id("billing_address_2"));
        billingAddress2.typeText("Lotzowplatz 17");
        var billingCity = driver.findElement(By.id("billing_city"));
        billingCity.typeText("Berlin");
        var billingZip = driver.findElement(By.id("billing_postcode"));
        billingZip.typeText("10115");
        var billingPhone = driver.findElement(By.id("billing_phone"));
        billingPhone.typeText("+00498888999281");
        var billingEmail = driver.findElement(By.id("billing_email"));
        billingEmail.typeText("info@berlinspaceflowers.com");
        purchaseEmail = "info@berlinspaceflowers.com";

        // This pause will be removed when we introduce a logic for waiting for AJAX requests.
        driver.waitForAjax();
        var placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();

        driver.waitForAjax();
//        var receivedMessage = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/main/div/header/h1"));
        var receivedMessage = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(receivedMessage.getText(), "Order received");
    }

    @Test(priority = 2)
    public void completePurchaseSuccessfully_whenExistingClient() throws InterruptedException {
        driver.goToUrl("http://demos.bellatrix.solutions/");

        var addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        applyCoupon();
        increaseProductQuantity();

        var proceedToCheckout = driver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();
        driver.waitUntilPageLoadsCompletely();

        var loginHereLink = driver.findElement(By.linkText("Click here to login"));
        loginHereLink.click();
        var userName = driver.findElement(By.id("username"));
        driver.waitForAjax();
        userName.typeText(purchaseEmail);
        var password = driver.findElement(By.id("password"));
        password.typeText(GetUserPasswordFromDb(purchaseEmail));
        var loginButton = driver.findElement(By.xpath("//button[@name='login']"));
        loginButton.click();

        driver.waitForAjax();
        var placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();

        var receivedMessage = driver.findElement(By.xpath("//h1[text() = 'Order received']"));
        Assert.assertEquals(receivedMessage.getText(), "Order received");

        var orderNumber = driver.findElement(By.xpath("//*[@id='post-7']//li[1]/strong"));
        purchaseOrderNumber = orderNumber.getText();
    }

    @Test(priority = 3)
    public void correctOrderDataDisplayed_whenNavigateToMyAccountOrderSection() throws InterruptedException {
        driver.goToUrl("http://demos.bellatrix.solutions/");

        var myAccountLink = driver.findElement(By.linkText("My account"));
        myAccountLink.click();
        var userName = driver.findElement(By.id("username"));
        driver.waitForAjax();
        userName.typeText(purchaseEmail);
        var password = driver.findElement(By.id("password"));
        password.typeText(GetUserPasswordFromDb(GetUserPasswordFromDb(purchaseEmail)));
        var loginButton = driver.findElement(By.xpath("//button[@name='login']"));
        loginButton.click();

        var orders = driver.findElement(By.linkText("Orders"));
        orders.click();

        var viewButtons = driver.findElements(By.linkText("View"));
        viewButtons.get(0).click();

        var orderName = driver.findElement(By.xpath("//h1"));
        String expectedMessage = String.format("Order #%s", purchaseOrderNumber);
        Assert.assertEquals(expectedMessage, orderName.getText());
    }

    private void increaseProductQuantity() {
        var quantityBox = driver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.typeText("2");
        driver.waitForAjax();
        var updateCart = driver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        driver.waitForAjax();
        var totalSpan = driver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assert.assertEquals("114.00€", totalSpan.getText());
    }

    private void applyCoupon() {
        var couponCodeTextField = driver.findElement(By.id("coupon_code"));
        couponCodeTextField.typeText("happybirthday");
        var applyCouponButton = driver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        driver.waitForAjax();
        var messageAlert = driver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");
    }

    private String GetUserPasswordFromDb(String userName) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }
}