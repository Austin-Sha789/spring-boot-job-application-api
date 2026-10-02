# Job Application REST API

## Overview
A RESTful backend application built with Java and Spring Boot for managing job applications.

The API supports creating, retrieving, updating, deleting, filtering, and paginating job application records. It also includes input validation, centralized exception handling, integration testing with MockMvc, persistent H2 database storage, and interactive API documentation using OpenAPI and Swagger UI.

## Features

Create, retrieve, update, and delete job applications through RESTful endpoints

Filter applications by company and application status

Combine company and status filters in a single request

Paginate application results using configurable page and size query parameters

Perform case-insensitive filtering with Spring Data JPA derived query methods

Validate incoming request data using Jakarta Bean Validation

Return centralized 404 Not Found responses through custom exceptions and @ControllerAdvice

Persist application data using Spring Data JPA and H2

Test API behaviour with Spring Boot, MockMvc, and an isolated in-memory test database

Explore and test endpoints interactively through OpenAPI and Swagger UI

## Tech Stack

- Spring Web / Spring MVC — REST controller and HTTP request handling

- Spring Data JPA — repository abstraction and database access

- Hibernate — JPA implementation and ORM

- H2 Database — persistent development database and in-memory test database

- Jakarta Bean Validation — request validation using annotations such as @NotBlank and @Valid

- JUnit 5 — test framework

- MockMvc — integration testing of REST endpoints

- Springdoc OpenAPI — automatic OpenAPI specification generation

- Swagger UI — interactive API documentation and endpoint testing

- Maven — dependency management and build tool

- Git / GitHub — version control and project history

## Architecture

The application follows a layered architecture to separate responsibilities and reduce coupling between components.

Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
H2 Database

## Controller Layer

The controller layer handles HTTP requests and responses.

Responsibilities include:

Defining REST API endpoints

Reading path variables and query parameters

Receiving and validating request bodies

Returning appropriate HTTP responses and status codes

Passing application logic to the service layer

## Service Layer

The service layer contains the main application and business logic.

Responsibilities include:

Coordinating CRUD operations

Applying filtering and pagination logic

Checking whether requested job applications exist

Throwing custom exceptions when resources are not found

Keeping business logic separate from HTTP and database concerns

## Repository Layer

The repository layer is responsible for database access.

JobApplicationRepository extends Spring Data JPA's JpaRepository, providing standard CRUD operations and custom derived queries such as:

findByCompanyIgnoreCase(...)
findByStatusIgnoreCase(...)
findByCompanyIgnoreCaseAndStatusIgnoreCase(...)

Spring Data JPA and Hibernate translate these repository operations into SQL that is executed against the H2 database.

## Exception Handling

Resource-not-found errors are handled centrally rather than being repeated inside each controller method.

Service
  ↓
JobApplicationNotFoundException
  ↓
GlobalExceptionHandler
  ↓
@ControllerAdvice
  ↓
@ExceptionHandler
  ↓
404 Not Found

