package com.sofka.automation.tasks;

import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CheckHasPending implements Task {

    private final String eventId;

    public CheckHasPending(String eventId) {
        this.eventId = eventId;
    }

    public static CheckHasPending forEvent(String eventId) {
        return instrumented(CheckHasPending.class, eventId);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(Endpoints.WAITLIST_HAS_PENDING)
                        .with(req -> req.queryParam("eventId", eventId))
        );
    }
}
