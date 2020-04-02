package com.ten10.example.pages;

import com.frameworkium.core.ui.pages.BasePage;
import org.openqa.selenium.By;

public class Generic extends BasePage<Generic> {

    public Generic clickLinkText(String linkText){
        driver.findElement(By.linkText(linkText)).click();
        return new Generic().get();
    }

}
