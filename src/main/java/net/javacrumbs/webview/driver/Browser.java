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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByXPath;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

class Browser extends JFrame implements SearchContext, FindsById, FindsByXPath {
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;

    private final JPanel panel = new JPanel(new BorderLayout());
    private CountDownLatch latch = new CountDownLatch(1);

    public Browser() {
        super();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        createScene();

        panel.add(jfxPanel, BorderLayout.CENTER);

        getContentPane().add(panel);

        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    /**
     * Waits for the URI to be loaded.
     *
     * @param url
     */
    public void loadURL(final String url) {
        WaitListener waitListener = new WaitListener();
        Platform.runLater(() -> {
            waitListener.register(engine);
            String tmp = toURL(url);

            if (url == null) {
                tmp = toURL("http://" + url);
            }
            engine.load(tmp);
        });
        waitListener.waitForIt();
        Platform.runLater(() -> waitListener.unregister(engine));
    }

    private static String toURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }

    private void createScene() {
        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();

            engine.titleProperty().addListener((observable, oldValue, newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    Browser.this.setTitle(newValue);
                });
            });

            engine.getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        System.out.println("webEngine result " + newState.toString());
                    }
            );
            StackPane root = new StackPane();
            root.getChildren().add(view);
            Scene scene = new Scene(root, 1200, 800);
            jfxPanel.setScene(scene);
        });
    }

    @Override
    public List<WebElement> findElements(By by) {
        return by.findElements(this);
    }

    public WebElement findElement(By by) {
        return by.findElement(this);
    }

    @Override
    public WebElement findElementById(String using) {
        return new NodeWebElement(engine.getDocument().getElementById(using));
    }

    @Override
    public List<WebElement> findElementsById(String using) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WebElement findElementByXPath(String using) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.evaluate(using, engine.getDocument(), XPathConstants.NODE);
            return new NodeWebElement(node);
        } catch (XPathExpressionException e) {
            //FIXME
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WebElement> findElementsByXPath(String using) {
        return null;
    }

    private static final class WaitListener implements ChangeListener<Worker.State> {
        private static final Set<Worker.State> END_STATES = EnumSet.of(Worker.State.SUCCEEDED, Worker.State.CANCELLED, Worker.State.FAILED);
        private final CountDownLatch latch = new CountDownLatch(1);

        private void register(WebEngine engine) {
            engine.getLoadWorker().stateProperty().addListener(this);
        }

        private void unregister(WebEngine engine) {
            engine.getLoadWorker().stateProperty().removeListener(this);
        }

        @Override
        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
            if (END_STATES.contains(newValue)) {
                latch.countDown();
            }
        }

        public void waitForIt() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                //FIXME
                throw new RuntimeException(e);
            }
        }
    }
}
