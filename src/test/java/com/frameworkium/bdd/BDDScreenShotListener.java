package com.frameworkium.bdd;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.capture.ScreenshotCapture;
import com.frameworkium.core.ui.driver.DriverSetup;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BDDScreenShotListener extends TestListenerAdapter {

    private static final Logger logger = LogManager.getLogger();
    private final boolean captureEnabled = ScreenshotCapture.isRequired();

    public BDDScreenShotListener() {
    }

    public void onTestFailure(ITestResult failingTest) {
        if (!this.captureEnabled && this.isScreenshotSupported(failingTest)) {
            this.takeScreenshotAndSaveLocally(failingTest.getName());
        }

    }

    public void onTestSkipped(ITestResult skippedTest) {
        if (!this.captureEnabled && this.isScreenshotSupported(skippedTest)) {
            this.takeScreenshotAndSaveLocally(skippedTest.getName());
        }

    }

    private void takeScreenshotAndSaveLocally(String testName) {
        this.takeScreenshotAndSaveLocally(testName.replace("\"", ""), (TakesScreenshot) UITestLifecycle.get().getWebDriver());
    }

    private void takeScreenshotAndSaveLocally(String testName, TakesScreenshot driver) {
        String screenshotDirectory = System.getProperty("screenshotDirectory");
        if (screenshotDirectory == null) {
            screenshotDirectory = "screenshots";
        }

        String fileName = String.format("%s_%s.png", System.currentTimeMillis(), testName);
        Path screenshotPath = Paths.get(screenshotDirectory);
        Path absolutePath = screenshotPath.resolve(fileName);
        if (this.createScreenshotDirectory(screenshotPath)) {
            this.writeScreenshotToFile(driver, absolutePath);
            logger.info("Written screenshot to " + absolutePath);
        } else {
            logger.error("Unable to create " + screenshotPath);
        }

    }

    private boolean createScreenshotDirectory(Path screenshotDirectory) {
        try {
            Files.createDirectories(screenshotDirectory);
        } catch (IOException var3) {
            logger.error("Error creating screenshot directory", var3);
        }

        return Files.isDirectory(screenshotDirectory, new LinkOption[0]);
    }

    @Attachment(
            value = "Screenshot on failure",
            type = "image/png"
    )
    private byte[] writeScreenshotToFile(TakesScreenshot driver, Path screenshot) {
        try {
            OutputStream screenshotStream = Files.newOutputStream(screenshot);
            Throwable var4 = null;

            byte[] var6;
            try {
                byte[] bytes = (byte[])driver.getScreenshotAs(OutputType.BYTES);
                screenshotStream.write(bytes);
                screenshotStream.close();
                var6 = bytes;
            } catch (Throwable var17) {
                var4 = var17;
                throw var17;
            } finally {
                if (screenshotStream != null) {
                    if (var4 != null) {
                        try {
                            screenshotStream.close();
                        } catch (Throwable var16) {
                            var4.addSuppressed(var16);
                        }
                    } else {
                        screenshotStream.close();
                    }
                }

            }

            return var6;
        } catch (IOException var19) {
            logger.error("Unable to write " + screenshot, var19);
        } catch (WebDriverException var20) {
            logger.error("Unable to take screenshot.", var20);
        }

        return null;
    }

    private boolean isScreenshotSupported(ITestResult testResult) {
        boolean isElectron = Property.BROWSER.isSpecified() && DriverSetup.Browser.ELECTRON.equals(DriverSetup.Browser.valueOf(Property.BROWSER.getValue().toUpperCase()));
        boolean isUITest = UITestLifecycle.get().getWebDriver() != null;
        return isUITest && !isElectron;
    }

}
