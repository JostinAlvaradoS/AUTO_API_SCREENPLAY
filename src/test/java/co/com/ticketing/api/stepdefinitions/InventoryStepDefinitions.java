package co.com.ticketing.api.stepdefinitions;

import co.com.ticketing.api.models.ReservationRequest;
import co.com.ticketing.api.tasks.CreateReservation;
import co.com.ticketing.api.tasks.GetReservation;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.Map;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class InventoryStepDefinitions {

    private Actor actor;
    private String lastReservationId;
    private EnvironmentVariables environmentVariables;

    @Before
    public void setup() {
        actor = Actor.named("Sistema");
        environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();
        String baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getProperty("restapi.inventory.base.url");
        
        if (baseUrl == null) baseUrl = "http://localhost:50002";
        
        actor.can(CallAnApi.at(baseUrl));
    }

    @DataTableType
    public ReservationRequest reservationEntry(Map<String, String> entry) {
        return ReservationRequest.builder()
                .eventId(entry.get("eventId"))
                .seatId(entry.get("seatId"))
                .customerId(entry.get("customerId"))
                .build();
    }

    @Dado("que el {string} tiene acceso a la API de Inventario")
    public void queElTieneAccesoALaAPIDeInventario(String actorName) {
        // Habilidad ya configurada en @Before
    }

    @Cuando("realiza una reserva para:")
    public void realizaUnaReservaPara(ReservationRequest request) {
        actor.attemptsTo(
                CreateReservation.withData(request)
        );
        lastReservationId = SerenityRest.lastResponse().path("reservationId");
    }

    @Entonces("el código de respuesta debe ser {int}")
    public void elCodigoDeRespuestaDebeSer(Integer statusCode) {
        actor.should(
                seeThatResponse("El código de respuesta es correcto",
                        response -> response.statusCode(statusCode))
        );
    }

    @Entonces("la respuesta debe contener un {string} válido")
    public void laRespuestaDebeContenerUnValido(String field) {
        actor.should(
                seeThatResponse("El campo " + field + " está presente",
                        response -> response.body(field, notNullValue()))
        );
    }

    @Cuando("consulta la reserva por su ID")
    public void consultaLaReservaPorSuID() {
        actor.attemptsTo(
                GetReservation.byId(lastReservationId)
        );
    }

    @Entonces("la reserva debe tener el estado {string}")
    public void laReservaDebeTenerElEstado(String expectedStatus) {
        actor.should(
                seeThatResponse("El estado de la reserva es " + expectedStatus,
                        response -> response.body("status", equalTo(expectedStatus)))
        );
    }
}
