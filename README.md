# Boardgames API

A backend-only CRUD REST API for managing a personal board game collection, built in Java using the Spring Boot stack and PostgreSQL.

Each user registers, logs in, and manages **their own** board games. Nobody can see or modify another user's records.

## Data models
The main resource is a **BoardGame**: a game in a user's collection, with its player count, play time, complexity and purchase date.

### `users`

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary key, auto-generated |
| username | VARCHAR(50) | Unique, not null |
| email | VARCHAR(100) | Unique, not null |
| password | VARCHAR(255) | Not null, BCrypt hash |
| role | VARCHAR(20) | Not null, default `USER` |
| created_at | TIMESTAMP | Set automatically |

### `board_games`

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary key, auto-generated |
| title | VARCHAR(100) | Not null |
| publisher | VARCHAR(100) | Optional |
| min_players | INT | Not null |
| max_players | INT | Not null |
| play_time_minutes | INT | Not null |
| complexity | VARCHAR(20) | Not null, enum: `LIGHT`, `MEDIUM`, `HEAVY`, `EXTREME` |
| purchase_date | DATE | Optional |
| user_id | BIGINT | Foreign key to `users`, not null |
| created_at | TIMESTAMP | Set automatically |
| updated_at | TIMESTAMP | Updated automatically |

### Validation rules (board game request)

| Field | Rule |
|---|---|
| title | Not blank, max 100 characters |
| publisher | Max 100 characters |
| minPlayers / maxPlayers | Required, 1 to 20 |
| playTimeMinutes | Required, 5 to 600 |
| complexity | Required, one of the enum values |
| purchaseDate | Not in the future |
| (whole request) | `maxPlayers` must be at least `minPlayers` |

## Setup

### 1. Environment variables

The application reads its secrets from environment variables. Nothing secret is stored in the repository.

| Variable | Description |
|---|---|
| `DB_USER` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | **Base64-encoded** signing key, at least 32 bytes before encoding |

Generate a suitable JWT secret:

```bash
openssl rand -base64 64
```

Your `.env` should look like this:

```
DB_USER=boardgames
DB_PASSWORD=choose_a_password
JWT_SECRET=<output of the openssl command>
```

The token lifetime can be configured at `src/main/resources/application.properties`:

### 2. Database

Docker Compose reads `.env` automatically and starts PostgreSQL with a database called `boardgamedb`:

```bash
docker compose up -d
```

The data is stored in a Docker volume (`pgdata`) and survives restarts. To wipe the database completely:

```bash
docker compose down -v
```

### 3. Run the application

Spring Boot does not read `.env` by itself, so the variables must be present in the environment that starts the app.

The API is hosted at `http://localhost:8080`.

## API reference

Base path: `/api`. All board game endpoints require the header `Authorization: Bearer <token>`.

### Auth (public)

| Method | Endpoint | Description | Success |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | 201 Created |
| POST | `/api/auth/login` | Log in (username **or** email) and receive a JWT | 200 OK |

Register request:

```json
{ "username": "john", "email": "john@example.com", "password": "StrongPass123" }
```

Login request and response:

```json
{ "username": "john", "password": "StrongPass123" }
```

```json
{ "token": "<jwt>", "type": "Bearer", "expiresIn": 3600 }
```

### Board games (authenticated)

| Method | Endpoint | Description | Success |
|---|---|---|---|
| POST | `/api/boardgames` | Create a board game | 201 Created |
| GET | `/api/boardgames` | List your own games (paginated, filterable) | 200 OK |
| GET | `/api/boardgames/{id}` | Get one game | 200 OK |
| PUT | `/api/boardgames/{id}` | Replace a game (full update) | 200 OK |
| DELETE | `/api/boardgames/{id}` | Delete a game | 204 No Content |

Request body (create and update):

```json
{
  "title": "Catan",
  "publisher": "Kosmos",
  "minPlayers": 3,
  "maxPlayers": 4,
  "playTimeMinutes": 90,
  "complexity": "MEDIUM",
  "purchaseDate": "2024-03-15"
}
```

Responses additionally include `id`, `createdAt` and `updatedAt`.

**List query parameters:**

| Parameter | Default | Description |
|---|---|---|
| `page` | 0 | Zero-based page number |
| `size` | 10 | Page size (maximum 100) |
| `title` | none | Case-insensitive "contains" search |
| `complexity` | none | Exact match: `LIGHT`, `MEDIUM`, `HEAVY`, `EXTREME` |

Filters can be combined, for example `GET /api/boardgames?title=ca&complexity=LIGHT&page=0&size=5`.

List response shape:

```json
{
  "content": [ { "id": 1, "title": "Catan", "...": "..." } ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### Ownership

Every board game belongs to the user who created it. Requests for another user's game return **404 Not Found**, exactly as if the game did not exist, so the API never reveals which ids belong to someone else.

### Errors

All errors use one JSON format:

```json
{
  "timestamp": "2026-11-23T17:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Board game with id 5 not found",
  "path": "/api/boardgames/5"
}
```

Validation errors (400) additionally include a `fieldErrors` map with one message per invalid field.

| Code | Usage |
|---|---|
| 200 | Successful read or update |
| 201 | Resource created |
| 204 | Successful delete |
| 400 | Validation error or malformed request |
| 401 | Missing, invalid or expired token; bad credentials |
| 404 | Resource not found (or owned by someone else) |
| 409 | Duplicate username or email |
| 500 | Unexpected server error |
