package com.sofka.automation.tasks;

import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.UUID;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DeactivateEvent implements Task {

    private final UUID eventId;

    public DeactivateEvent(UUID eventId) {
        this.eventId = eventId;
    }

    public static DeactivateEvent withId(UUID eventId) {
        return instrumented(DeactivateEvent.class, eventId);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(Endpoints.DEACTIVATE_EVENT)
                        .with(request -> request.pathParam("id", eventId))
        );
    }
}
