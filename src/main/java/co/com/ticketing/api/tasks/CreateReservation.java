package co.com.ticketing.api.tasks;

import co.com.ticketing.api.models.ReservationRequest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CreateReservation implements Task {

    private final ReservationRequest body;

    public CreateReservation(ReservationRequest body) {
        this.body = body;
    }

    public static CreateReservation withData(ReservationRequest body) {
        return instrumented(CreateReservation.class, body);
    }

    @Override
    @Step("{0} crea una reserva para el evento #body.eventId")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/reservations")
                        .with(request -> request
                                .header("Content-Type", "application/json")
                                .body(body))
        );
    }
}
