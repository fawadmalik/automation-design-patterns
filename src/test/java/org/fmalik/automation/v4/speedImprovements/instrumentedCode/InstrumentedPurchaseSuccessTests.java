package org.fmalik.automation.v4.speedImprovements.instrumentedCode;

import com.google.common.base.Stopwatch;
import org.fmalik.automation.v4.speedImprovements.Browser;
import org.fmalik.automation.v4.speedImprovements.Driver;
import org.fmalik.automation.v4.speedImprovements.LoggingDriver;
import org.fmalik.automation.v4.speedImprovements.WebCoreDriver;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class InstrumentedPurchaseSuccessTests {
    private Driver driver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;
    private static Stopwatch stopwatch;

    @BeforeMethod
    public void testInit() {
        stopwatch = Stopwatch.createStarted();
        driver = new LoggingDriver(new WebCoreDriver());
        driver.start(Browser.CHROME);

        System.out.printf("end browser init: %d", stopwatch.elapsed(TimeUnit.SECONDS));
    }

    @AfterMethod
    public void testCleanup() {
        driver.quit();
        System.out.printf("afterTest: %d", stopwatch.elapsed(TimeUnit.SECONDS));
        stopwatch.stop();
    }

    @Test(priority = 1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        System.out.printf("start completePurchaseSuccessfully_whenNewClient: %d", stopwatch.elapsed(TimeUnit.SECONDS));
        driver.goToUrl("http://demos.bellatrix.solutions/");
        var addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        applyCoupon();
        increaseProductQuantity();
        Thread.sleep(3000);
//        var proceedToCheckout = driver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        var proceedToCheckout = driver.findElementAndMoveToIt(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        Thread.sleep(3000);
        proceedToCheckout.click();

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
        Thread.sleep(5000);
        var placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();

        Thread.sleep(10000);
        var receivedMessage = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(receivedMessage.getText(), "Order received");

        System.out.printf("end completePurchaseSuccessfully_whenNewClient: %d", stopwatch.elapsed(TimeUnit.SECONDS));
    }

    @Test(priority = 2)
    public void completePurchaseSuccessfully_whenExistingClient() throws InterruptedException {
        System.out.printf("start completePurchaseSuccessfully_whenExistingClient: %d", stopwatch.elapsed(TimeUnit.SECONDS));

        driver.goToUrl("http://demos.bellatrix.solutions/");

        var addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

//        var couponCodeTextField = driver.findElement(By.id("coupon_code"));
//        couponCodeTextField.typeText("happybirthday");
//        var applyCouponButton = driver.findElement(By.cssSelector("[value*='Apply coupon']"));
//        applyCouponButton.click();
//        Thread.sleep(4000);
//        var messageAlert = driver.findElement(By.cssSelector("[class*='woocommerce-message']"));
//        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");
//
//        var quantityBox = driver.findElement(By.cssSelector("[class*='input-text qty text']"));
//        quantityBox.typeText("2");
//        var updateCart = driver.findElement(By.cssSelector("[value*='Update cart']"));
//        updateCart.click();
//        Thread.sleep(4000);
//        var totalSpan = driver.findElement(By.xpath("//*[@class='order-total']//span"));
//        Assert.assertEquals("114.00€", totalSpan.getText());

        applyCoupon();
        increaseProductQuantity();

//        var proceedToCheckout = driver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        var proceedToCheckout = driver.findElementAndMoveToIt(By.cssSelector("[class*='checkout-button button alt wc-forward']"));

        proceedToCheckout.click();

        var loginHereLink = driver.findElement(By.linkText("Click here to login"));
        loginHereLink.click();
        var userName = driver.findElement(By.id("username"));
        Thread.sleep(5000);
        userName.typeText(purchaseEmail);
        var password = driver.findElement(By.id("password"));
        password.typeText(GetUserPasswordFromDb(purchaseEmail));
        var loginButton = driver.findElement(By.xpath("//button[@name='login']"));
        loginButton.click();

        // This pause will be removed when we introduce a logic for waiting for AJAX requests.
        Thread.sleep(5000);
        var placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();

        var receivedMessage = driver.findElement(By.xpath("//h1[text() = 'Order received']"));
        Assert.assertEquals(receivedMessage.getText(), "Order received");

        var orderNumber = driver.findElement(By.xpath("//*[@id='post-7']//li[1]/strong"));
        purchaseOrderNumber = orderNumber.getText();

        System.out.printf("end completePurchaseSuccessfully_whenExistingClient: %d", stopwatch.elapsed(TimeUnit.SECONDS));
    }

    @Test(priority = 3)
    public void correctOrderDataDisplayed_whenNavigateToMyAccountOrderSection() throws InterruptedException {
        System.out.printf("start correctOrderDataDisplayed_whenNavigateToMyAccountOrderSection: %d", stopwatch.elapsed(TimeUnit.SECONDS));
        driver.goToUrl("http://demos.bellatrix.solutions/");

        var myAccountLink = driver.findElement(By.linkText("My account"));
        myAccountLink.click();
        var userName = driver.findElementAndMoveToIt(By.id("username"));
        Thread.sleep(4000);
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

        System.out.printf("end correctOrderDataDisplayed_whenNavigateToMyAccountOrderSection: %d", stopwatch.elapsed(TimeUnit.SECONDS));
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

    private String GetUserPasswordFromDb(String userName) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }
}
