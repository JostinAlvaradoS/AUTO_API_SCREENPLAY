package com.sofka.automation.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TheEventSchema implements Question<Boolean> {

    private final String schemaPath;

    public TheEventSchema(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    public static TheEventSchema matches(String schemaPath) {
        return new TheEventSchema(schemaPath);
    }

    @Override
    public Boolean answeredBy(net.serenitybdd.screenplay.Actor actor) {
        actor.should(
                ResponseConsequence.seeThatResponse("The event response matches the schema",
                        response -> response.body(matchesJsonSchemaInClasspath(schemaPath)))
        );
        return true;
    }
}
