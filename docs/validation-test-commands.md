# Validation Test Commands

## Table of Contents

1. [POST /v1/library-events (Flow 1: ADD Event)](#post-v1library-events-flow-1-add-event)
   - [✅ Valid Request (Expected: 201 Created)](#-valid-request-expected-201-created)
   - [❌ Invalid eventType (Expected: 400 Bad Request)](#-invalid-eventtype-expected-400-bad-request)
   - [❌ Missing eventType (Expected: 400 Bad Request)](#-missing-eventtype-expected-400-bad-request)
   - [❌ Missing book (Expected: 400 Bad Request)](#-missing-book-expected-400-bad-request)
   - [❌ Missing bookId (Expected: 400 Bad Request)](#-missing-bookid-expected-400-bad-request)
   - [❌ Blank bookName (Expected: 400 Bad Request)](#-blank-bookname-expected-400-bad-request)
   - [❌ Blank bookAuthor (Expected: 400 Bad Request)](#-blank-bookauthor-expected-400-bad-request)
   - [❌ All book fields missing (Expected: 400 Bad Request with multiple errors)](#-all-book-fields-missing-expected-400-bad-request-with-multiple-errors)
   - [❌ Empty JSON body (Expected: 400 Bad Request)](#-empty-json-body-expected-400-bad-request)

2. [PUT /v1/library-events (Flow 2: UPDATE Event)](#put-v1library-events-flow-2-update-event)
   - [✅ Valid Request (Expected: 200 OK)](#-valid-request-expected-200-ok)
   - [❌ Invalid eventType (Expected: 400 Bad Request)](#-invalid-eventtype-expected-400-bad-request-1)
   - [❌ Missing libraryEventId (Expected: 400 Bad Request)](#-missing-libraryeventid-expected-400-bad-request)
   - [❌ Missing eventType (Expected: 400 Bad Request)](#-missing-eventtype-expected-400-bad-request-1)
   - [❌ Missing book (Expected: 400 Bad Request)](#-missing-book-expected-400-bad-request-1)
   - [❌ Missing bookId (Expected: 400 Bad Request)](#-missing-bookid-expected-400-bad-request-1)
   - [❌ Blank bookName (Expected: 400 Bad Request)](#-blank-bookname-expected-400-bad-request-1)
   - [❌ Blank bookAuthor (Expected: 400 Bad Request)](#-blank-bookauthor-expected-400-bad-request-1)
   - [❌ All book fields missing (Expected: 400 Bad Request with multiple errors)](#-all-book-fields-missing-expected-400-bad-request-with-multiple-errors-1)

3. [PowerShell Versions (Windows)](#powershell-versions-windows)
   - [Valid POST](#valid-post)
   - [Invalid eventType (UPDATE for POST)](#invalid-eventtype-update-for-post)
   - [Missing book](#missing-book)
   - [Missing bookId](#missing-bookid)
   - [Valid PUT](#valid-put)
   - [Invalid eventType (ADD for PUT)](#invalid-eventtype-add-for-put)
   - [Missing libraryEventId](#missing-libraryeventid)
   - [Missing eventType](#missing-eventtype)
   - [Missing book](#missing-book-1)
   - [Missing bookId](#missing-bookid-1)
   - [All book fields missing](#all-book-fields-missing)

4. [Quick Reference: Windows JSON Escaping Rules](#quick-reference-windows-json-escaping-rules)

5. [Expected Error Response Format](#expected-error-response-format)
   - [Bean Validation Errors (400)](#bean-validation-errors-400)
   - [Controller-Level Validation (400)](#controller-level-validation-400)

6. [Running the Tests](#running-the-tests)

---

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

### ✅ Valid Request (Expected: 200 OK)

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

### ❌ Missing bookId (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": {
      "bookId": null,
      "bookName": "Clean Code",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Blank bookName (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": {
      "bookId": 101,
      "bookName": "",
      "bookAuthor": "Robert Martin"
    }
  }'
```

### ❌ Blank bookAuthor (Expected: 400 Bad Request)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": {
      "bookId": 101,
      "bookName": "Clean Code",
      "bookAuthor": "   "
    }
  }'
```

### ❌ All book fields missing (Expected: 400 Bad Request with multiple errors)

```bash
curl -X PUT "http://localhost:8080/v1/library-events" \
  -H "Content-Type: application/json" \
  -d '{
    "libraryEventId": 1,
    "eventType": "UPDATE",
    "book": {
      "bookId": null,
      "bookName": "",
      "bookAuthor": null
    }
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

### Valid PUT
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"eventType":"UPDATE","book":{"bookId":101,"bookName":"Clean Code (Updated)","bookAuthor":"Robert Martin"}}'
```

### Invalid eventType (ADD for PUT)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"eventType":"ADD","book":{"bookId":101,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### Missing libraryEventId
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":null,"eventType":"UPDATE","book":{"bookId":101,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### Missing eventType
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"book":{"bookId":101,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### Missing book
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"eventType":"UPDATE","book":null}'
```

### Missing bookId
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"eventType":"UPDATE","book":{"bookId":null,"bookName":"Clean Code","bookAuthor":"Robert Martin"}}'
```

### All book fields missing
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/v1/library-events" `
  -Method PUT `
  -ContentType "application/json" `
  -Body '{"libraryEventId":1,"eventType":"UPDATE","book":{"bookId":null,"bookName":"","bookAuthor":null}}'
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