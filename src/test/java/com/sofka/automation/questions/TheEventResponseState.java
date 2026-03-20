package com.sofka.automation.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class TheEventResponseState implements Question<Integer> {

    public static TheEventResponseState code() {
        return new TheEventResponseState();
    }

    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        return SerenityRest.lastResponse().statusCode();
    }
}
