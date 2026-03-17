# Feature Specification: API Event CRUD Flow

**Feature Branch**: `001-event-crud-api`  
**Created**: 2026-03-16  
**Status**: Draft  
**Input**: Generar el specs.md para un flujo CRUD completo (POST, GET, PUT, DELETE) del recurso de Eventos. Contexto: Taller Automatización API con Screenplay. 
**Gherkin Source of Truth**: [shared-specs/specs/001-ticketing-mvp/hu05-admin-config.feature](../../shared-specs/specs/001-ticketing-mvp/hu05-admin-config.feature)
**Technical Reference**: [shared-specs/specs/001-ticketing-mvp/catalog-admin.feature](../../shared-specs/specs/001-ticketing-mvp/catalog-admin.feature)
**Technical Base**: Catalog.Api.

## User Scenarios & Testing *(mandatory)*
**Source Location**: Features will be read directly from `shared-specs/specs/001-ticketing-mvp/`.
### User Story 1 - Create Event (Priority: P1) 🎯 MVP

As an administrator, I want to create a new event with its basic information so that I can start configuring the ticket sales for it.

**Why this priority**: Core functionality needed to populate the catalog and enable further operations.

**Independent Test**: Use a POST request to `/admin/events` with valid event details and verify the 201 Created response.

**Acceptance Scenarios**:

1. **Given** a valid event payload, **When** I send a POST request to `/admin/events`, **Then** a new event is created with state "active", an ID is generated, and its details are returned in the response.
2. **Given** an event payload with an empty name, **When** I send a POST request to `/admin/events`, **Then** the creation fails with a 400 Bad Request and a validation message "El nombre del evento es obligatorio".
3. **Given** an event payload with a past date, **When** I send a POST request to `/admin/events`, **Then** the creation fails with a validation error "La fecha del evento debe ser futura".

---

### User Story 2 - Read Event Information (Priority: P1) 🎯 MVP

As a user or administrator, I want to retrieve details of existing events so that I can see what's available or verify my configurations.

**Why this priority**: Essential for verifying creations and enabling the user-facing side of the marketplace.

**Independent Test**: Send a GET request to `/Events` or `/Events/{id}` and verify the 200 OK response with the expected event data.

**Acceptance Scenarios**:

1. **Given** existing events in the system, **When** I send a GET request to `/Events`, **Then** I receive a list of all active events.
2. **Given** a valid and existing event ID, **When** I send a GET request to `/Events/{id}`, **Then** I receive the specific details of that event.
3. **Given** a non-existent event ID, **When** I send a GET request to `/Events/{id}`, **Then** I receive a 404 Not Found response.

---

### User Story 3 - Update Event (Priority: P2)

As an administrator, I want to update an event's details (name, description, capacity) so that I can fix errors or adjust configurations.

**Why this priority**: Important for maintenance, but secondary to the ability to create and list events.

**Independent Test**: Send a PUT request to `/admin/events/{id}` with updated fields for an existing event and verify the 200 OK response.

**Acceptance Scenarios**:

1. **Given** an existing event, **When** I send a PUT request to `/admin/events/{id}` with updated basic fields, **Then** the event is updated and the new details are stored.
2. **Given** an existing event that already has reservations, **When** I attempt to update restricted fields like `EventDate` or `BasePrice` (if applicable), **Then** the system prevents the update and returns a business rule error.

---

### User Story 4 - Remove Event (Soft Delete) (Priority: P2)

As an administrator, I want to deactivate an event so that it is no longer visible to users, especially if it was created by mistake or cancelled.

**Why this priority**: Necessary for lifecycle management and avoiding corrupted sales data.

**Independent Test**: Send a POST request to `/admin/events/{id}/deactivate` and verify the event is no longer returned in public listings.

**Acceptance Scenarios**:

1. **Given** an existing event with no active reservations, **When** I send a deactivation request, **Then** the status code is **200 OK**, the event status changes to "inactive" in the response body, and the event is no longer visible in public listings.
2. **Given** an event with active reservations, **When** I attempt to deactivate it, **Then** the request fails with a validation error indicating that active reservations exist.

### Edge Cases

- Attempting to update an event that was just soft-deleted.
- Creating an event with the maximum possible capacity (boundary test).
- Concurrent retrieval of an event while it is being updated.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST provide a POST endpoint for creating events with fields: name, description, date, venue, capacity, and price. Access is trust-based/internal network (No Auth required for automation scope).
- **FR-002**: The system MUST automatically generate a unique UUID for every new event.
- **FR-003**: The system MUST validate that the event date is in the future at creation time.
- **FR-004**: The system MUST support listing all events and retrieving a single event by ID.
- **FR-005**: The system MUST support updating event metadata (name, description, max capacity).
- **FR-006**: The system MUST support a deactivation mechanism (soft delete) via **POST /admin/events/{id}/deactivate** that returns a **200 OK** with the inactive status.
- **FR-007**: The system MUST prevent modification or deactivation of events if active reservations or ticket sales are linked to them.

### Key Entities

- **Event**: Represents a scheduled gathering (concert, sports, etc.). Attributes: ID (String/UUID), Name, Description, Date, Venue, Max Capacity, Base Price, Status (Active/Inactive).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of event creation attempts with valid data must result in a stored event with a unique ID available for immediate retrieval (Synchronous Consistency).
- **SC-002**: 100% of attempts to create events with past dates or empty names must be rejected with 400 Bad Request.
- **SC-003**: Deactivation requests (Soft Delete) MUST return a **200 OK** containing the event object with status `inactive`.
- **SC-004**: 100% of deactivated (soft-deleted) events must be excluded from the public `/Events` GET results.
- **SC-005**: Data consistency must be maintained such that an event ID returned by POST is immediately fetchable via GET `/Events/{id}`.
- **SC-006**: 100% of API responses MUST match their defined JSON Schema to ensure contract stability.

## Clarifications

### Session 2026-03-16

- Q: En el escenario de borrado lógico (Soft Delete) del recurso de Eventos, ¿qué comportamiento de respuesta debe validar la prueba de automatización cuando un evento ha sido desactivado exitosamente? → A: 200 OK con el objeto del evento actualizado mostrando su estado como `inactive`.
- Q: ¿Cuál es el formato esperado para el ID del evento en los contratos de la API? → A: Request: String (UUID) | Response: String (UUID)
- Q: ¿Qué mecanismo de autenticación se debe validar para las operaciones de administración (POST/PUT/DELETE)? → A: No Auth (Confianza/Red interna).
- Q: ¿Cuál es la expectativa de consistencia de datos para las pruebas de automatización tras una creación o actualización exitosa? → A: Sincrónica (Disponibilidad inmediata para GET).
- Q: ¿Qué verbo HTTP y endpoint se utilizarán para la eliminación (borrado lógico) de eventos? → A: POST .../deactivate (Soft Delete con 200 OK).
- Q: ¿Cómo se debe implementar la validación de contratos exigida por la Constitución? → A: Question dedicada (JSON Schema Validation).
- Q: ¿Desde dónde debe el Runner de Cucumber leer los archivos .feature? → A: Referencia directa al directorio shared-specs.

## Assumptions

- **A-001**: The catalog service uses a relational database for event persistence.
- **A-002**: Administrators are correctly authenticated via an upstream identity service; the Catalog.Api trust-based checks (Admin role) are sufficient for this scope.
- **A-003**: "DELETE" operations in the external CRUD request map to the internal `DeactivateEvent` business logic (soft delete).
