package com.sofka.automation.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.core.Serenity;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

public class Hooks {

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        String baseUrl = System.getProperty("restapi.baseurl", "http://localhost:50001");
        theActorCalled("The System").can(CallAnApi.at(baseUrl));
        
        Serenity.recordReportData().withTitle("Base URL API").andContents(baseUrl);
    }
}
