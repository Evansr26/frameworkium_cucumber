package com.frameworkium.bdd.zephyr;

import com.frameworkium.core.common.properties.Property;
import com.frameworkium.core.common.reporting.jira.zapi.Execution;
import cucumber.runtime.CucumberException;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;
import gherkin.formatter.model.Result;
import gherkin.pickles.PickleTag;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.junit.platform.commons.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.frameworkium.core.common.reporting.jira.JiraConfig.ZapiStatus.*;

public class ZephyrListener implements ConcurrentEventListener, Reporter {

    private Boolean updateTCMStatus = Property.JIRA_URL.isSpecified();
    private int scnStepBrokenCount = 0;
    private Throwable latestError;

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, scenarioStartedHandler);
        publisher.registerHandlerFor(TestCaseFinished.class, scenarioFinishedHandler);
    }

    private EventHandler<TestCaseStarted> scenarioStartedHandler = event -> {
        scnStepBrokenCount = 0;
        if (updateTCMStatus) {
            updateTCMStatus(getTestCaseId(event.getTestCase()), ZAPI_STATUS_WIP, "");
        }
    };

    private EventHandler<TestCaseFinished> scenarioFinishedHandler = testCaseFinished -> {
        if (!updateTCMStatus) {
            return;
        }

        // Update Zephyr with scenario's test result
        final List<String> testCaseIds = getTestCaseId(testCaseFinished.getTestCase());
        if (scnStepBrokenCount > 0) {
            updateTCMStatus(testCaseIds, ZAPI_STATUS_FAIL, latestError.getLocalizedMessage());
        } else {
            updateTCMStatus(testCaseIds, ZAPI_STATUS_PASS, "");
        }
    };

    private List<String> getTestCaseId(TestCase scenario) {
        return retrieveTagStream(scenario.getTags(), "TestCaseId")
                .collect(Collectors.toList());
    }

    private Stream<String> retrieveTagStream(List<PickleTag> tags, String tagName) {
        String tagSearch = "@" + tagName + "(";
        return tags.stream()
                .map(PickleTag::getName)
                .filter(name -> name.startsWith(tagSearch))
                .map(name -> name
                        .replace(tagSearch, "")
                        .replace(")", "")
                        .trim()
                        .replaceAll("^\"|\"$", ""));
    }

    private void updateTCMStatus(List<String> testCaseIds, int status, String comment) {
        String updatedComment = "Updated by Cucumber Zephyr Listener\n" + comment;
        testCaseIds.stream()
                .filter(StringUtils::isNotBlank)
                .map(Execution::new)
                .forEach(ex -> ex.update(status, updatedComment));
    }

    @Override
    public void before(Match match, Result result) {

    }

    @Override
    public void result(Result result) {
        String status = result.getStatus();
        if (isNotSkipped(status) && resultIsBroken(status)) {
            scnStepBrokenCount++;
            latestError = result.getError();
            if (latestError instanceof CucumberException) {
                latestError = latestError.getCause();
            }
        }
    }

    @Override
    public void after(Match match, Result result) {

    }


    @Override
    public void match(Match match) {

    }

    @Override
    public void embedding(String s, byte[] bytes) {

    }

    @Override
    public void write(String s) {

    }

    private boolean isNotSkipped(String status) {
        return "skipped".equals(status);
    }

    private boolean resultIsBroken(String status) {
        return "undefined".equals(status)
                || "passed".equals(status);
    }


}
