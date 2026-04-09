package com.sofka.automation.tasks;

import com.sofka.automation.models.WaitlistJoinRequest;
import com.sofka.automation.utils.Endpoints;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class JoinWaitlist implements Task {

    private final WaitlistJoinRequest request;

    public JoinWaitlist(WaitlistJoinRequest request) {
        this.request = request;
    }

    public static JoinWaitlist withEmail(String email, String eventId) {
        WaitlistJoinRequest request = WaitlistJoinRequest.builder()
                .email(email)
                .eventId(eventId)
                .build();
        return instrumented(JoinWaitlist.class, request);
    }

    @Override
    public <T extends net.serenitybdd.screenplay.Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(Endpoints.WAITLIST_JOIN)
                        .with(req -> req
                                .header("Content-Type", "application/json")
                                .body(request))
        );
    }
}
