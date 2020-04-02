package com.ten10.example.pages;

import com.frameworkium.bdd.Config;
import com.frameworkium.core.ui.pages.BasePage;
import io.cucumber.java.en.Given;

public class Homepage extends BasePage<Homepage> {

public static Homepage open(){
   return new Homepage().get(Config.getProperty("baseUrl"));
}

}