This keeps the controllers focused on HTTP handling and provides consistent error responses across GET, PUT, and DELETE operations.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/applications` | Retrieve job applications with optional filtering and pagination |
| `GET` | `/applications/{id}` | Retrieve a single job application by ID |
| `POST` | `/applications` | Create a new job application |
| `PUT` | `/applications/{id}` | Update an existing job application |
| `DELETE` | `/applications/{id}` | Delete an existing job application |

## Filtering and Pagination

The GET /applications endpoint supports optional query parameters for filtering and pagination.

## Query Parameters

| Parameter | Required | Default | Description |
|---|---|---|---|
| `company` | No | — | Filter applications by company name |
| `status` | No | — | Filter applications by application status |
| `page` | No | `0` | Zero-based page number |
| `size` | No | `10` | Maximum number of records per page |

## Examples

Retrieve all applications using the default pagination settings:

GET /applications

Filter by company:

GET /applications?company=Microsoft

Filter by status:

GET /applications?status=Applied

Filter by both company and status:

GET /applications?company=Microsoft&status=Applied

Request the second page with two records per page:

GET /applications?page=1&size=2

Combine filtering and pagination:

GET /applications?company=Microsoft&status=Applied&page=0&size=5

Filtering by company and status is case-insensitive.

## Paginated Response

A paginated response includes both the application records and pagination metadata.

Example:

{
  "content": [
    {
      "id": 1,
      "company": "Microsoft",
      "position": "Software Engineer",
      "status": "Applied"
    }
  ],
  "number": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}

content contains the records for the current page, while fields such as totalElements, totalPages, number, first, and last describe the pagination state.

## Validation and Error Handling

Incoming request data is validated using Jakarta Bean Validation.

For example, required fields such as company, position, and status use @NotBlank constraints to prevent null, empty, or whitespace-only values.

@NotBlank
private String company;

Controller methods use @Valid to trigger validation for incoming request bodies.

public ResponseEntity<JobApplication> createApplication(
        @Valid @RequestBody JobApplication application) {
    ...
}

Invalid request data results in:

400 Bad Request

Resource-not-found errors are handled through a custom unchecked exception:

JobApplicationNotFoundException

The service layer throws this exception when a requested application does not exist.

repository.findById(id)
        .orElseThrow(() ->
                new JobApplicationNotFoundException(id));

A global exception handler uses @ControllerAdvice and @ExceptionHandler to convert the exception into a consistent HTTP response.

Example:

404 Not Found

Job application not found with id: 999999

This keeps error handling centralized and avoids repeating null checks and 404 response logic across multiple controller methods.

## API Documentation

OpenAPI documentation is generated automatically using Springdoc OpenAPI.

After starting the application, Swagger UI can be accessed at:

http://localhost:8080/swagger-ui.html

Swagger UI provides an interactive interface for:

Viewing available API endpoints

Inspecting request parameters

Viewing request and response schemas

Testing GET, POST, PUT, and DELETE operations directly from the browser

Testing filtering and pagination query parameters

The raw OpenAPI specification is available at:

http://localhost:8080/v3/api-docs

## Testing

The application includes integration tests using Spring Boot, JUnit 5, and MockMvc.

Tests cover the main API behaviours, including:

- Creating valid job applications
- Rejecting invalid request data with `400 Bad Request`
- Retrieving applications by ID
- Returning `404 Not Found` for missing applications
- Updating existing applications
- Preventing invalid updates
- Deleting applications
- Filtering by company
- Filtering by status
- Combining company and status filters
- Returning paginated results
- Verifying first-page and second-page pagination metadata
- Checking centralized exception response messages

The test environment uses a separate in-memory H2 database so that automated tests do not modify the persistent development database.

Test configuration:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

Tests can be run with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

At the current stage, the project contains **16 passing tests**.

## Logging

The service layer uses SLF4J logging to record important application events.

Examples include:

```text
Fetching applications with company=Microsoft, status=Applied, page=0, size=10
Created job application with id=5
Updated job application with id=5
Deleted job application with id=5
Job application not found with id=999999
```

Informational application events use `INFO` logs, while missing-resource situations use `WARN`.

Parameterized logging is used instead of manual string concatenation:

```java
logger.info(
    "Created job application with id={}, company={}, position={}",
    created.getId(),
    created.getCompany(),
    created.getPosition()
);
```

This keeps runtime activity visible without mixing logging concerns into controller logic.

## How to Run

### Prerequisites

Make sure the following are installed:

- Java 21
- Maven, or use the included Maven Wrapper

### Start the Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS or Linux:

```bash
./mvnw spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

### Swagger UI

After the application starts, open:

```text
http://localhost:8080/swagger-ui.html
```

This can be used to explore and test the API interactively.

### Run Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On macOS or Linux:

```bash
./mvnw test
```

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.austin.jobtracker
│   │       ├── controller
│   │       ├── exception
│   │       ├── model
│   │       ├── repository
│   │       └── service
│   └── resources
│
└── test
    ├── java
    └── resources
```

The project separates API handling, business logic, database access, domain models, and exception handling into dedicated packages.

## Future Improvements

Possible future improvements include:

- Replacing direct entity exposure with DTOs
- Adding authentication and authorization
- Adding sorting support
- Adding more advanced search criteria
- Migrating from H2 to a production database such as PostgreSQL
- Adding Docker support
- Adding CI/CD with GitHub Actions
- Improving structured error response bodies