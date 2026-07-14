# todo-api

Task Manager (To-Do API)

REST API for managing tasks per user. Each user registers and logs in (JWT authentication) to create, list, update, and delete their own tasks, with filtering by status and priority — never seeing other users' tasks. Interactive documentation available via Swagger.

![alt text](image.png)

## Data Model

```mermaid
classDiagram
    class User {
        -Long id
        -String name
        -String email
        -String password
        -List~Task~ tasks
    }

    class Task {
        -Long id
        -String title
        -String description
        -Priority priority
        -Status status
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -User user
    }

    class Priority {
        <<enumeration>>
        LOW
        MEDIUM
        HIGH
    }

    class Status {
        <<enumeration>>
        PENDING
        IN_PROGRESS
        DONE
    }

    User "1" *-- "0..*" Task : owns
    Task --> Priority
    Task --> Status
```