package com.sofka.automation.tasks;

import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetEventsList implements Task {

    public static GetEventsList all() {
        return instrumented(GetEventsList.class);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(Endpoints.GET_EVENTS_LIST)
        );
    }
}
