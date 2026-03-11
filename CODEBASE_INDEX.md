# OFD Restaurant – Codebase Index

Structured index for navigation and making changes. Focus: **what to change, where to change, how to change.**

**How to use this index:** When making changes, open this file and use section 8 (Where to change for common tasks) to find the right files. For architecture and flows, see sections 2–3. For existing TODOs, see section 4. Keep this file updated when adding new services, routes, or major flows.

---

## 1. Project structure

### Top-level

| Path | Description |
|------|-------------|
| `ofd-restaurant-app/` | **Frontend** – React SPA (Vite, TypeScript) |
| `backend/` | **Backend** – Java microservices (Maven, Spring Boot) |
| `run-all.sh` | Script to run full stack: Docker or local (infra in Docker + Maven/npm) |
| `docker-compose.yml` | Infra + all services (MongoDB, Postgres, Redis, Kafka, Zookeeper, backend apps, frontend) |

### Frontend (`ofd-restaurant-app/`)

| Item | Location |
|------|----------|
| Entry HTML | `index.html` → loads `/src/main.tsx` |
| App root & routing | `src/App.tsx` |
| Main entry | `src/main.tsx` |
| Config | `vite.config.ts`, `tsconfig.json`, `tailwind.config.ts` |
| Package manifest | `package.json` |

### Backend (`backend/`)

| Item | Location |
|------|----------|
| Parent POM | `pom.xml` (modules, BOM, plugins) |
| Services (present in repo) | `user-service/`, `restaurant-service/`, `menu-service/`, `order-service/`, `promotion-service/`, `orchestration-service/` |
| Docs | `README.md`, `DOMAIN_MODEL.md`, `QUALITY_STANDARDS.md`, `SERVICE_TEMPLATE_SPEC.md` |

---

## 2. Architecture

### Layers

- **Frontend**: React 18, Redux Toolkit, React Router, TanStack Query, Axios. Single SPA; all API calls go through **orchestration-service** (API gateway).
- **API gateway**: `orchestration-service` (port **8085**). Proxies by path prefix to the right service. No saga/checkout in current "restaurant-only" backend.
- **Backend services**: Java 21, Spring Boot 3.2.1, DDD-style. Each service has its own port and (where applicable) DB.
- **Databases**: MongoDB (user, restaurant, menu, promotion), PostgreSQL (order-service), Redis (caching/session where used).
- **Messaging**: Kafka (e.g. events; optional per service).

### How they connect

1. **Browser** → `VITE_API_GATEWAY_URL` (default `http://localhost:8085/api/v1`) → **Orchestration (8085)**.
2. **Gateway** routes by prefix to:
   - `/user/**` → **user-service (8083)**
   - `/restaurant/**` → **restaurant-service (8081)**
   - `/menu/**` → **menu-service (8082)**
   - `/order/**` → **order-service (8084)**
   - `/promotion/**` → **promotion-service (8089)**
3. Frontend uses a single Axios instance (in `api.config.ts`) with base URL = gateway; path = e.g. `/user/...`, `/restaurant/...`. Gateway strips prefix and forwards to the corresponding service.

### Frameworks & languages

| Layer | Stack |
|-------|--------|
| Frontend | React 18, TypeScript, Vite 5, Tailwind CSS, Radix UI, Redux Toolkit, React Query, Axios, React Router 6 |
| Gateway | Spring Boot 3.2, WebFlux (Mono), forwards to other services |
| Services | Spring Boot 3.2, Spring Web MVC, JPA (Postgres where used), Spring Data MongoDB, Redis, Kafka (optional) |

---

## 3. Main flows and where they live

### Auth flow

- **Frontend**: `src/store/slices/authSlice.ts` (login/register/logout thunks), `src/hooks/useAuth.ts`, `src/services/user.service.ts` (calls `userApi.post('/auth/register'|'/auth/login'|'/auth/logout'|'/auth/refresh'|...)`).
- **Token handling**: `src/services/api.config.ts` – `tokenManager` (localStorage), Axios request interceptor (Bearer token), response interceptor (401 → refresh then retry; failure → `auth:logout`).
- **Backend**: User-service owns auth; gateway routes `/user/**` to it. Frontend expects auth at `/auth/*` under the user path (e.g. `/user/auth/login` at gateway → user-service).

