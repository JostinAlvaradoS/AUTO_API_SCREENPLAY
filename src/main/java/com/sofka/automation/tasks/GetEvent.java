package com.sofka.automation.tasks;

import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetEvent implements Task {

    private final UUID eventId;

    public GetEvent(UUID eventId) {
        this.eventId = eventId;
    }

    public static GetEvent withId(UUID eventId) {
        return instrumented(GetEvent.class, eventId);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(Endpoints.GET_EVENT)
                        .with(request -> request.pathParam("id", eventId))
        );
    }
}
