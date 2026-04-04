package com.sofka.automation.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/hu-waitlist.feature",
        glue     = "com.sofka.automation.stepdefinitions",
        tags     = "@HU-Waitlist",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class WaitlistRunner {
}
