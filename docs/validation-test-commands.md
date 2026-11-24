# Validation Test Commands

Base URL: `http://localhost:8080/v1/library-events`

---

## POST /v1/library-events (Flow 1: ADD Event)

### ✅ Valid Request (Expected: 201 Created)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Invalid eventType (Expected: 400 Bad Request)

```bash
# eventType = UPDATE (not allowed for POST)
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "UPDATE",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Missing eventType (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Missing book (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": null
  }'
```

### ❌ Missing bookId (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": {
      "bookId": null,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Blank bookName (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": {
      "bookId": 101,
      "bookName": "",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Blank bookAuthor (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "   "
    }
  }'
```

### ❌ All book fields missing (Expected: 400 Bad Request with multiple errors)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "ADD",
    "book": {
      "bookId": null,
      "bookName": "",
      "bookAuthor": null
    }
  }'
```

### ❌ Empty JSON body (Expected: 400 Bad Request)

```bash
curl -X POST "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{}'
```

---

## PUT /v1/library-events (Flow 2: UPDATE Event)

> Note: These will return 404/405 until Flow 2 is implemented.

### ✅ Valid Request (Expected: 200 OK when Flow 2 implemented)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code (Updated)",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Invalid eventType (Expected: 400 Bad Request)

```bash
# eventType = ADD (not allowed for PUT)
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "ADD",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Missing libraryEventId (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": null,
    "eventType": "UPDATE",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Missing eventType (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Missing book (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": null
  }'
```

---

## PowerShell Versions (Windows)

### Valid POST
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"libraryEventId":null,"eventType":"ADD","book":{"bookId":101,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### Invalid eventType (UPDATE for POST)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"libraryEventId":null,"eventType":"UPDATE","book":{"bookId":101,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### Missing book
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"libraryEventId":null,"eventType":"ADD","book":null}'
```

### Missing bookId
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"libraryEventId":null,"eventType":"ADD","book":{"bookId":null,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

---

## Expected Error Response Format

When validation fails, the response will be a 400 with a structure like:

```json
{
  "timestamp": "2026-11-24T18:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: bookId is required; bookName is required; bookAuthor is required",
  "path": "/v1/library-events"
}
```

Or for controller-level validation (eventType mismatch):

```json
{
  "timestamp": "2026-11-24T18:30:00.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "eventType must be ADD for POST request",
  "path": "/v1/library-events"
}
```

---

## Running the Tests

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```

2. In another terminal, run the curl commands above.

3. Verify responses match expected status codes.