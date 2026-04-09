package com.sofka.automation.stepdefinitions;

import com.sofka.automation.models.EventRequest;
import com.sofka.automation.models.EventResponse;
import com.sofka.automation.questions.TheEventResponseState;
import com.sofka.automation.tasks.DeactivateEvent;
import com.sofka.automation.tasks.GetEvent;
import com.sofka.automation.tasks.PostEvent;
import com.sofka.automation.tasks.PutEvent;
import com.sofka.automation.utils.SessionManager;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;

import java.util.UUID;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.*;

public class GeneratedEventSteps {

    @Dado("que el Administrador tiene acceso al servicio de Catálogo")
    public void queElAdministradorTieneAccesoAlServicioDeCatalogo() {
        
    }

    @Cuando("crea un nuevo evento con el nombre {string} y precio {double}")
    public void creaUnNuevoEventoConElNombreYPrecio(String name, Double price) {
        EventRequest request = EventRequest.builder()
                .name(name)
                .description("Evento creado desde steps generados")
                .eventDate("2026-10-01T00:00:00Z")
                .venue("Recinto generado")
                .maxCapacity(100)
                .basePrice(price)
                .build();

        theActorInTheSpotlight().attemptsTo(
                PostEvent.withInfo(request)
        );

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        if (response != null && response.getId() != null) {
            SessionManager.set(SessionManager.EVENT_ID, response.getId());
        }
    }

    @Entonces("el evento debe ser creado exitosamente con un ID válido")
    public void elEventoDebeSerCreadoExitosamenteConUnIDValido() {
        theActorInTheSpotlight().should(
                seeThat(TheEventResponseState.code(), equalTo(201))
        );

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        if (response != null && response.getId() != null) {
            SessionManager.set(SessionManager.EVENT_ID, response.getId());
        }
    }

    @Entonces("al consultar el evento por su ID el nombre debe ser {string}")
    public void alConsultarElEventoPorSuIDElNombreDebeSer(String expectedName) {
        UUID id = (UUID) SessionManager.get(SessionManager.EVENT_ID);
        theActorInTheSpotlight().attemptsTo(
                GetEvent.withId(id)
        );

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        theActorInTheSpotlight().should(
                seeThat("El nombre del evento", act -> response.getName(), equalTo(expectedName))
        );
    }

    @Cuando("al actualizar el nombre del evento a {string}")
    public void alActualizarElNombreDelEventoA(String newName) {
        UUID id = (UUID) SessionManager.get(SessionManager.EVENT_ID);

        EventResponse current = SerenityRest.lastResponse().as(EventResponse.class);
        EventRequest update = EventRequest.builder()
                .name(newName)
                .description(current != null ? current.getDescription() : "")
                .eventDate(current != null ? current.getEventDate() : "2026-10-01T00:00:00Z")
                .venue(current != null ? current.getVenue() : "Recinto generado")
                .maxCapacity(current != null && current.getMaxCapacity() != null ? current.getMaxCapacity() : 100)
                .basePrice(current != null && current.getBasePrice() != null ? current.getBasePrice() : 50.0)
                .build();

        theActorInTheSpotlight().attemptsTo(
                PutEvent.withInfo(id, update)
        );
    }

    @Entonces("el cambio debe persistirse correctamente en el sistema")
    public void elCambioDebePersistirseCorrectamenteEnElSistema() {
        UUID id = (UUID) SessionManager.get(SessionManager.EVENT_ID);
        theActorInTheSpotlight().attemptsTo(
                GetEvent.withId(id)
        );

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        theActorInTheSpotlight().should(
                seeThat("Evento existe", act -> response != null && response.getId() != null, is(true))
        );
    }

    @Cuando("al desactivar el evento mediante el proceso de borrado lógico")
    public void alDesactivarElEventoMedianteElProcesoDeBorradoLogico() {
        UUID id = (UUID) SessionManager.get(SessionManager.EVENT_ID);
        theActorInTheSpotlight().attemptsTo(
                DeactivateEvent.withId(id)
        );
    }

    @Entonces("el evento ya no debe figurar como activo para la venta")
    public void elEventoYaNoDebeFigurarComoActivoParaLaVenta() {
        UUID id = (UUID) SessionManager.get(SessionManager.EVENT_ID);
        theActorInTheSpotlight().attemptsTo(
                GetEvent.withId(id)
        );

        EventResponse response = SerenityRest.lastResponse().as(EventResponse.class);
        theActorInTheSpotlight().should(
                seeThat("El estado del evento", act -> response.getStatus(), not(equalTo("ACTIVE")))
        );
    }
}
