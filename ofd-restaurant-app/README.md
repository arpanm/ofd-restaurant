# OFD Restaurant App

Standalone frontend for **restaurant** flows: onboarding, dashboard, menu, orders, promotions, campaigns, user segments, reviews, ratings, tickets, and AI suggestions.

Extracted from the main `agentic-plate` app so it can be run and deployed as its own project.

## Routes

| Path | Page |
|------|------|
| `/` | Restaurant Dashboard |
| `/restaurant` | Restaurant Dashboard |
| `/restaurant-onboarding` | Restaurant Onboarding |
| `/restaurant/order/:orderId` | Order details |
| `/restaurant/:restaurantId` | Restaurant menu (consumer view) |

## Run locally

```bash
cd ofd-restaurant-app
npm install
npm run dev
```

App runs at `http://localhost:8081` (port 8081 to avoid clashing with main app on 8080).

## Build

```bash
npm run build
npm run preview   # preview production build
```

## Backend

Configure the API gateway URL via env:

- `VITE_API_GATEWAY_URL` – e.g. `http://localhost:8085/api/v1` (orchestration service)

Same backend services as the main app: orchestration, restaurant, menu, order, promotion, user.
