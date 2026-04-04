# Tasks: Sistema de Lista de Espera — API Automation

**Feature**: `002-waitlist-api` | **Date**: 2026-04-03
**Plan**: [plan.md](./plan.md) | **Spec**: [spec.md](./spec.md)
**Total Tasks**: 20

---

## Phase 1: Feature File (Gherkin SSOT)

- [x] **T001** Crear `src/test/resources/features/hu-waitlist.feature` con los 6 escenarios en español, tags `@HU-Waitlist`, `@RegistroExitoso`, `@TicketsDisponibles`, `@RegistroDuplicado`, `@AsignacionAutomatica`, `@LiberacionConSiguiente`, `@LiberacionSinCola`

---

## Phase 2: Models

- [x] **T002** [P] Crear `models/WaitlistJoinRequest.java` — `@Data @Builder @NoArgsConstructor @AllArgsConstructor` con campos `String email`, `String eventId`
- [x] **T003** [P] Crear `models/WaitlistJoinResponse.java` — `@JsonIgnoreProperties(ignoreUnknown=true)` con campos `String entryId`, `int position`
- [x] **T004** [P] Crear `models/WaitlistErrorResponse.java` — campo `String message`

---

## Phase 3: Infrastructure (Endpoints + Session)

- [x] **T005** Editar `utils/Endpoints.java` — agregar:
  - `WAITLIST_JOIN = "/api/v1/waitlist/join"`
  - `WAITLIST_HAS_PENDING = "/api/v1/waitlist/has-pending"`

- [x] **T006** Editar `utils/SessionManager.java` — agregar keys:
  - `WAITLIST_EVENT_ID = "waitlistEventId"`
  - `WAITLIST_ENTRY_ID = "waitlistEntryId"`
  - `WAITLIST_EMAIL = "waitlistEmail"`
  - `WAITLIST_SECOND_EMAIL = "waitlistSecondEmail"`

---

## Phase 4: Tasks (Screenplay Actions)

- [x] **T007** Crear `tasks/JoinWaitlist.java`
  - Método factory: `JoinWaitlist.withEmail(String email, String eventId)`
  - `performAs`: `Post.to(Endpoints.WAITLIST_JOIN)` con body `WaitlistJoinRequest`
  - Header `Content-Type: application/json`

- [x] **T008** Crear `tasks/CheckHasPending.java`
  - Método factory: `CheckHasPending.forEvent(String eventId)`
  - `performAs`: `Get.resource(Endpoints.WAITLIST_HAS_PENDING)` con query param `eventId`

---

## Phase 5: Questions

- [x] **T009** Crear `questions/TheWaitlistResponseState.java`
  - `code()` → `SerenityRest.lastResponse().statusCode()`
  - `entryId()` → `SerenityRest.lastResponse().jsonPath().getString("entryId")`
  - `position()` → `SerenityRest.lastResponse().jsonPath().getInt("position")`
  - `errorMessage()` → `SerenityRest.lastResponse().jsonPath().getString("message")`
  - `hasPending()` → `SerenityRest.lastResponse().jsonPath().getBoolean("hasPending")`

---

## Phase 6: Hooks

- [x] **T010** Crear `stepdefinitions/WaitlistHooks.java`
  - `@Before(order=0)`: `OnStage.setTheStage(new OnlineCast())`
  - `@Before(order=1)`: configurar Actor "The System" con `CallAnApi.at("http://localhost:5006")`
  - `@Before(order=2, value="@RegistroExitoso or @RegistroDuplicado or @AsignacionAutomatica or @LiberacionConSiguiente or @LiberacionSinCola")`: crear evento agotado vía Catalog+Inventory API, guardar en `SessionManager.WAITLIST_EVENT_ID`
  - `@Before(order=2, value="@TicketsDisponibles")`: crear evento con asientos disponibles, guardar en `SessionManager.WAITLIST_EVENT_ID`
  - `@After`: desactivar evento creado via Catalog API

---

## Phase 7: Step Definitions (RED → GREEN por escenario)

- [x] **T011** 🔴 Implementar steps del Escenario 1 (`@RegistroExitoso`) en `WaitlistSteps.java`
  - `Dado que el evento ... tiene stock igual a cero` → no-op (setup en hook)
  - `Cuando el usuario ... se registra en la lista de espera del evento` → `JoinWaitlist.withEmail(email, eventId)`
  - `Entonces el sistema responde 201 Created` → `seeThat(TheWaitlistResponseState.code(), equalTo(201))`
  - `Y el usuario recibe su posición en la cola` → `seeThat(TheWaitlistResponseState.position(), greaterThan(0))`

