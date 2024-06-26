package org.fmalik.automation.instrumentationForSpeedImprovement.v4;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DecoratedPurchaseSuccessTests {
    private Driver driver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;

    @BeforeMethod
    public void testInit(){
        driver = new LoggingDriver(new WebCoreDriver());
        driver.start(Browser.CHROME);
    }

    @AfterMethod
    public void testCleanup(){
        driver.quit();
    }

    @Test(priority = 1)
    public void verifyPurchaseSuccessWithNewClient(){
        driver.goToUrl("http://demos.bellatrix.solutions/");
        addRocketToShoppingCart();
        applyCoupon();

        Element messageAlert = driver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        increaseProductQuantity();

        Element updateCart = driver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        sleep(4);
        Element totalSpan = driver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assert.assertEquals("114.00€", totalSpan.getText());

        Element proceedToCheckout = driver.findElement(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();

        Element billingFirstName = driver.findElement(By.id("billing_first_name"));
        billingFirstName.typeText("Anton");
        Element bilingLastName = driver.findElement(By.id("billing_last_name"));
        bilingLastName.typeText("Angelov");
        Element billingCompany = driver.findElement(By.id("billing_company"));
        billingCompany.typeText("Space Flowers");
        Element billingCountryWrapper = driver.findElement(By.id("select2-billing_country-container"));
        billingCountryWrapper.click();
        Element billingCountryFilter = driver.findElement(By.className("select2-search__field"));
        billingCountryFilter.typeText("Germany");
        Element germanyOption  = driver.findElement(By.xpath("//*[contains(text(), 'Germany')]"));
        germanyOption.click();
        Element billingAddress1 = driver.findElement(By.id("billing_address_1"));
        billingAddress1.typeText("1 Willi Brandt Avenue TearGarten");
        Element billingAddress2 = driver.findElement(By.id("billing_address_2"));
        billingAddress2.typeText("Lotzowplatz");
        Element billingCity = driver.findElement(By.id("billing_city"));
        billingCity.typeText("Berlin");
        Element billingZip = driver.findElement(By.id("billing_postcode"));
        billingZip.typeText("10115");
        Element billingPhone = driver.findElement(By.id("billing_phone"));
        billingPhone.typeText("+0048888999281");
        Element billingEmail = driver.findElement(By.id("billing_email"));
        billingEmail.typeText("info@berlinspaceflowers.com");
        purchaseEmail = "info@berlinspaceflowers.com";

        sleep(5);
        Element placeOrderButton = driver.findElement(By.id("place_order"));
        placeOrderButton.click();
        sleep(10);
        Element recievedMessage = driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/article/header/h1"));
        Assert.assertEquals(recievedMessage.getText(), "Order received");
    }

    private void applyCoupon() {
        Element couponCodeTextField = driver.findElement(By.id("coupon_code"));
        couponCodeTextField.typeText("happybirthday");
        Element applyCouponButton = driver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        sleep(4);
    }

    private void addRocketToShoppingCart() {
        Element addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        Element viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();
    }

    private void increaseProductQuantity() {
        Element quantityBox = driver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.typeText("2");
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}