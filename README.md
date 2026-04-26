# Neon Ark Creature Management System

Course: COSC-4301-001 Modern Programming  
Project: Capstone  
Author: Nasim Bayati

Acknowledgment: This README was prepared with editing assistance from OpenAI Codex.

This project is a CLI-first full stack system for Neon Ark. The backend is a Spring Boot REST API with PostgreSQL persistence and Flyway migrations. The client is a vanilla Java command-line application that communicates with the backend only through HTTP.

## Project Structure

```text
neon-ark-capstone/
├── backend/          Spring Boot REST API
├── cli/              Java CLI client
├── docker-compose.yml
├── gradlew.bat
└── settings.gradle
```

## Required Routes

| CLI Option | HTTP Request |
|---|---|
| 1. List all creatures | `GET /api/creatures` (or `GET /api/creatures?includeRemoved=true`) |
| 2. View creature by ID | `GET /api/creatures/{id}` |
| 3. Register new creature | `POST /api/creatures` |
| 4. Rename creature | `PUT /api/creatures/{id}/name` |
| 5. Remove creature | `DELETE /api/creatures/{id}` |
| 6. View creature observations/notes | `GET /api/creatures/{id}/observations` |
| 7. Find creatures by feeding time | `GET /api/feedings?time={HH:MM}` |
| 8. View all system users | `GET /api/admin/users` |
| 0. Exit | No API request |

Option 4, option 5, and option 0 require explicit user confirmation. Option 8 uses the `X-Role` request header; use `ADMIN` for an authorized request.

## How to Run

Use JDK 17 or newer. On this machine the default `java` command points to Java 8, so use a newer `JAVA_HOME` before running Gradle:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-23'
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
```

Start PostgreSQL:

```powershell
docker compose up -d
```

Start the backend:

```powershell
.\gradlew.bat :backend:bootRun
```

In a second terminal, start the CLI:

```powershell
.\gradlew.bat :cli:run
```

If Gradle progress text appears in the CLI screen, run the same command with an explicit plain console:

```powershell
.\gradlew.bat --console=plain :cli:run
```

Run tests:

```powershell
.\gradlew.bat test
```

## Notes

- Docker Desktop must be running before `docker compose up -d` works.
- The capstone PostgreSQL container uses local port `5438` to avoid conflicts with earlier course projects.
- Flyway creates and seeds the database automatically when the backend starts.
- `GET /api/creatures` hides removed creatures by default. `GET /api/creatures?includeRemoved=true` includes historical soft-deleted records for review.
- Deleting a creature performs a soft delete by changing its status to `REMOVED`.

## Tested Workflows

The following workflows were manually verified against the running backend and CLI:

- List creatures with default filtering
- List creatures including removed records
- View a creature by ID
- View creature observations
- Find creatures by feeding time
- View system users with and without the required `X-Role: ADMIN` header
- Register a new creature
- Rename a creature with confirmation
- Remove a creature with confirmation

Backend route spot checks were also verified directly with PowerShell using:

- `GET /api/creatures`
- `GET /api/creatures?includeRemoved=true`
- `GET /api/creatures/{id}`
- `GET /api/feedings?time=08:00`
- `GET /api/admin/users` with `X-Role: ADMIN`
