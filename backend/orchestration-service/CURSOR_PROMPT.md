# Orchestration Service - Cursor AI Prompt

## Service Overview

In the **restaurant app backend** (ofd-restaurant/backend), the Orchestration Service is the **API Gateway only**. It routes frontend requests to user, restaurant, menu, order, and promotion services. Saga and Checkout have been removed; there is no distributed transaction engine in this repo.

## Key Responsibilities

1. **Request routing**
   - Route by path prefix: `/api/v1/user/**`, `/api/v1/restaurant/**`, `/api/v1/menu/**`, `/api/v1/order/**`, `/api/v1/promotion/**`
   - Forward method, headers, body, and query params to the correct backend service
   - Return the downstream response to the client

2. **Gateway behaviour**
   - Pass through JWT and other headers
   - Configurable timeouts per service (WebClient)
   - Logging (e.g. request ID, route)

## Code Generation Guidelines

When generating code for this service:

### Architecture
- **GatewayController**: Map path prefixes to `GatewayService.routeRequest(serviceType, method, path, headers, body, queryParams)`.
- **GatewayService**: Use WebClient per service (from GatewayConfig); build URI from path (e.g. `/api/v1` + remainder after prefix); forward and return response.
- **GatewayConfig**: Define WebClient beans with base URLs for user, restaurant, menu, order, promotion services.

### Do not add
- Saga controllers, Saga engine, MongoDB, Kafka (removed in this repo).

### Quality Standards
- Follow `backend/QUALITY_STANDARDS.md` and `backend/README.md`
- All tests must pass
- Use structured logging

## Validation

```bash
cd ofd-restaurant/backend
./mvnw.sh service orchestration-service
```

## References

- `backend/README.md` – Overall backend and gateway routes
- `backend/DOMAIN_MODEL.md` – Orchestration context (API Gateway only)
