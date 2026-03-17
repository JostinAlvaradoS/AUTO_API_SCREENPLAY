package com.sofka.automation.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.core.steps.BaseStepListener;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.util.EnvironmentVariables;
import java.io.File;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

public class Hooks {
    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        String baseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("http://localhost:50001");
        theActorCalled("The System").can(CallAnApi.at(baseUrl));
        // Register BaseStepListener so Serenity captures Rest requests in reports
        try {
            File outputDir = new File("build/serenity");
            outputDir.mkdirs();
            BaseStepListener baseStepListener = new BaseStepListener(outputDir);
            StepEventBus.getEventBus().registerListener(baseStepListener);
        } catch (Exception e) {
            System.err.println("Warning: could not register BaseStepListener: " + e.getMessage());
        }

        // Enable RestAssured/Serenity request/response logging for validation failures
        SerenityRest.enableLoggingOfRequestAndResponseIfValidationFails();
    }
}