### Restaurant onboarding flow

- **Frontend**: Page `src/pages/RestaurantOnboarding.tsx`; route `/restaurant-onboarding`. Uses `restaurantService` and likely `authSlice`/user state.
- **Backend**: `restaurant-service`: `RestaurantController` (e.g. `POST /onboarding`), `OnboardingDraftController` (`/api/v1/restaurants/onboarding/draft`, submit, etc.).

### Restaurant & menu (browse / manage)

- **Frontend**: `src/pages/Restaurant.tsx` (landing/list), `src/pages/RestaurantMenu.tsx` (menu for a restaurant); routes `/`, `/restaurant`, `/restaurant/:restaurantId`. Components: `MenuItemCard`, `Navbar`, cart (e.g. `CartDrawer` / `CartDrawerRedux`).
- **Backend**: `restaurant-service`: `RestaurantController` (CRUD, search, outlets). `menu-service`: `MenuItemController`, `MenuCategoryController` (e.g. `/api/v1/menu-items`, `/api/v1/menu-categories`).

### Order flow (restaurant-facing)

- **Frontend**: `src/pages/OrderDetails.tsx` (route `/restaurant/order/:orderId`). `src/store/slices/orderSlice.ts`, `src/services/order.service.ts`, `src/hooks/useOrders.ts`. Cart: `cartSlice`, `orderService.getCart()`, `addToCart`, etc. Checkout/saga calls (e.g. `initiateCheckout`) go to orchestration; in the current "restaurant-only" backend, saga/checkout may be removed or stubbed.
- **Backend**: `order-service`: `OrderController` – get by ID, by number, by restaurant; cancel; assign rider. Cart/Payment controllers removed in restaurant-only version (see backend README).

### Promotions / campaigns

- **Frontend**: `src/store/slices/promotionSlice.ts`, `src/services/promotion.service.ts`.
- **Backend**: `promotion-service`: `PromotionController`, `CouponController`, `CampaignController`, `CustomerSegmentController` under `/api/v1/promotions`, `/api/v1/coupons`, etc.

---

## 4. TODOs / FIXMEs

| File | Line (approx) | Comment |
|------|----------------|---------|
| `backend/restaurant-service/.../SearchMapper.java` | 110 | TODO: Implement actual distance calculation using lat/long |
| `backend/restaurant-service/.../SearchMapper.java` | 127 | TODO: Implement delivery fee calculation based on outlet configuration |
| `backend/restaurant-service/.../SearchMapper.java` | 188, 196 | TODO: Implement actual tag generation logic (offers, ratings, etc.) |
| `backend/order-service/.../PaymentService.java` | 44 | TODO: In production, inject Razorpay client |
| `backend/order-service/.../PaymentService.java` | 88 | TODO: Get actual order number |
| `backend/order-service/.../PaymentService.java` | 270 | TODO: Implement actual Razorpay signature verification |
| `backend/order-service/.../PaymentService.java` | 283 | TODO: Implement actual Razorpay refund |
| `backend/order-service/.../OrderService.java` | 216 | TODO: Send notifications, publish events |
| `backend/order-service/.../OrderService.java` | 243 | TODO: Publish order.confirmed event |
| `backend/order-service/.../OrderService.java` | 269 | TODO: Trigger refund if paid, publish order.cancelled event |
| `backend/order-service/.../CartService.java` | 154 | TODO: Validate coupon with promotion service |
| `backend/order-service/.../CartService.java` | 254 | TODO: Integrate with promotion service |

---

## 5. Key patterns

### State management (frontend)

- **Redux Toolkit** with slices: `auth`, `restaurant`, `menu`, `cart`, `order`, `promotion`, `ui` (`src/store/slices/*.ts`). Store: `src/store/index.ts`.
- **Typed hooks**: `useAppDispatch`, `useAppSelector` from `src/store/hooks.ts`.
- **Async**: `createAsyncThunk` in slices; services in `src/services/*.service.ts` called from thunks.

### API calls (frontend)

