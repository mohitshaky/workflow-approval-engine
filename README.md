# WorkFlow — Configurable Approval & Workflow Backend

[![CI](https://github.com/mohitshaky/workflow-approval-engine/actions/workflows/ci.yml/badge.svg)](https://github.com/mohitshaky/workflow-approval-engine/actions/workflows/ci.yml)

![Java](https://img.shields.io/badge/Java-21-orange?logo=java) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-blue?logo=postgresql) ![Redis](https://img.shields.io/badge/Redis-red?logo=redis) ![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker) ![License](https://img.shields.io/badge/license-MIT-brightgreen)

> A production-ready, configurable workflow & approval backend built with **Spring Boot 3**, **PostgreSQL**, **Redis**, and **OAuth2**. Supports multi-step approval processes, email notifications, Docker & Kubernetes deployment.

---

## Features

- Configurable multi-step workflows — define approval chains dynamically
- OAuth2 / Spring Security — secure API with resource server & JWT
- Email notifications — notify users at each workflow step via Spring Mail
- Redis caching — fast state lookups and session management
- PostgreSQL + Flyway — versioned schema migrations, production-safe
- Swagger / OpenAPI — auto-generated API docs at /swagger-ui/index.html
- Docker Compose — one-command local setup
- Kubernetes — K8s manifests included for cloud deployment
- Testcontainers — integration tests with real DB instances

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + OAuth2 Resource Server |
| Database | PostgreSQL + Flyway migrations |
| Cache | Redis |
| Notifications | Spring Mail (SMTP) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Mapping | MapStruct |
| Observability | Micrometer + Zipkin tracing |
| Testing | JUnit 5 + Testcontainers |
| Deployment | Docker Compose + Kubernetes |

---

## Quick Start

```bash
git clone https://github.com/mohitshaky/WorkFlow.git
cd WorkFlow
docker compose up --build
```

- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **PostgreSQL:** localhost:5432 (db: workflow, user: wf, pass: wf)

---

## API Endpoints

### Start a Workflow
```http
POST /api/workflows/start
{ "workflowId": 1, "initiator": "alice" }
```

### Complete a Task
```http
POST /api/workflows/tasks/complete
{ "taskId": 1, "user": "alice" }
```

Full docs at /swagger-ui/index.html when running locally.

---

## Project Structure

```
WorkFlow/
├── workflow-backend-service/   # Spring Boot app
│   ├── src/main/java/          # Source code
│   └── src/test/               # Integration tests
├── k8s/                        # Kubernetes manifests
├── docker-compose.yml          # Local dev
└── .env.example                # Config template
```

---

## Running Tests

```bash
./mvnw test
```

---

## License

MIT — Built by [Mohit](https://www.linkedin.com/in/mohit-shakya-9ab944110/) | Java Backend Developer with 6+ years experience