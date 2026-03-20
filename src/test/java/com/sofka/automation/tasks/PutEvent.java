package com.sofka.automation.tasks;

import com.sofka.automation.models.EventRequest;
import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;

import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PutEvent implements Task {

    private final UUID eventId;
    private final EventRequest eventRequest;

    public PutEvent(UUID eventId, EventRequest eventRequest) {
        this.eventId = eventId;
        this.eventRequest = eventRequest;
    }

    public static PutEvent withInfo(UUID eventId, EventRequest eventRequest) {
        return instrumented(PutEvent.class, eventId, eventRequest);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Put.to(Endpoints.UPDATE_EVENT)
                        .with(request -> request
                                .pathParam("id", eventId)
                                .header("Content-Type", "application/json")
                                .body(eventRequest))
        );
    }
}
