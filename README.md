# OFD Restaurant

Standalone **restaurant-facing** application for the FoodAI platform: onboarding, dashboard, menu management, orders, promotions, campaigns, user segments, reviews, ratings, support tickets, and AI suggestions. This repo contains backend microservices, an API gateway, and a React frontend—scoped to restaurant operations only (consumer checkout, cart, loyalty, and saga flows are not included).

---

## Features

- **Restaurant onboarding** — Multi-step flow with save/resume; draft persisted per step; on completion creates restaurant, owner user (via user-service), and sends verification email/SMS (stub).
- **Restaurant dashboard** — Overview and navigation to menu, orders, promotions.
- **Menu management** — Categories, items, pricing, availability.
- **Orders** — Restaurant-facing APIs: view orders, update status, cancel, assign rider.
- **Promotions** — Promotions, coupons, campaigns, customer segments.
- **API Gateway** — Single entry (orchestration-service) for routing, auth forwarding, rate limiting.

---

## Repository structure

```
ofd-restaurant/
├── README.md                 # This file
├── run-all.sh                # Top-level script: run full stack (Docker or local)
├── docker-compose.yml        # Full stack: infra + backend + frontend
├── backend/                  # Java/Spring Boot microservices
│   ├── README.md             # Backend architecture, services, API details
│   ├── DOMAIN_MODEL.md       # DDD context and aggregates
│   ├── QUALITY_STANDARDS.md
│   ├── SERVICE_TEMPLATE_SPEC.md
│   ├── orchestration-service/   # API Gateway (8085)
│   ├── user-service/            # Users, auth, profiles (8083)
│   ├── restaurant-service/      # Restaurants, onboarding, outlets (8081)
│   ├── menu-service/           # Menu, categories, items (8082)
│   ├── order-service/          # Orders, status, rider assign (8084)
│   └── promotion-service/       # Promotions, coupons, campaigns (8089)
└── ofd-restaurant-app/       # React + Vite frontend
    ├── README.md
    ├── Dockerfile
    ├── nginx.conf
    └── src/
```

---

## Prerequisites

- **Docker (recommended):** Docker and Docker Compose to run the full stack.
- **Local dev (optional):** Java 21, Maven 3.8+, Node 20+, npm. Used when running `./run-all.sh local`.

---

## Quick start (Docker)

From the `ofd-restaurant` directory:

```bash
# Run everything (foreground; logs in terminal)
./run-all.sh docker

# Or run in background
./run-all.sh docker-bg

# Stop the stack
./run-all.sh stop
```

**First run:** Building images can take several minutes. Subsequent starts are faster.

| What              | URL / Port |
|-------------------|------------|
| **Frontend**      | http://localhost:3000 |
| **API Gateway**   | http://localhost:8085 |
| Gateway API base | http://localhost:8085/api/v1 |

---

## Run options (`run-all.sh`)

| Command | Description |
|--------|-------------|
| `./run-all.sh docker` | Full stack in Docker, foreground (Ctrl+C to stop). |
| `./run-all.sh docker-bg` | Full stack in Docker, detached (`docker compose up -d`). |
| `./run-all.sh local` | Infra only in Docker (MongoDB, Postgres, Redis, Kafka); backend via Maven and frontend via `npm run dev`. Ctrl+C stops backend and frontend. |
| `./run-all.sh stop` | Stops Docker stack (`docker compose down`). |

---

## Ports reference

| Service | Port | Notes |
|---------|------|--------|
| Frontend | 3000 | In Docker, nginx serves the built app. |
| API Gateway (orchestration) | 8085 | Single entry for frontend API calls. |
| user-service | 8083 | User CRUD, auth; called by restaurant-service for owner creation. |
| restaurant-service | 8081 | Restaurants, onboarding (including draft save/resume). |
| menu-service | 8082 | Menu, categories, items. |
| order-service | 8084 | Restaurant-facing order APIs. |
| promotion-service | 8089 | Promotions, coupons, campaigns, segments. |
| MongoDB | 27017 | Used by user, restaurant, menu, order, promotion. |
| PostgreSQL | 5432 | Used by order-service (DB: `order_db`). |
| Redis | 6379 | Cache / session. |
| Kafka | 9092 | Event bus (inter-service). |

---

## Infrastructure (Docker)

`docker-compose.yml` defines:

- **mongodb** — MongoDB 7
- **postgres** — PostgreSQL 16, database `order_db`, user/password `postgres`/`postgres`
- **redis** — Redis 7
- **zookeeper** + **kafka** — Confluent Kafka 7.5 (for event-driven features)