- [x] **T012** 🔴 Implementar steps del Escenario 2 (`@TicketsDisponibles`)
  - `Dado que el evento ... tiene tickets disponibles` → no-op (setup en hook)
  - `Cuando ... intenta unirse a la lista de espera del evento` → `JoinWaitlist.withEmail(email, eventId)`
  - `Entonces el sistema responde con error 409` → `seeThat(TheWaitlistResponseState.code(), equalTo(409))`
  - `Y el mensaje indica que hay tickets disponibles` → assert message `containsString("disponibles")`

- [x] **T013** 🔴 Implementar steps del Escenario 3 (`@RegistroDuplicado`)
  - `Dado que ... ya está registrado` → `JoinWaitlist.withEmail(email, eventId)` (primera vez, espera 201)
  - `Cuando el mismo correo intenta registrarse nuevamente` → `JoinWaitlist.withEmail(email, eventId)`
  - `Entonces el sistema responde con error 409` → assert 409
  - `Y el mensaje indica que ya está en la lista` → assert message `containsString("lista de espera")`

- [x] **T014** 🔴 Implementar steps del Escenario 4 (`@AsignacionAutomatica`)
  - `Dado que ... es el primero en la lista de espera` → `JoinWaitlist.withEmail(email, eventId)` → `CheckHasPending` → assert `hasPending=true`
  - `Cuando el tiempo de pago inicial caduca` → no-op (worker corre cada 60s)
  - `Entonces el sistema crea una orden automática` → polling `CheckHasPending` hasta `hasPending=false` (timeout 120s)
  - `Y actualiza el estado de la entrada a Asignado` → verificado implícitamente (no más pending)
  - `Y envía un correo con el enlace de pago` → paso de verificación de comportamiento esperado (logging)

- [x] **T015** 🔴 Implementar steps del Escenario 5 (`@LiberacionConSiguiente`)
  - Setup: registrar "jostin@" y "segundo@" en lista
  - Verificar que "jostin@" fue expirado y "segundo@" asignado vía `has-pending` polling
  - `Y reasigna el asiento directamente a "segundo@"` → `hasPending=false` tras rotación

- [x] **T016** 🔴 Implementar steps del Escenario 6 (`@LiberacionSinCola`)
  - Setup: solo "jostin@" en lista
  - Tras expiración: `hasPending=false` y asiento liberado

---

## Phase 8: Runner

- [x] **T017** Crear `runners/WaitlistRunner.java`
  - `@RunWith(CucumberWithSerenity.class)`
  - `features = "src/test/resources/features/hu-waitlist.feature"`
  - `glue = "com.sofka.automation.stepdefinitions"`
  - `tags = "@HU-Waitlist"`

---

## Phase 9: Polish

- [ ] **T018** [P] Verificar que `WaitlistHooks` no interfiere con los hooks existentes de `Hooks.java`
- [ ] **T019** [P] Agregar logs descriptivos en cada step via `LoggerUtils` o SLF4J
- [ ] **T020** Ejecutar suite completa y verificar que escenarios 1-3 pasan (escenarios 4-6 marcados `@Pending` si no hay infraestructura Kafka)

---

## Dependency Graph

```
T001 (feature file)
    ↓
T002-T004 (models) → T005-T006 (endpoints + session) → T007-T008 (tasks) → T009 (questions)
                                                              ↓
                                                        T010 (hooks)
                                                              ↓
                                                    T011-T016 (steps)
                                                              ↓
                                                        T017 (runner)
                                                              ↓
                                                    T018-T020 (polish)
```

## TDD Cycle Summary

| Ciclo | Escenario | RED | GREEN |
|---|---|---|---|
| 1 | Registro exitoso | T011 step fails → no task | T007 JoinWaitlist task |
| 2 | Tickets disponibles | T012 step fails → 404 not 409 | T007 + hook stock>0 |
| 3 | Duplicado | T013 step fails | T007 segunda llamada → 409 |
| 4 | Asignación automática | T014 polling timeout | Infraestructura completa |
| 5 | Rotación | T015 setup + polling | Infraestructura completa |
| 6 | Cola vacía | T016 setup + polling | Infraestructura completa |
