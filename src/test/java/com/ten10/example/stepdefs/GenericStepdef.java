package com.ten10.example.stepdefs;

import com.ten10.example.pages.Generic;
import com.ten10.example.pages.Homepage;
import io.cucumber.java.en.*;

import static org.testng.AssertJUnit.assertTrue;

public class GenericStepdef {

    PagesApi pagesApi;

    GenericStepdef(PagesApi pagesApi){
        this.pagesApi = pagesApi;
        this.pagesApi.generic = new Generic();
    }


    @Given("^I am on the Ten10 homepage$")
    public void i_am_on_the_ten10_homepage(){
        pagesApi.homepage = Homepage.open();
    }


    @When("^I click (.+)$")
    public void i_click_link(String linkText){
        pagesApi.generic.clickLinkText(linkText);
    }


    @Then("^The title matches '(.+)'$")
    public void the_title_matches(String title){
        assertTrue(pagesApi.generic.getTitle().contains(title));
    }

}
