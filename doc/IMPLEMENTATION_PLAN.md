# Layer-By-Layer Implementation Plan

## Layer 0 - Foundation (Project + Build)
- Confirm dependencies in build.gradle: web, validation, kafka, actuator, test, spring-kafka-test.
- Add Jackson databind dependency (aligned with Jackson 2.20.x BOM) for Kafka's JsonSerializer:
  - `implementation 'com.fasterxml.jackson.core:jackson-databind:2.20.2'`
- Add app config in src/main/resources/application.yml for server port, topic name, Kafka bootstrap servers.
  - Keep application.properties removed/empty to avoid split config sources.
  - For tests, prefer src/test/resources/application-test.yml to isolate broker settings.
- Create base package structure under src/main/java/com/learnjava/:
  - controller, domain, dto (optional), service, producer, config, exception.
- Define constants class for topic + API paths to avoid string duplication.
- If using @ConfigurationProperties, enable scanning via @EnableConfigurationProperties or @ConfigurationPropertiesScan (Spring Boot usually handles this if class is registered properly).
- **Exit criteria**: app starts with no custom code errors.

---

## Layer 1 - Domain Model + Validation
- Create Book model: bookId, bookName, bookAuthor with bean validation annotations.
- Create LibraryEvent model: libraryEventId, eventType, book.
- Create LibraryEventType enum: ADD, UPDATE.
- Add validation rules aligned to PRD:
  - book required
  - book fields required/non-empty
  - libraryEventId required for update flow (enforced in API/service)
- **Exit criteria**: models compile and validation annotations are correctly applied.

---

## Layer 2 - API Contract Layer (Controller)
- Create LibraryEventsController in controller.
- Implement POST /v1/library-events:
  - Accept payload
  - Reject if eventType != ADD
  - Return 201 + full payload on success
- Implement PUT /v1/library-events:
  - Require libraryEventId
  - Reject if eventType != UPDATE
  - Return 200 + full payload on success
- Keep controller thin: delegate publish logic to service.
- **Exit criteria**: controller compiles and endpoints are mapped correctly.

---

## Layer 3 - Service Layer (Business Rules)
- Create LibraryEventService interface + implementation.
- Methods:
  - publishNewLibraryEvent(LibraryEvent event)
  - updateLibraryEvent(LibraryEvent event)
- Centralize business checks here too (defense in depth):
  - strict event type per operation
  - required ID for update
- Return the same event payload for controller responses.
- **Exit criteria**: service compiles and business rules are implemented.

---

## Layer 4 - Kafka Producer Layer
- Create LibraryEventProducer using KafkaTemplate<Long, LibraryEvent> (or String key strategy if preferred).
- Publish to configured topic from properties.
- Use libraryEventId as key.
- Add success/failure callbacks with structured logs (libraryEventId, eventType, bookId).
- **Exit criteria**: producer compiles and publishes to configured topic.

---

## Layer 5 - Exception + Error Response Standardization
- Create custom exceptions:
  - InvalidLibraryEventException
  - LibraryEventPublishException
- Add @RestControllerAdvice global handler:
  - map validation + business errors -> 400
  - map Kafka/internal errors -> 500
- Add consistent error payload (timestamp, status, message, path, optional traceId).
- **Exit criteria**: exception handler compiles and maps errors to responses.

---

## Layer 6 - Observability + Runtime Readiness
- Enable actuator endpoints (health/info/metrics) in application.yml.
- Add request/response logging policy (no sensitive payload leakage).
- Add Micrometer counters/timers for:
  - requests by endpoint/status
  - publish success/failure
  - publish latency
- **Exit criteria**: /actuator/health works and metrics exposed.

---

## Layer 7 - Testing Strategy (Must-Have)
- Testing is handled in a separate section/course
- Dependencies for testing (Testcontainers, spring-kafka-test, etc.) are configured in Layer 0
- **Exit criteria**: test dependencies configured and available

---

## Layer 8 - API Documentation + DevX
- Add OpenAPI/Swagger dependency and annotate controller models.
- Publish endpoint examples matching PRD in docs/:
  - request/response success and error samples
- Add docs/IMPLEMENTATION_PLAN.md and optional docs/TEST_PLAN.md.
- **Exit criteria**: docs reflect actual code behavior exactly.

---

## Layer 9 - Hardening + Pre-Release
- Validate property externalization for env-specific Kafka config.
- Add retry/backoff producer settings from PRD requirements.
- Check idempotency implications are documented (at-least-once semantics).
- Smoke test both endpoints end-to-end with local Kafka (compose.yaml if used).
- **Exit criteria**: release checklist complete, no blocker defects.