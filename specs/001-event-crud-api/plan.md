# Implementation Plan: API Event CRUD Flow

**Branch**: `001-event-crud-api` | **Date**: 2026-03-16 | **Spec**: [spec.md](./spec.md)
**SSOT (Gherkin)**: [hu05-admin-config.feature](../../shared-specs/specs/001-ticketing-mvp/hu05-admin-config.feature)
**Input**: Feature specification for a complete CRUD flow using Serenity Screenplay Rest, including contract validation via JSON Schema.

## Summary

The goal is to implement a robust API automation suite for the Events resource following the **Catalog.Api** (C#) technical base. The implementation will use the Screenplay pattern with the Actor "**The System**", utilizing the `CallAnApi` ability. A key differentiator in this plan is the enforcement of **JSON Schema Validation** for all responses to comply with the project's Constitution.

## Technical Context

**Language/Version**: Java 17+ (Standard for Serenity)
**Primary Dependencies**: Serenity Screenplay, Serenity Rest, JUnit 5/Cucumber, Jackson (JSON Mapping)
**Storage**: Serenity Session (for cross-step eventId persistence)
**Testing**: Serenity Screenplay Rest with **JSON Schema Validation**
**Target Platform**: JVM
**Project Type**: API Automation Project
**Constraints**: Base URL `http://localhost:50001` (from `serenity.conf`), No Auth scope (Internal Network/Trust-based).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Verb Compliance**: Use `POST .../deactivate` for Soft Delete as specified in business rules (exception to generic DELETE verb).
- [x] **Screenplay Compliance**: Use Actor "The System" and Tasks/Questions structure.
- [x] **Contract Safety**: Mandated **JSON Schema Validation** in all validation questions.

## Project Structure

### Documentation (this feature)

```text
specs/001-event-crud-api/
├── plan.md              # This file
├── research.md          # UUID/Guid mapping and Actor patterns
├── data-model.md        # POJO definitions (Request/Response)
├── quickstart.md        # Feature file sync (es)
└── tasks.md             # Functional tasks including contract verification
```

### Source Code

```text
src/
├── main/java/com/sofka/automation/
│   ├── models/          # EventRequest, EventResponse (UUID compatible)
│   ├── tasks/           # PostEvent, GetEvent, PutEvent, DeactivateEvent
│   ├── questions/       # TheEventResponseCode, TheEventSchema, TheEventDetails
│   └── utils/           # Endpoints, SessionManager, EnvConfig
└── test/
    ├── java/com/sofka/automation/
    │   ├── runners/     # EventRunner (pointing to shared-specs)
    │   └── stepdefinitions/ # Cucumber Steps (language: es)
    └── resources/
        └── serenity.conf # Configuration (http://localhost:50001)
```

**Structure Decision**: Standard Screenplay architecture with a focus on contract testing (Schema Validation) and semantic Actor initialization.

---

## Phase 0: Outline & Research

1. **Research Task**: Finalize JSON Schema generation for the Event resource from `catalog.yaml`.
2. **Research Task**: Investigate the exact behavior of `serenity-rest` when validating nested UUID formats in schemas.

## Phase 1: Design & Contracts

1. **JSON Schema**: Create the expected schemas for `EventResponse` and list responses.
2. **Data Model**: Update `data-model.md` to reflect mandatory fields and validation rules.
3. **Agent context**: Run `update-agent-context.sh` to synchronize Copilot with the Schema Validation requirement.

---

## Phase 2: Implementation Strategy

1. **Infrastructure**: Setup `serenity.conf` and `Hooks.java` for Actor initialization.
2. **Foundational**: Implement `PostEvent` Task and `TheEventSchema` Question.
3. **CRUD Cycle**: 
   - **Step 1**: POST Create -> Validate Schema & Code 201.
   - **Step 2**: GET Retrieve -> Validate Schema & Integrity.
   - **Step 3**: PUT Update -> Validate restricted fields rules.
   - **Step 4**: POST Deactivate -> Validate Soft Delete status.
4. **Assertive Logging**: Ensure all API failures include the JSON response body for faster debugging.
