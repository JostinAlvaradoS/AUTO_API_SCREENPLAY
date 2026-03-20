# Research: API Automation with Serenity Screenplay Rest

This document summarizes the research tasks for implementing a CRUD flow for the Events resource.

## 1. `CallAnApi` Ability & Session Management

**Rationale**: To execute REST requests, the Actor needs the `CallAnApi` ability. In a multi-step CRUD flow (POST -> GET -> PUT -> DELETE), the Actor must maintain consistency in the base URL and potentially shared state (like the created resource ID).

**Decision**:
- **Ability Initialization**: The Actor should be initialized with `CallAnApi.at(baseUrl)`. This sets the base URI for all subsequent interactions (Get, Post, Put, Delete).
- **Session Management**: Use `Serenity.setSessionVariable("VARIABLE_NAME").to(value)` to pass IDs (e.g., `eventId`) between steps in the CRUD flow.
- **Actor Persistence**: Use a single Actor (e.g., "The System") throughout the scenario to keep the `CallAnApi` ability active.

**Alternatives**:
- Manually passing the URL to every `rest()` interaction (not recommended, violates Screenplay).
- Using a global static variable (bad practice for parallel execution).

---

## 2. C# `Guid` to Java `UUID` Serialization

**Rationale**: The backend (Catalog.Api) is built with C# and uses `Guid`. The Java automation suite uses `java.util.UUID`. We need to ensure Jackson/Gson can handle the conversion without custom logic.

**Decision**:
- **Standard Compatibility**: Both C# `Guid` and Java `UUID` follow the RFC 4122 standard. Jackson (the default for Serenity Rest) handles string-to-UUID conversion automatically if the POJO field is of type `java.util.UUID`.
- **Implementation**: Define the `id` field in POJOs as `private UUID id;`. No custom serializers are required unless the backend uses a non-standard binary format (which is rare for JSON APIs).

**Alternatives**:
- Using `String` for IDs and manually parsing (extra boilerplate).
- Custom `JsonDeserializer` (only if the backend returns GUIDs in an unusual format like `N` or `P` without dashes).

---

## 3. Accessing `serenity.conf` Properties

**Rationale**: Configuration like `restapi.baseurl` should be centralized in `serenity.conf` and accessible within the Screenplay framework.

**Decision**:
- **EnvironmentVariables**: In Screenplay Tasks or Step Definitions, inject `EnvironmentVariables` using the `@Steps` or `@Managed` equivalent logic, or use `ConfigurableEnvironmentVariables`.
- **Retrieval**: Use `EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("restapi.baseurl")`. This is the Serenity-idiomatic way to support multi-environment setups (dev, staging, prod).
- **Actor Setup**: Typically done in a `@Before` hook: 
  ```java
  OnStage.theActorCalled("The System").can(CallAnApi.at(baseUrl));
  ```

**Alternatives**:
- `System.getProperty()`: Harder to manage for complex configuration.
- Hardcoding: Not acceptable for professional automation.
