# todo-api

Task Manager (To-Do API)

REST API for managing tasks per user. Each user registers and logs in (JWT authentication) to create, list, update, and delete their own tasks, with filtering by status and priority — never seeing other users' tasks. Interactive documentation available via Swagger.

![alt text](image.png)

## Architecture

### Level 1 — System Context

```mermaid
C4Context
    title System Context diagram - Todo API

    Person(user, "User", "A person who wants to manage their own tasks")
    System(todoApi, "Todo API", "Allows a user to register, authenticate, and manage their own tasks via a REST API")
    SystemDb(database, "PostgreSQL Database", "Stores users and tasks")

    Rel(user, todoApi, "Registers, logs in, and manages tasks via", "HTTPS/JSON")
    Rel(todoApi, database, "Reads from and writes to", "JDBC")
```

### Level 2 — Containers

```mermaid
C4Container
    title Container diagram - Todo API

    Person(user, "User", "A person who wants to manage their own tasks")

    System_Boundary(todoApi, "Todo API") {
        Container(api, "API Application", "Java 21, Spring Boot", "Handles registration, login (JWT), and CRUD of tasks scoped per user. Exposes REST endpoints and Swagger UI documentation.")
        ContainerDb(db, "Database", "PostgreSQL 16", "Stores users and tasks")
    }

    Rel(user, api, "Registers, logs in, and manages tasks via", "HTTPS/JSON")
    Rel(api, db, "Reads from and writes to", "JDBC")
```

### Level 3 — Components (Spring Boot application)

```mermaid
C4Component
    title Component diagram - API Application (Todo API)

    Container(client, "API Client", "HTTP client / Swagger UI", "Sends REST requests")
    ContainerDb(db, "Database", "PostgreSQL 16", "Stores users and tasks")

    Container_Boundary(api, "API Application") {
        Component(authController, "Auth Controller", "Spring MVC REST Controller", "Handles /auth/register and /auth/login")
        Component(taskController, "Task Controller", "Spring MVC REST Controller", "Handles /tasks CRUD and filtering by status/priority")

        Component(userService, "User Service", "Spring Service", "Registration logic, password encoding, duplicate-email validation")
        Component(taskService, "Task Service", "Spring Service", "Task CRUD logic, always scoped to the authenticated user")

        Component(userRepository, "User Repository", "Spring Data JPA", "Persists and queries User entities")
        Component(taskRepository, "Task Repository", "Spring Data JPA", "Persists and queries Task entities, scoped by user id")

        Component(jwtFilter, "JWT Filter", "Servlet Filter", "Validates the Bearer token on every request and populates the security context")
        Component(jwtService, "JWT Service", "Spring Service", "Issues and validates JWT tokens")
        Component(userDetailsService, "User Details Service", "Spring Security", "Loads a user by email for authentication")

        Component(exceptionHandler, "Global Exception Handler", "@RestControllerAdvice", "Converts all exceptions into a standardized JSON error response")
    }

    Rel(client, jwtFilter, "Every request passes through", "Servlet Filter Chain")
    Rel(client, authController, "Uses", "HTTPS/JSON")
    Rel(client, taskController, "Uses", "HTTPS/JSON")

    Rel(authController, userService, "Uses")
    Rel(authController, jwtService, "Uses")
    Rel(taskController, taskService, "Uses")

    Rel(jwtFilter, jwtService, "Validates tokens via")
    Rel(jwtFilter, userDetailsService, "Loads authenticated user via")
    Rel(userDetailsService, userRepository, "Uses")

    Rel(userService, userRepository, "Uses")
    Rel(taskService, taskRepository, "Uses")

    Rel(userRepository, db, "Reads from and writes to", "JDBC")
    Rel(taskRepository, db, "Reads from and writes to", "JDBC")

    Rel(authController, exceptionHandler, "Errors handled by")
    Rel(taskController, exceptionHandler, "Errors handled by")
```