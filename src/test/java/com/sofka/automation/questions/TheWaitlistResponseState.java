package com.sofka.automation.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class TheWaitlistResponseState {

    public static Question<Integer> code() {
        return actor -> SerenityRest.lastResponse().statusCode();
    }

    public static Question<String> entryId() {
        return actor -> SerenityRest.lastResponse().jsonPath().getString("entryId");
    }

    public static Question<Integer> position() {
        return actor -> SerenityRest.lastResponse().jsonPath().getInt("position");
    }

    public static Question<String> errorMessage() {
        return actor -> SerenityRest.lastResponse().jsonPath().getString("message");
    }

    public static Question<Boolean> hasPending() {
        return actor -> SerenityRest.lastResponse().jsonPath().getBoolean("hasPending");
    }

    private TheWaitlistResponseState() {
        // Utility class
    }
}
