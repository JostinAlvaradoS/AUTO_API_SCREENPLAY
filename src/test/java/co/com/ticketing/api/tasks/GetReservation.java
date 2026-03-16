package co.com.ticketing.api.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GetReservation implements Task {

    private final String id;

    public GetReservation(String id) {
        this.id = id;
    }

    public static GetReservation byId(String id) {
        return instrumented(GetReservation.class, id);
    }

    @Override
    @Step("{0} consulta la reserva con ID #id")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/reservations/{reservationId}")
                        .with(request -> request
                                .pathParam("reservationId", id))
        );
    }
}
