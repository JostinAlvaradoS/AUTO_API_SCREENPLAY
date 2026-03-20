package com.sofka.automation.utils;

import io.restassured.filter.log.LogDetail;
import net.serenitybdd.rest.SerenityRest;

public class LoggerUtils {

    public static void logResponseOnFailure() {
        if (SerenityRest.lastResponse().statusCode() >= 400) {
            SerenityRest.lastResponse().then().log().ifValidationFails(LogDetail.ALL);
            System.err.println("API Failure: " + SerenityRest.lastResponse().asString());
        }
    }

    private LoggerUtils() {
        // Utility class
    }
}
