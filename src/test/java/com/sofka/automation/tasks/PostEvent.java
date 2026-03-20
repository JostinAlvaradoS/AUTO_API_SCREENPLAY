package com.sofka.automation.tasks;

import com.sofka.automation.models.EventRequest;
import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PostEvent implements Task {

    private final EventRequest eventRequest;

    public PostEvent(EventRequest eventRequest) {
        this.eventRequest = eventRequest;
    }

    public static PostEvent withInfo(EventRequest eventRequest) {
        return instrumented(PostEvent.class, eventRequest);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(Endpoints.CREATE_EVENT)
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .body(eventRequest))
        );
    }
}
