package com.sofka.automation.stepdefinitions;

import com.sofka.automation.models.EventRequest;
import com.sofka.automation.models.EventResponse;
import com.sofka.automation.questions.TheEventResponseState;
import com.sofka.automation.questions.TheEventSchema;
import com.sofka.automation.tasks.PostEvent;
import com.sofka.automation.utils.SessionManager;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.rest.SerenityRest;

import java.util.Map;
import java.util.stream.Collectors;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;

public class EventStepDefinitions {

    @Dado("que soy un organizador autenticado")
    public void queSoyUnOrganizadorAutenticado() {
        // En este MVP no hay auth activa, el actor ya está inicializado en Hooks
    }

    @Cuando("ingreso los datos del evento:")
    public void ingresoLosDatosDelEvento(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps(String.class, String.class).stream()
                .collect(Collectors.toMap(m -> m.get("campo"), m -> m.get("valor")));

        EventRequest request = EventRequest.builder()
                .name(data.get("nombre"))
                .description(data.getOrDefault("descripcion", "Descripción automática"))
                .eventDate(data.get("fecha") + "T00:00:00Z")
                .venue(data.get("recinto"))
                .maxCapacity(Integer.parseInt(data.get("capacidad")))
                .basePrice(Double.parseDouble(data.getOrDefault("precio_base", "50.0")))
                .build();

        theActorInTheSpotlight().attemptsTo(
                PostEvent.withInfo(request)
        );
    }

    @Y("defino las zonas y precios del mapa")
    public void definoLasZonasYPreciosDelMapa() {
        // En este MVP de API CRUD, este paso se asume implícito en el POST del evento
    }

    @Entonces("el evento debe guardarse exitosamente")
    public void elEventoDebeGuardarseExitosamente() {
        theActorInTheSpotlight().should(
                seeThat("El código de respuesta es exitoso", TheEventResponseState.code(), equalTo(201))
        );
    }

    @Y("recibir una confirmación de creación")
    public void recibirUnaConfirmacionDeCreacion() {
        theActorInTheSpotlight().asksFor(TheEventSchema.matches("schemas/event-response-schema.json"));

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        SessionManager.set(SessionManager.EVENT_ID, response.getId());
    }

}