Backend services use healthchecks and `depends_on` so the gateway starts after the other services and infra.

---

## Backend services (summary)

- **orchestration-service** — API Gateway only: routes `/api/v1/user/**`, `/api/v1/restaurant/**`, `/api/v1/menu/**`, `/api/v1/order/**`, `/api/v1/promotion/**` to the corresponding service. No Saga/checkout in this repo.
- **user-service** — User accounts, profiles, auth. No loyalty. Invoked by restaurant-service to create owner after onboarding.
- **restaurant-service** — Restaurants, outlets, onboarding (draft create/get/save/submit), post-onboarding owner creation and notification stub (email/SMS).
- **menu-service** — Menu categories and items, pricing, availability.
- **order-service** — Restaurant-facing order APIs only (get order, by number, by restaurant, update status, cancel, assign rider). No cart/payment APIs.
- **promotion-service** — Promotions, coupons, campaigns, customer segments.

Each service has its own `Dockerfile` (multi-stage, Java 21) and optional `README.md` and `CURSOR_PROMPT.md` under `backend/<service>/`.

---

## Frontend

- **Stack:** React, Vite, Redux Toolkit, TanStack Query, Shadcn UI, Tailwind.
- **App entry:** `ofd-restaurant-app/`
- **Key routes:** `/` (dashboard), `/restaurant-onboarding`, `/restaurant/order/:orderId`, `/restaurant/:restaurantId`.

**Environment (build-time):**

- `VITE_API_GATEWAY_URL` — e.g. `http://localhost:8085/api/v1` (used when serving from port 3000).
- `VITE_WS_HOST`, `VITE_ORDER_WS_URL` — Optional; for WebSocket endpoints.

In Docker, the frontend image is built with these set so the browser talks to the gateway on the host. For a different host/port, override the build args in `docker-compose.yml` under `frontend.build.args` and rebuild.

---

## Local development (infra in Docker, app on host)

```bash
./run-all.sh local
```

1. Starts **only** infrastructure: MongoDB, Postgres, Redis, Zookeeper, Kafka.
2. Starts each backend service with Maven (`spring-boot:run`, profile `dev`) in the background.
3. Starts the frontend with `npm run dev` in the foreground.

Backend uses `application-dev.yml` and connects to `localhost` for DBs/Redis/Kafka (ports 27017, 5432, 6379, 9092). Use Ctrl+C to stop; the script will terminate the backend processes.

**Manual alternative:** Start infra with `docker compose up -d mongodb postgres redis zookeeper kafka`, then run each backend from `backend/` with `mvn -f <service>/pom.xml spring-boot:run -Dspring.profiles.active=dev`, and run the frontend with `cd ofd-restaurant-app && npm run dev`.

---

## Environment variables (Docker)

Backend services receive connection settings via `docker-compose` (e.g. `SPRING_DATA_MONGODB_URI`, `POSTGRES_HOST`, `KAFKA_BOOTSTRAP_SERVERS`). No extra `.env` is required for the default setup. To override, use a `.env` file next to `docker-compose.yml` or set env in the compose file.

---

## Documentation

| Document | Description |
|----------|-------------|
| [backend/README.md](backend/README.md) | Backend architecture, gateway flow, service list, API patterns, quality standards. |
| [backend/DOMAIN_MODEL.md](backend/DOMAIN_MODEL.md) | DDD contexts and aggregates. |
| [backend/QUALITY_STANDARDS.md](backend/QUALITY_STANDARDS.md) | Coding and testing standards. |
| [backend/SERVICE_TEMPLATE_SPEC.md](backend/SERVICE_TEMPLATE_SPEC.md) | Service layout and template. |
| [ofd-restaurant-app/README.md](ofd-restaurant-app/README.md) | Frontend setup, routes, and run instructions. |

Per-service READMEs and `CURSOR_PROMPT.md` live under `backend/<service>/`.

---

## Tech stack

| Layer | Technologies |
|-------|----------------|
| Frontend | React 18, Vite, Redux Toolkit, TanStack Query, Shadcn UI, Tailwind CSS |
| API Gateway | Spring Boot 3.2, Java 21 |
| Backend | Spring Boot 3.2, Java 21, Maven |
| Data | MongoDB, PostgreSQL (order-service), Redis |
| Messaging | Apache Kafka |
| Runtime | Docker, Docker Compose; frontend served by nginx in container |
