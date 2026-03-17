# Data Model: [DEPRECATED] API Event CRUD Flow

**NOTE**: This document serves as the data dictionary for the API automation suite.

## 1. EventRequest (POST/PUT)
Used for creating (`POST`) and updating (`PUT`) operations.

| Field | Type | Example | Description |
| :--- | :--- | :--- | :--- |
| `name` | `String` | "Festival Jazz 2026" | Event name (required) |
| `description` | `String` | "Jazz music festival" | Detailed info |
| `eventDate` | `String` | "2026-10-15T00:00:00Z" | ISO 8601 (future date) |
| `venue` | `String` | "Teatro Nacional" | Location |
| `maxCapacity` | `Integer` | 500 | Total seats |
| `basePrice` | `Double` | 50.00 | Ticket price |

---

## 2. EventResponse (GET/POST Response)
Complete structure returned for discovery and status checks.

| Field | Type | Example | Description |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `550e8...` | Guid-compatible identifier |
| `name` | `String` | "Festival Jazz 2026" | Event name |
| `eventDate` | `String` | "2026-10-15T00:00:00Z" | ISO 8601 |
| `status` | `String` | "active", "inactive" | Current system status |

---

## 3. Serialization Rules
- **Date Format**: ISO-8601 hyphenated.
- **UUID**: Standard string (hyphenated) to match C# `Guid`.
- **Actor Mapping**: Data is populated from `hu05-admin-config.feature` tables.
