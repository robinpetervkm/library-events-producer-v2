# Product Requirements Document (PRD)

## 1) Product Title
**Library Events Producer API v2**

## 2) Document Metadata
| Property | Value |
|----------|-------|
| **Version** | 1.0 (Draft) |
| **Date** | March 21, 2026 |
| **Owner** | Engineering / Platform Team |
| **Target Stack** | Spring Boot 4, Java 25, Apache Kafka |

---

### 3) Purpose
Build a REST API application that accepts library event requests (ADD, UPDATE) and publishes each event to a Kafka topic for downstream consumers.

### 4) Background / Problem Statement
Library systems need an event-driven mechanism to notify downstream services when books are added or updated. A producer service is required to:
- Expose HTTP APIs for event intake
- Validate input
- Publish events to Kafka in a consistent schema
- Provide clear success/error responses for clients

### 5) Goals
- Provide two REST endpoints: POST and PUT for library events.
- Publish every valid request as a message to Kafka topic.
- Enforce event type semantics:
  - **POST => ADD**
  - **PUT => UPDATE**
- Standardize event payload with LibraryEvent and nested Book details.
- Ensure compatibility with Spring Boot 4 and Java 25.

### 6) Non-Goals
- No consumer-side processing logic in this service.
- No database persistence (unless explicitly added later).
- No UI.
- No event deletion flow.

### 7) Users / Stakeholders
| Role | Description |
|------|-------------|
| **API Clients** | Internal services that submit library events. |
| **Platform/Integration Team** | Manages Kafka and integration contracts. |
| **Operations/SRE** | Monitors reliability and runtime health. |

---

### 8) Functional Requirements

#### FR-1: POST endpoint to create ADD events
- Endpoint accepts Library Event payload with Book details.
- If payload is valid, publish to Kafka topic.
- Event type for POST must be ADD.
- Return HTTP success response with published event details (or metadata).

#### FR-2: PUT endpoint to create UPDATE events
- Endpoint accepts LibraryEvent payload with library EventId and Book details.
- If payload is valid, publish to Kafka topic.
- Event type for PUT must be UPDATE.
- Return HTTP success response with published event details (or metadata).

#### FR-3: Input validation
- **libraryEventId**: Optional/nullable for ADD (POST) unless business decides otherwise. Mandatory for UPDATE (PUT).
- **eventType**: Must be one of: ADD, UPDATE.
- **book object**: Required for both operations.
- **book.bookId**, **book.bookName**, **book.bookAuthor**: Required and non-empty.

#### FR-4: Error handling
- Return **400 Bad Request** for validation failures with clear error details.
- Return **500 Internal Server Error** (or mapped integration error) when Kafka publish fails unexpectedly.
- Include correlation/trace identifier in error responses.

#### FR-5: Kafka publishing
- Publish valid events to configured topic (default: `library-events`).
- Message payload mirrors API contract.
- Use `libraryEventId` as message key where present (or deterministic fallback strategy).
- Log publish success/failure with key identifiers.

---

### 9) API Specification (Draft)

#### POST /v1/library-events
**Purpose**: Publish new library event (ADD)

**Request Body (example)**:
```json
{
  "libraryEventId": null,
  "eventType": "ADD",
  "book": {
    "bookId": 123,
    "bookName": "Kafka for Developers",
    "bookAuthor": "Jane Doe"
  }
}
```

**Responses**:
- **201 Created** (or 200 OK, team decision) on success
- **400 Bad Request** on invalid payload
- **500 Internal Server Error** on publish failure

#### PUT /v1/library-events
**Purpose**: Publish update event (UPDATE)

**Request Body (example)**:
```json
{
  "libraryEventId": 1001,
  "eventType": "UPDATE",
  "book": {
    "bookId": 123,
    "bookName": "Kafka for Developers 2nd Edition",
    "bookAuthor": "Jane Doe"
  }
}
```

**Responses**:
- **200 OK** on success
- **400 Bad Request** on invalid payload
- **500 Internal Server Error** on publish failure

---

### 10) Data Model Requirements

#### LibraryEvent
| Field | Type | Description |
|-------|------|-------------|
| `libraryEventId` | Long/Integer | Unique event identifier |
| `eventType` | Enum: ADD, UPDATE | Type of event |
| `book` | Book | Nested book details |

#### Book
| Field | Type | Description |
|-------|------|-------------|
| `bookId` | Long/Integer | Unique book identifier |
| `bookName` | String | Title of the book |
| `bookAuthor` | String | Author of the book |

---

### 11) Kafka Contract Requirements
| Property | Value |
|----------|-------|
| **Topic name** | Configurable via application properties (`library-events` default) |
| **Value serializer** | JSON |
| **Message schema** | Matches Library Event |
| **Partitioning/keying** | Keyed by `libraryEventId` when available |
| **Delivery behavior** | At-least-once (baseline) |
| **Retries** | Configurable producer retries/backoff |

---

### 12) Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| **Performance** | p95 API latency under agreed internal SLA (e.g., < 300 ms excluding Kafka broker issues) |
| **Reliability** | Graceful handling/retry for transient Kafka errors |
| **Scalability** | Horizontally scalable stateless API service |
| **Observability** | Structured logs with event id, book id, event type; metrics for request count, success/failure publish, latency; health endpoints for readiness/liveness |
| **Security** | TLS in transit; authN/authZ strategy (e.g., OAuth2/JWT) per platform standard; avoid logging sensitive data |

---

### 13) Technical Constraints
- Must run on **Java 25**.
- Must use **Spring Boot 4**.
- Build tool: **Gradle**.
- Container-ready deployment recommended.
- Kafka broker configuration externalized in environment/config properties.

---

### 14) Acceptance Criteria
- POST with valid ADD payload publishes one Kafka message and returns success.
- PUT with valid UPDATE payload publishes one Kafka message and returns success.
- Invalid payloads are rejected with descriptive 400 responses.
- Kafka publish failures are surfaced via server error response and error logs.
- Unit + integration tests cover validation, endpoint behavior, and Kafka publish path.
- API contract documented (OpenAPI/Swagger preferred).

---

### 15) Testing Requirements
- **Unit tests**: controller validation, service logic, event type enforcement.
- **Integration tests**: API-to-Kafka publish flow (embedded Kafka or testcontainers).
- **Contract tests**: payload schema consistency for producer/consumer expectations.
- **Negative tests**: missing `libraryEventId` on UPDATE, missing book, invalid enum values.

---

### 16) Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| **Schema drift between producer and consumers** | Versioned contract and consumer contract tests. |
| **Duplicate events from retries** | Idempotency strategy at consumer side; stable event keys. |
| **Kafka outages impacting API success rate** | Retry/backoff, circuit-breaker patterns, clear operational alerts. |

---

### 17) Open Questions

| Question | Decision |
|----------|----------|
| Should POST strictly force eventType=ADD even if client sends another value? | **Yes** |
| Should `libraryEventId` for ADD be generated by API or provided by client? | **Generated by the upstream app** |
| Expected success response format: full event payload vs metadata-only? | **Full payload** |
| Required authentication mechanism for API clients? | **Not needed** |
| Do we need dead-letter topic behavior in this phase? | **Not right now** |

---

*Document Version: 1.0 (Draft)*  
*Date: March 21, 2026*  
*Owner: Engineering / Platform Team*  
*Author: Generated via Claude Code*