- **Single Axios instance** in `src/services/api.config.ts`; service wrappers: `userApi`, `restaurantApi`, `menuApi`, `orderApi`, `orchestrationApi`, `promotionApi`. Feature code uses `*Service` modules; types in `src/types/api.types.ts`.

### Naming

- **Components**: PascalCase; pages in `src/pages/`, shared/UI in `src/components/` (e.g. `components/ui/` for design-system primitives).
- **Routes**: In `App.tsx`; paths like `/`, `/restaurant`, `/restaurant-onboarding`, `/restaurant/order/:orderId`, `/restaurant/:restaurantId`, `*` (NotFound).
- **Services**: `*.service.ts` in `src/services/`; export from `src/services/index.ts`.
- **Slices**: `*Slice.ts` in `src/store/slices/`.

### Shared utilities & design system

- **Utils**: `src/lib/utils.ts` – `cn()` (clsx + tailwind-merge).
- **UI**: `src/components/ui/` – Radix-based primitives (Button, Card, Dialog, Input, etc.).

---

## 6. Entry points & config

### Frontend

- **Entry**: `index.html` → `src/main.tsx` → `<App />`.
- **Routing**: `App.tsx`: `BrowserRouter` → `Routes`/`Route`.
- **Env**: `VITE_API_GATEWAY_URL`, `VITE_WS_HOST`. No `.env` in repo; set in environment or `.env.local`.

### Backend

- **Entry per service**: Each service has an `*Application.java` with `@SpringBootApplication` and `main`.
- **Gateway routing**: `backend/orchestration-service/.../GatewayController.java` – `@RequestMapping("/api/v1")` and path-based routing; `GatewayService` does the forward.

---

## 7. Dependencies (summary)

### Frontend

- **Runtime**: react, react-dom, react-router-dom, @reduxjs/toolkit, react-redux, @tanstack/react-query, axios, @supabase/supabase-js, react-hook-form, zod, Radix UI, tailwind-merge, clsx, lucide-react, etc.
- **Dev**: vite, @vitejs/plugin-react-swc, typescript, eslint, tailwindcss.

### Backend

- **Runtime**: Spring Boot 3.2.1, Spring Cloud, MongoDB, PostgreSQL, Kafka, Redis, MapStruct, Lombok, SpringDoc OpenAPI.
- **Test**: JUnit 5, Mockito, Testcontainers, REST Assured, ArchUnit.
- **Quality**: JaCoCo, Spotless, Checkstyle, SpotBugs, PMD, OWASP.

---

## 8. Where to change for common tasks

| Task | Where to change |
|------|------------------|
| **Add new API endpoint** | Backend: add method in the right `*Controller` in the right service. Frontend: add method in the right `*.service.ts` calling the right `*Api`; optionally add thunk in the corresponding slice and types in `api.types.ts`. |
| **Add new page/route** | Create page in `src/pages/`, add `<Route path="..." element={<NewPage />} />` in `src/App.tsx`. Add nav link in `Navbar` or layout if needed. |
| **Change auth logic** | Frontend: `authSlice.ts`, `useAuth.ts`, `user.service.ts`, `api.config.ts` (token, refresh, logout). Backend: user-service (auth endpoints and auth service); gateway only forwards `/user/**`. |
| **Add new component** | `src/components/` or `src/components/ui/`. Use `cn()` from `@/lib/utils`. For new UI primitives, follow existing `components/ui/*` pattern. |
| **Change DB schema** | Backend: entity in owning service, migrations if present, DTOs and mappers. Frontend: `api.types.ts` and any slice mapping responses. |
| **Add new backend service** | Add module under `backend/`, add to parent `pom.xml`, implement controller. In orchestration-service: add route in `GatewayController` and `GatewayService`. Frontend: add `SERVICE_PATHS.<name>` and `<name>Api` in `api.config.ts`, then `<name>.service.ts` and optionally a slice. |
| **Change gateway routing** | `GatewayController.java`, `GatewayService` (service type and target base URL). |
| **Environment / URLs** | Frontend: `VITE_API_GATEWAY_URL`, `VITE_WS_HOST`. Backend: per-service `application*.yml` and Docker/env in `docker-compose.yml`. |
