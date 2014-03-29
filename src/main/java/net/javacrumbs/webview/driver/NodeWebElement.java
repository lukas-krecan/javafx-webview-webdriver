/**
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.javacrumbs.webview.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLInputElement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NodeWebElement implements WebElement {
    private final Node node;

    public NodeWebElement(Node element) {
        this.node = element;
    }

    @Override
    public void click() {
        if (node instanceof HTMLInputElement) {
            ((HTMLInputElement) node).click();
        }
    }

    @Override
    public void submit() {
        if (node instanceof HTMLInputElement) {
            ((HTMLInputElement) node).getForm().submit();
        }
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        String value = Arrays.stream(keysToSend).collect(Collectors.joining());
        if (node instanceof HTMLInputElement) {
            ((HTMLInputElement) node).setValue(value);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return null;
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public Point getLocation() {
        return null;
    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public String getCssValue(String propertyName) {
        return null;
    }
}
