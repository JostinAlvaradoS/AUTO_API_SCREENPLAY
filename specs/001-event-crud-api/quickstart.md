# Quickstart: Event CRUD Feature

**SSOT (Gherkin Source of Truth)**: [hu05-admin-config.feature](../../shared-specs/specs/001-ticketing-mvp/hu05-admin-config.feature)

Este archivo sincroniza los escenarios de negocio definidos en la feature del organizador con las capacidades técnicas del API.

## Referencia de Gherkin (Sincronizado con hu05)

```gherkin
# language: es
@HU-05 @Admin
Característica: Creación de eventos y configuración de asientos
  Como organizador
  Quiero crear eventos y configurar los asientos del lugar
  Para que se puedan vender entradas para mis eventos

  Escenario: Creación exitosa de un evento básico
    Dado que soy un organizador autenticado
    Cuando ingreso los datos del evento:
      | campo         | valor               |
      | nombre        | Festival Jazz 2026  |
      | fecha         | 2026-10-15          |
      | recinto       | Teatro Nacional     |
      | capacidad     | 500                 |
    Y defino las zonas y precios del mapa
    Entonces el evento debe guardarse exitosamente
    Y recibir una confirmación de creación
```

## Guía Técnica para Screenplay

### Actor & Habilidades
- **Nombre**: "The System"
- **Habilidad**: `CallAnApi.at("http://localhost:50001")`

### Ejemplo de Implementación de Task (POST)
Para ejecutar el primer escenario de la `hu05`, la task `PostEvent` utilizará los datos de la tabla Gherkin para construir el `EventRequest`.

```java
public class PostEvent implements Task {
    private final EventRequest eventRequest;

    public static PostEvent withData(EventRequest data) {
        return instrumented(PostEvent.class, data);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/admin/events")
                .with(request -> request.header("Content-Type", "application/json")
                                         .body(eventRequest))
        );
    }
}
```

## Verificación de Resultados (Questions)
- `Status.code().is(201)`
- `LastResponse.body().path("name").is(expectedName)`
- `LastResponse.body().path("status").is("active")`
