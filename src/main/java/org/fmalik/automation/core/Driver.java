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

package org.fmalik.automation.core;

import org.openqa.selenium.By;

import java.util.List;

public abstract class Driver {
    public abstract void start(Browser browser);

    public abstract void quit();

    public abstract void goToUrl(String url);

    public abstract Element findElement(By locator);

    public abstract List<Element> findElements(By locator);

    public abstract void waitForAjax();

    public abstract void waitUntilPageLoadsCompletely();

    public abstract Element findElementAndMoveToIt(By locator);
}
