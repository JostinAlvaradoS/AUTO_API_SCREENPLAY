# Feature Specification: Sistema de Lista de Espera Inteligente — API Automation

**Feature Branch**: `002-waitlist-api`
**Created**: 2026-04-03
**Status**: Draft
**Input**: Automatizar los 6 escenarios del Sistema de Lista de Espera Inteligente usando Serenity Screenplay REST contra el Waitlist.Api (C#).
**Technical Base**: Waitlist.Api — `POST /api/v1/waitlist/join` | `GET /api/v1/waitlist/has-pending`

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Registro en Lista de Espera (Priority: P1) 🎯 MVP

Como usuario interesado en un evento agotado, quiero registrarme en la lista de espera para recibir una asignación cuando un asiento quede disponible.

**Why this priority**: Es el punto de entrada al flujo completo. Sin el registro exitoso, los demás escenarios no existen.

**Independent Test**: `POST /api/v1/waitlist/join` con evento de stock=0 y email válido → verificar `201 Created` con `entryId` y `position`.

**Acceptance Scenarios**:

1. **Given** el evento "Concierto Rock 2026" tiene stock igual a cero, **When** el usuario "jostin@example.com" llama `POST /api/v1/waitlist/join`, **Then** el sistema responde `201 Created` con `entryId` válido y `position >= 1`.
2. **Given** el evento "Concierto Rock 2026" tiene tickets disponibles (stock > 0), **When** el usuario "jostin@example.com" intenta registrarse en la lista, **Then** el sistema responde `409 Conflict` con mensaje que contiene "Hay tickets disponibles".
3. **Given** "jostin@example.com" ya está registrado en la lista del evento, **When** el mismo correo intenta registrarse nuevamente, **Then** el sistema responde `409 Conflict` con mensaje que contiene "Ya estás en la lista de espera".

---

### User Story 2 — Asignación Automática al Expirar Reserva (Priority: P2)

Como sistema, quiero que al expirar una reserva y existir un usuario en la lista de espera, se le asigne automáticamente el asiento sin liberarlo al pool general.

**Why this priority**: Es el núcleo del valor del sistema de waitlist. Garantiza que ningún asiento libre quede sin asignarse a un usuario en espera.

**Independent Test**: Verificar vía `GET /api/v1/waitlist/has-pending?eventId=X` que el estado cambia de `hasPending=true` a `hasPending=false` tras la asignación.

**Acceptance Scenarios**:

4. **Given** "jostin@example.com" es el primero en la lista de espera del evento, **When** el tiempo de reserva inicial caduca, **Then** el sistema crea una orden automática, actualiza la entrada a `Asignado`, y `has-pending` refleja el nuevo estado de la cola.

---

### User Story 3 — Rotación y Liberación de Asiento por Inacción (Priority: P2)

Como sistema, quiero manejar el caso donde el usuario asignado no paga en 30 minutos: rotar al siguiente en la cola o liberar el asiento si no hay nadie más.

**Why this priority**: Garantiza que ningún asiento quede bloqueado indefinidamente y que la rotación FIFO sea justa.

**Independent Test**: Verificar vía `has-pending` y endpoints de Inventory que el asiento vuelve al pool o se reasigna según corresponda.

**Acceptance Scenarios**:

5. **Given** "jostin@example.com" fue asignado y no pagó en 30 minutos y "segundo@example.com" es el siguiente en la cola, **When** el sistema detecta la inacción, **Then** la entrada de "jostin@example.com" queda como `Expirado`, el asiento se reasigna a "segundo@example.com" sin liberarse al pool.
6. **Given** "jostin@example.com" fue asignado y no pagó en 30 minutos y no hay más usuarios en la lista, **When** el sistema detecta la inacción, **Then** el sistema cancela la orden y libera el asiento al pool general.

---

### Edge Cases

- `POST /api/v1/waitlist/join` con `eventId` vacío → `400 Bad Request`
- `POST /api/v1/waitlist/join` con email malformado → `400 Bad Request` con errores de validación
- `GET /api/v1/waitlist/has-pending` con `eventId` vacío → `400 Bad Request`
- Servicio de Catalog no disponible al momento del registro → `503 Service Unavailable`

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El framework DEBE validar `POST /api/v1/waitlist/join` retorna `201 Created` con `{ entryId, position }` para eventos con stock=0.
- **FR-002**: El framework DEBE validar `409 Conflict` con mensaje apropiado cuando hay tickets disponibles.
- **FR-003**: El framework DEBE validar `409 Conflict` con mensaje apropiado en registro duplicado.
- **FR-004**: El framework DEBE verificar el estado de la cola vía `GET /api/v1/waitlist/has-pending`.
- **FR-005**: La configuración del Actor "The System" DEBE apuntar al Waitlist.Api en `http://localhost:5006`.
- **FR-006**: Los modelos de Request/Response DEBEN ser POJOs con Lombok `@Data @Builder`.
- **FR-007**: El Runner DEBE filtrar por tag `@HU-Waitlist`.

### Key Entities

- **WaitlistJoinRequest**: `{ email: String, eventId: UUID }`
- **WaitlistJoinResponse**: `{ entryId: String, position: int }`
- **WaitlistErrorResponse**: `{ message: String }`
- **WaitlistHasPendingResponse**: `{ hasPending: boolean, pendingCount: int }`

---

## Success Criteria *(mandatory)*

- **SC-001**: 100% de llamadas con evento stock=0 y email válido retornan `201` con `entryId` no nulo.
- **SC-002**: 100% de intentos con stock>0 retornan `409` con mensaje de tickets disponibles.
- **SC-003**: 100% de registros duplicados retornan `409` con mensaje de ya registrado.
- **SC-004**: El endpoint `has-pending` responde `200 OK` con campo `hasPending` booleano correcto.
- **SC-005**: Los tests de US1 (escenarios 1-3) pasan de forma independiente sin infraestructura Kafka.

---

## Clarifications

- **Q**: ¿Puerto del Waitlist.Api para tests locales? → **A**: `5006` (docker: `5006:5006`, diferente a otros servicios que usan `500x`).
- **Q**: ¿Requiere autenticación el endpoint de waitlist? → **A**: No. Sin `[Authorize]` en el controlador.
- **Q**: ¿Cómo verificar escenarios 4-6 que dependen de Kafka/workers? → **A**: Verificación indirecta vía `has-pending` + polling con timeout. Requieren infraestructura completa.
- **Q**: ¿Qué formato tiene la respuesta 409? → **A**: `{ "message": "..." }` (WaitlistConflictException → Conflict()).

---

## Assumptions

- **A-001**: El Waitlist.Api está corriendo en `http://localhost:5006` al ejecutar los tests.
- **A-002**: Para escenarios 1 y 3, el evento de prueba se crea vía Catalog.Api (`http://localhost:50001`) en el Hook `@Before`.
- **A-003**: Para escenario 1 (stock=0), el Hook bloquea todos los asientos del evento de prueba vía Inventory.Api antes de ejecutar el step.
- **A-004**: Los escenarios 4-6 son de integración completa y requieren Kafka + workers activos (`@AsignacionAutomatica`, `@LiberacionConSiguiente`, `@LiberacionSinCola`).
