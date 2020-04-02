package com.ten10.example;

import com.frameworkium.bdd.BDDScreenShotListener;
import com.frameworkium.core.ui.listeners.CaptureListener;
import io.cucumber.testng.CucumberOptions;
import org.junit.runner.RunWith;
import org.testng.annotations.Listeners;
import io.cucumber.junit.Cucumber;


@Listeners({CaptureListener.class, BDDScreenShotListener.class})
@RunWith(Cucumber.class)
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


public class TestRunner {
//Before

    //After

}
