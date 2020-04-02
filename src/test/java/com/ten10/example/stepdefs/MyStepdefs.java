package com.ten10.example.stepdefs;

import com.ten10.example.pages.Homepage;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import static org.testng.AssertJUnit.assertTrue;

public class MyStepdefs {
    PagesApi pagesApi;

    @Given("^I am on the Ten10 homepage$")
    public void iAmOnTheTenHomepage() {
        pagesApi.homepage = Homepage.open();
    }


    @cucumber.api.java.en.When("^I click (.*)$")
    public void iClickLink(String link) {
    pagesApi.generic.clickLinkText(link);
    }


    @Then("^The title matches (.*)$")
    public void theTitleMatchesTitle(String title) {
        assertTrue(pagesApi.generic.getTitle().contains(title));
    }
}
