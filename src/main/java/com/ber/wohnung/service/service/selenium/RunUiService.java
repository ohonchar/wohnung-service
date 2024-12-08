package com.ber.wohnung.service.service.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RunUiService {
    private WebDriver driver;

    public void executeUi() {
        executeUi("");
    }

    public void executeUi(String s) {
        WebDriverManager.chromedriver().setup();
//        driver = selenoidCapabilities("http://localhost:4444/wd/hub");
        driver = new ChromeDriver();
        driver.get("https://www.google.com");

        driver.findElement(By.xpath("//div[text()='Alle ablehnen']")).click();
        driver.findElement(By.xpath("//form[@action='/search']//textarea")).sendKeys(s);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.quit();
    }

    private static RemoteWebDriver selenoidCapabilities(String serverUrl) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", new HashMap<String, Object>() {
            {
                put("profile.default_content_settings.popups", 0);
                put("download.default_directory", "/home/selenium/Downloads");
                put("download.prompt_for_download", false);
                put("download.directory_upgrade", true);
                put("safebrowsing.enabled", false);
                put("plugins.always_open_pdf_externally", true);
                put("plugins.plugins_disabled", new ArrayList<String>() {
                    {
                        add("Chrome PDF Viewer");
                    }
                });
            }
        });
        chromeOptions.setCapability("selenoid:options", new HashMap<String, Object>() {{
            /* How to add test badge */
            put("name", "Test badge...");

            /* How to set session timeout */
            put("sessionTimeout", "15m");

            /* How to set timezone */
            put("env", new ArrayList<String>() {{
                add("TZ=UTC");
            }});

            /* How to add "trash" button */
            put("labels", new HashMap<String, Object>() {{
                put("manual", "true");
            }});


            put("enableVNC", true);

            /* How to enable video recording */
//            put("enableVideo", true);
        }});

        RemoteWebDriver driver = null;
        try {
            driver = new RemoteWebDriver(
                    URI.create(serverUrl).toURL(),
                    chromeOptions
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(driver).setFileDetector(new LocalFileDetector());
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;
    }
}
