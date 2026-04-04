# Implementation Plan: Sistema de Lista de Espera — API Automation

**Branch**: `002-waitlist-api` | **Date**: 2026-04-03 | **Spec**: [spec.md](./spec.md)
**Input**: Automatización API del Waitlist.Api usando Serenity Screenplay REST.

---

## Summary

Implementar un suite de automatización API para el Sistema de Lista de Espera usando el **Screenplay Pattern**. El Actor "The System" usará la ability `CallAnApi` apuntando al Waitlist.Api en `http://localhost:5006`. Se cubren los 6 escenarios del feature con Tasks específicas para cada endpoint.

---

## Technical Context

| Ítem | Valor |
|---|---|
| **Language/Version** | Java 17+ |
| **Primary Dependencies** | Serenity Screenplay REST, RestAssured, Lombok, JUnit 5 |
| **Actor** | "The System" con `CallAnApi.at("http://localhost:5006")` |
| **Target API** | Waitlist.Api — `http://localhost:5006` |
| **Auxiliary APIs** | Catalog.Api (`50001`) + Inventory.Api (`50002`) para setup de hooks |
| **Session Storage** | `SessionManager` (Serenity session variables) |
| **Runner** | `WaitlistRunner` — filtra por `@HU-Waitlist` |

---

## Constitution Check

- [x] **Screenplay Compliance**: Actor "The System" + Tasks + Questions. Misma estructura que `001-event-crud-api`.
- [x] **Endpoint Coverage**: `POST /api/v1/waitlist/join` + `GET /api/v1/waitlist/has-pending`.
- [x] **No Auth**: El Waitlist.Api no requiere autenticación — sin `[Authorize]` en el controlador.
- [x] **Session Management**: `SessionManager` para persistir `eventId`, `entryId`, `email` entre steps.
- [x] **Hook Isolation**: Cada escenario crea su propio evento de prueba y lo limpia en `@After`.

---

## Project Structure (New files)

```text
specs/002-waitlist-api/
├── spec.md
├── plan.md          ← este archivo
└── tasks.md

src/test/
├── resources/
│   └── features/
│       └── hu-waitlist.feature          ← Gherkin en español (6 escenarios)
└── java/com/sofka/automation/
    ├── models/
    │   ├── WaitlistJoinRequest.java      ← { email, eventId }
    │   ├── WaitlistJoinResponse.java     ← { entryId, position }
    │   └── WaitlistErrorResponse.java    ← { message }
    ├── tasks/
    │   ├── JoinWaitlist.java             ← POST /api/v1/waitlist/join
    │   └── CheckHasPending.java          ← GET /api/v1/waitlist/has-pending
    ├── questions/
    │   └── TheWaitlistResponseState.java ← statusCode + body fields
    ├── stepdefinitions/
    │   ├── WaitlistSteps.java            ← Step definitions (6 escenarios)
    │   └── WaitlistHooks.java            ← @Before setup evento + @After cleanup
    └── runners/
        └── WaitlistRunner.java           ← @HU-Waitlist filter
```

**Ediciones a archivos existentes:**

```text
utils/Endpoints.java       ← + WAITLIST_JOIN, WAITLIST_HAS_PENDING
utils/SessionManager.java  ← + WAITLIST_EVENT_ID, WAITLIST_ENTRY_ID, WAITLIST_EMAIL
```

---

## Flow Design

### Escenarios 1-3 (Sincronos — sin Kafka)

```
@Before WaitlistHooks
  ├── [Esc 1, 3] createEvent() → generateSeats(1) → blockAllSeats() → store WAITLIST_EVENT_ID
  └── [Esc 2]    createEvent() → generateSeats(1) → (sin bloquear) → store WAITLIST_EVENT_ID

Escenario 1 (stock=0):
  JoinWaitlist(email, eventId) → POST /api/v1/waitlist/join → assert 201 + entryId + position

Escenario 2 (stock>0):
  JoinWaitlist(email, eventId) → POST /api/v1/waitlist/join → assert 409 + mensaje tickets

Escenario 3 (duplicado):
  JoinWaitlist(email, eventId) → 201 ✓
  JoinWaitlist(email, eventId) → 409 + mensaje ya registrado
```

### Escenarios 4-6 (Integración completa — requieren Kafka + workers)

```
@Before setup:
  createEvent() → generateSeats(1) → blockAllSeats() → joinWaitlist("jostin@") → store IDs

Escenario 4:
  CheckHasPending(eventId) → hasPending=true ✓
  [esperar expiración de reserva vía worker ≈60s]
  CheckHasPending(eventId) → hasPending=false (entrada asignada)

Escenario 5:
  joinWaitlist("segundo@") después de "jostin@"
  [expiración de asignación ≈30min + rotación por worker]
  CheckHasPending(eventId) → hasPending=false (segundo@ asignado)

Escenario 6:
  Solo "jostin@" en cola
  [expiración + cola vacía → liberación al pool]
  CheckHasPending(eventId) → hasPending=false (pool liberado)
```

---

## Dependency Graph

```
WaitlistHooks (setup)
    ↓
[T007] WaitlistJoinRequest + WaitlistJoinResponse + WaitlistErrorResponse
    ↓
[T008] Endpoints.java (add WAITLIST_JOIN, WAITLIST_HAS_PENDING)
[T009] SessionManager.java (add WAITLIST_* keys)
    ↓
[T010] JoinWaitlist Task
[T011] CheckHasPending Task
    ↓
[T012] TheWaitlistResponseState Question
    ↓
[T013] WaitlistSteps (step definitions)
    ↓
[T014] WaitlistRunner
```
