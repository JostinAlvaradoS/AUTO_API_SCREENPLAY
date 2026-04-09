package com.sofka.automation.stepdefinitions;

import com.sofka.automation.models.EventRequest;
import com.sofka.automation.utils.SessionManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;

public class WaitlistHooks {

    private static final Logger logger = LoggerFactory.getLogger(WaitlistHooks.class);

    private static final String CATALOG_URL   = System.getProperty("catalog.api.url",   "http://localhost:50001");
    private static final String INVENTORY_URL = System.getProperty("inventory.api.url", "http://localhost:50002");
    private static final String WAITLIST_URL  = System.getProperty("waitlist.api.url",  "http://localhost:5006");

    @Before(order = 10001, value = "@HU-Waitlist")
    public void setupWaitlistActor() {
        theActorCalled("The System").can(CallAnApi.at(WAITLIST_URL));
        Serenity.recordReportData().withTitle("Waitlist API URL").andContents(WAITLIST_URL);
    }

    @Before(order = 10002, value = "@HU-Waitlist")
    public void setupTestEvent(Scenario scenario) {
        boolean needsSoldOut = scenario.getSourceTagNames().stream()
                .anyMatch(tag -> List.of("@RegistroExitoso", "@RegistroDuplicado",
                        "@AsignacionAutomatica", "@LiberacionConSiguiente", "@LiberacionSinCola")
                        .contains(tag));

        String eventId = createEvent();
        if (eventId == null) {
            logger.warn("No se pudo crear el evento de prueba para waitlist");
            return;
        }

        generateSeat(eventId);

        if (needsSoldOut) {
            blockFirstSeat(eventId);
            
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        }

        SessionManager.set(SessionManager.WAITLIST_EVENT_ID, eventId);
        logger.info("Evento de prueba waitlist listo: {} (agotado={})", eventId, needsSoldOut);
    }

    @After(value = "@HU-Waitlist")
    public void cleanupTestEvent() {
        String eventId = SessionManager.get(SessionManager.WAITLIST_EVENT_ID);
        if (eventId != null) {
            try {
                RestAssured.given()
                        .baseUri(CATALOG_URL)
                        .post("/admin/events/" + eventId + "/deactivate");
                logger.info("Evento de prueba waitlist desactivado: {}", eventId);
            } catch (Exception e) {
                logger.warn("No se pudo desactivar el evento de prueba: {}", eventId);
            }
        }
    }

    private String createEvent() {
        try {
            String futureDate = LocalDateTime.now().plusDays(30)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            String body = String.format(
                    "{\"name\":\"Concierto Rock 2026 - Test\","
                            + "\"description\":\"Evento de prueba waitlist\","
                            + "\"eventDate\":\"%s\","
                            + "\"venue\":\"Teatro de Prueba\","
                            + "\"maxCapacity\":1,"
                            + "\"basePrice\":50.0}",
                    futureDate);

            Response response = RestAssured.given()
                    .baseUri(CATALOG_URL)
                    .contentType("application/json")
                    .body(body)
                    .post("/admin/events");

            if (response.statusCode() == 201) {
                return response.jsonPath().getString("id");
            }
        } catch (Exception e) {
            logger.error("Error creando evento de prueba: {}", e.getMessage());
        }
        return null;
    }

    private void generateSeat(String eventId) {
        try {
            String body = "{\"sectionConfigurations\":[{\"sectionCode\":\"A\",\"rows\":1,\"seatsPerRow\":1,\"priceMultiplier\":1.0}]}";
            RestAssured.given()
                    .baseUri(CATALOG_URL)
                    .contentType("application/json")
                    .body(body)
                    .post("/admin/events/" + eventId + "/seats");
        } catch (Exception e) {
            logger.warn("Error generando asiento para evento {}: {}", eventId, e.getMessage());
        }
    }

    private void blockFirstSeat(String eventId) {
        try {
            Response seatmap = RestAssured.given()
                    .baseUri(CATALOG_URL)
                    .get("/events/" + eventId + "/seatmap/");

            String seatId = seatmap.jsonPath().getString("seats[0].id");
            if (seatId == null) return;

            String customerId = java.util.UUID.randomUUID().toString();
            String body = String.format("{\"seatId\":\"%s\",\"customerId\":\"%s\",\"eventId\":\"%s\"}", seatId, customerId, eventId);

            RestAssured.given()
                    .baseUri(INVENTORY_URL)
                    .contentType("application/json")
                    .body(body)
                    .post("/reservations");

            logger.info("Asiento {} bloqueado para evento {}", seatId, eventId);
        } catch (Exception e) {
            logger.warn("Error bloqueando asiento para evento {}: {}", eventId, e.getMessage());
        }
    }
}
