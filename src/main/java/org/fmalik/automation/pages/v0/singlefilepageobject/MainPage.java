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

package org.fmalik.automation.pages.v0.singlefilepageobject;

import org.fmalik.automation.core.Driver;
import org.fmalik.automation.core.Element;
import org.openqa.selenium.By;

public class MainPage {
    private final Driver driver;
    private final String url = "http://demos.bellatrix.solutions/";

    public MainPage(Driver driver) {
        this.driver = driver;
    }

    private Element addToCartFalcon9() {
        return driver.findElement(By.cssSelector("[data-product_id*='28']"));
    }

    private Element viewCartButton() {
        return driver.findElement(By.cssSelector("[class*='added_to_cart wc-forward']"));
    }

    public void addRocketToShoppingCart() {
        driver.goToUrl(url);
        addToCartFalcon9().click();
        viewCartButton().click();
    }
}
