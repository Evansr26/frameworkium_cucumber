package com.frameworkium.bdd;

import com.frameworkium.core.ui.UITestLifecycle;
import com.frameworkium.core.ui.listeners.CaptureListener;
import gherkin.events.PickleEvent;
import io.cucumber.testng.CucumberFeatureWrapper;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.PickleEventWrapper;
import io.cucumber.testng.TestNGCucumberRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

@Listeners({CaptureListener.class, BDDScreenShotListener.class})
@CucumberOptions(
        strict = true,
        features = "src/test/resources/features/",
        plugin = {
                "pretty", // pretty console logging
                "json:cucumber-results.json" // json results file
        },
        monochrome = true,
        tags = "@example",
        // NB: change these to match your glue packages.
        glue = "com.ten10.example.stepdefs")

public class TestRunner implements ITest {

    private static final Logger logger = LogManager.getLogger();

    private TestNGCucumberRunner testNGCucumberRunner;
    private ThreadLocal<String> scenarioName = new ThreadLocal<>();

    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Rhonwen.Evans\\.m2\\repository\\webdriver\\chromedriver\\win32\\80.0.3987.106\\chromedriver.exe");
        System.setProperty("-Dbrowser", "chrome");
        WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup();
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        UITestLifecycle.get().beforeSuite();
    }

    @BeforeMethod(alwaysRun = true)
    public void setTestName(Method method, Object[] testData) {
        PickleEvent pickleEvent = ((PickleEventWrapper) testData[0]).getPickleEvent();
        String scenarioName = pickleEvent.pickle.getName();
        this.scenarioName.set(scenarioName);
        logger.info("START {}", scenarioName);
        UITestLifecycle.get().beforeTestMethod(scenarioName);
    }

    @Test(dataProvider = "scenarios", retryAnalyzer = BDDRetryFlakyTest.class)
    public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cfw) throws Throwable {
        testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        UITestLifecycle.get().afterTestMethod();
        logResult(result);
    }
    private void logResult(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.FAILURE:
                logger.error("FAIL  {}", scenarioName.get());
                break;
            case ITestResult.SKIP:
                logger.warn("SKIP  {}", scenarioName.get());
                break;
            case ITestResult.SUCCESS:
                logger.info("PASS  {}", scenarioName.get());
                break;
            default:
                logger.warn("Unexpected result status: {}", result.getStatus());
        }
    }

    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return testNGCucumberRunner.provideScenarios();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        try {
            testNGCucumberRunner.finish();
        }catch (Exception e){
        }
        UITestLifecycle.get().afterTestSuite();
    }

    @Override
    public String getTestName() {
        return scenarioName.get();
    }
}
