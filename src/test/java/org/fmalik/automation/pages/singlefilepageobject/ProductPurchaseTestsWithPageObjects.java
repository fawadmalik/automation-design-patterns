/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fmalik.automation.pages.singlefilepageobject;

import org.fmalik.automation.core.Browser;
import org.fmalik.automation.core.Driver;
import org.fmalik.automation.core.LoggingDriver;
import org.fmalik.automation.core.WebCoreDriver;
import org.fmalik.automation.pages.v0.singlefilepageobject.CartPage;
import org.fmalik.automation.pages.v0.singlefilepageobject.MainPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductPurchaseTestsWithPageObjects {
    private Driver driver;
    private static String purchaseEmail;
    private static String purchaseOrderNumber;
    private static MainPage mainPage;
    private static CartPage cartPage;

    @BeforeMethod
    public void testInit() {
        driver = new LoggingDriver(new WebCoreDriver());
        driver.start(Browser.CHROME);
        mainPage = new MainPage(driver);
        cartPage = new CartPage(driver);
    }

    @AfterMethod
    public void testCleanup() throws InterruptedException {
        driver.quit();
    }

    @Test(priority = 1)
    public void completePurchaseSuccessfully_whenNewClient() throws InterruptedException {
        mainPage.addRocketToShoppingCart();
        cartPage.applyCoupon("happybirthday");

        Assert.assertEquals(cartPage.getMessageNotification(), "Coupon code applied successfully.");

        cartPage.increaseProductQuantity(2);

        Assert.assertEquals("114.00€", cartPage.getTotal());

        driver.waitForAjax();
        cartPage.clickProceedToCheckout();
        driver.waitForAjax();
        var receivedMessage = driver.findElement(By.xpath("//h1"));
        Assert.assertEquals(receivedMessage.getText(), "Checkout");
    }

    @Test(priority = 2)
    public void completePurchaseSuccessfully_whenExistingClient() throws InterruptedException {
        driver.goToUrl("http://demos.bellatrix.solutions/");

        var addToCartFalcon9 = driver.findElement(By.cssSelector("[data-product_id*='28']"));
        addToCartFalcon9.click();
        var viewCartButton = driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
        viewCartButton.click();

        var couponCodeTextField = driver.findElement(By.id("coupon_code"));
        couponCodeTextField.typeText("happybirthday");
        var applyCouponButton = driver.findElement(By.cssSelector("[value*='Apply coupon']"));
        applyCouponButton.click();
        var messageAlert = driver.findElement(By.cssSelector("[class*='woocommerce-message']"));
        driver.waitForAjax();
        Assert.assertEquals(messageAlert.getText(), "Coupon code applied successfully.");

        var quantityBox = driver.findElement(By.cssSelector("[class*='input-text qty text']"));
        quantityBox.typeText("2");
        driver.waitForAjax();
        var updateCart = driver.findElement(By.cssSelector("[value*='Update cart']"));
        updateCart.click();
        driver.waitForAjax();
        var totalSpan = driver.findElement(By.xpath("//*[@class='order-total']//span"));
        Assert.assertEquals(totalSpan.getText(), "114.00€");

        var proceedToCheckout = driver.findElementAndMoveToIt(By.cssSelector("[class*='checkout-button button alt wc-forward']"));
        proceedToCheckout.click();
        driver.waitUntilPageLoadsCompletely();

        driver.waitForAjax();
        var loginHereLink = driver.findElement(By.linkText("Click here to login"));
        loginHereLink.click();
        var userName = driver.findElement(By.id("username"));
        driver.waitForAjax();
        purchaseEmail = "info@berlinspaceflowers.com";
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

    private String GetUserPasswordFromDb(String userName) {
        return "@purISQzt%%DYBnLCIhaoG6$";
    }
}