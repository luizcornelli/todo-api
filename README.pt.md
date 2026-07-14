# todo-api

Gerenciador de tarefas (To-Do API)

API REST para gerenciamento de tarefas por usuário. Cada usuário se cadastra e faz login (autenticação via JWT) para criar, listar, atualizar e excluir suas próprias tarefas, com filtros por status e prioridade — nunca enxergando as tarefas de outros usuários. Documentação interativa disponível via Swagger.

![alt text](image-1.png)

## Modelo de Dados

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