# 🔐 Secure Auth API

A secure authentication and authorization API built with **Spring Boot** and **Spring Security**, focused on **stateless JWT authentication**, **role-based access control (RBAC)**, and **production-oriented security design**.

> This project is designed as a **reference backend authentication service**, demonstrating real-world security patterns, clean architecture, and explicit design decisions — not tutorial shortcuts.

---

## 🚧 Project Status

### Active / Stable (v0.2.0)

Core authentication, authorization, infrastructure, and observability features are fully implemented.

---

## 🎯 Project Goals

- Provide stateless authentication using JWT
- Implement database-backed RBAC (USER / ADMIN)
- Demonstrate secure token lifecycle management
- Serve as a **portfolio-grade backend security project**
- Reflect **real-world Spring Security usage**, not simplified demos

---

## ✨ Features

### ✅ Implemented

**Authentication & Authorization**
- Stateless JWT authentication
- Secure password hashing with **BCrypt**
- Role-based access control (USER / ADMIN)
- Database-backed roles (User ↔ Role many-to-many)
- JWT propagation of roles via claims
- Refresh token with **rotation**
- Refresh token persistence with **SHA-256 hashing**
- Automatic refresh token revocation on reuse
- **Token reuse detection** — revokes all user sessions on suspicious refresh attempt
- Unique token generation using JWT `jti`
- Admin-protected endpoints using `@PreAuthorize`
- Custom JWT authentication filter

**Security Hardening**
- Rate limiting on `/login` — **10 requests/minute per IP** (Bucket4j)
- Structured JSON responses for `401 Unauthorized` and `403 Forbidden`
- `429 Too Many Requests` with `Retry-After` header on rate limit breach

**Audit Logging**
- Async audit trail for: `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `LOGOUT`, `TOKEN_REFRESHED`, `TOKEN_REUSE_DETECTED`, `USER_REGISTERED`, `ROLE_GRANTED`, `ROLE_REVOKED`
- IP address captured per event (supports `X-Forwarded-For` proxy headers)
- Events published via Spring's `ApplicationEventPublisher` (decoupled from business logic)

**Infrastructure**
- Flyway migrations for schema versioning
- PostgreSQL for production, H2 in-memory for tests
- Docker multi-stage build (non-root user, layer-optimized)
- Docker Compose for local development (app + PostgreSQL with healthchecks)
- Automated refresh token cleanup (scheduled daily at 03:00)

**Testing & CI**
- Integration tests with `@SpringBootTest` and H2 in-memory
- Full auth flow test: register → login → access → refresh → logout
- Token reuse detection test
- Admin authorization tests with `@WithMockUser`
- GitHub Actions CI: build, test, OWASP dependency scan, Docker build

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6 |
| JWT | JJWT 0.12.6 |
| Rate Limiting | Bucket4j 8.x (in-memory) |
| Persistence | JPA / Hibernate + Flyway |
| Database (dev/test) | H2 in-memory |
| Database (production) | PostgreSQL 16 |
| Build | Maven |
| Container | Docker (multi-stage, eclipse-temurin:17-alpine) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| CI | GitHub Actions |

---

## 🗂️ Project Structure

```
secure-auth-api/
├── .github/
│   └── workflows/
│       └── ci.yml                  # CI pipeline
├── src/
│   ├── main/
│   │   ├── java/com/davidmoura/secureauth/
│   │   │   ├── config/             # AsyncConfig, DataSeeder, OpenApiConfig, PasswordConfig
│   │   │   ├── controller/         # AuthController, AdminController, UserController, MeController, HealthController
│   │   │   ├── domain/             # User, Role, RefreshToken, AuditEvent, AuditEventType
│   │   │   ├── dto/                # Request/Response records
│   │   │   ├── exception/          # ApiError, GlobalExceptionHandler
│   │   │   ├── repository/         # UserRepository, RoleRepository, RefreshTokenRepository, AuditEventRepository
│   │   │   ├── security/           # JWT filter, TokenService, SecurityConfig, rate limiting, handlers
│   │   │   ├── service/            # AuthService, UserService, AuditService, TokenCleanupService
│   │   │   └── SecureAuthApiApplication.java
│   │   └── resources/
│   │       ├── db/migration/       # V1__init.sql, V2__create_refresh_tokens.sql, V3__create_audit_log.sql
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       ├── java/com/davidmoura/secureauth/
│       │   ├── api/                # AuthControllerIT, AdminAuthorizationIT
│       │   ├── config/             # TestSecurityConfig
│       │   └── AuthFlowIT.java
│       └── resources/
│           └── application-test.yml
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### Option A — Docker Compose (recommended)

```bash
# 1. Clone the repository
git clone https://github.com/DavidHMoura/secure-auth-api.git
cd secure-auth-api

# 2. Configure environment variables
cp .env.example .env
# Edit .env and set JWT_SECRET (min. 64 chars)

# 3. Start the stack (PostgreSQL + app)
docker compose up --build

# App available at http://localhost:8080
# Swagger UI at  http://localhost:8080/swagger-ui.html
```

### Option B — Local (Maven + external PostgreSQL)

```bash
# 1. Set environment variables
export SPRING_PROFILES_ACTIVE=dev
export JWT_SECRET=your-secret-with-at-least-64-characters-here
export DB_URL=jdbc:postgresql://localhost:5432/secureauth
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# 2. Run the application
./mvnw spring-boot:run
```

### Running tests

```bash
./mvnw test
```

---

## 🔐 Authentication Flow

```
1. POST /api/v1/users          → Register (public)
2. POST /api/v1/auth/login     → Returns accessToken + refreshToken
3. GET  /api/v1/me             → Authenticated endpoint (Bearer token)
4. POST /api/v1/auth/refresh   → New access + refresh token (old revoked)
5. POST /api/v1/auth/logout    → Refresh token revoked
```

---

## 📡 API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/users` | Public | Register user |
| `POST` | `/api/v1/auth/login` | Public | Login (rate limited) |
| `POST` | `/api/v1/auth/refresh` | Public | Refresh tokens |
| `POST` | `/api/v1/auth/logout` | Public | Logout |
| `GET` | `/api/v1/me` | Bearer | Current user info |
| `GET` | `/api/v1/admin/ping` | ADMIN | Admin health check |
| `POST` | `/api/v1/admin/grant` | ADMIN | Grant role to user |
| `POST` | `/api/v1/admin/revoke` | ADMIN | Revoke role from user |
| `GET` | `/health` | Public | Application health |

---

## 🔐 Security Design Decisions

### Stateless Authentication
JWT is used instead of server-side sessions.

**Why:** Horizontal scalability without shared session state. Clear separation between authentication and application state.

### Database-backed RBAC
Roles are stored in the database and associated via a many-to-many relationship.

**Why:** Avoids hardcoded roles. Enables dynamic role assignment at runtime.

### Refresh Token Rotation
Refresh tokens are persisted as SHA-256 hashes, revoked after use, and reissued with every refresh.

**Why:** Prevents replay attacks. Enables explicit session invalidation. The old token is immediately invalidated — any reuse is detectable.

### Token Reuse Detection
When a revoked refresh token is presented, **all active sessions for that user are immediately revoked**.

**Why:** Token reuse strongly suggests theft (e.g., the attacker has the original token and is trying to refresh after the legitimate user already did). Revoking all sessions forces a full re-authentication, limiting the attack window.

### Rate Limiting on Login
The `/api/v1/auth/login` endpoint is limited to **10 requests per minute per IP** using Bucket4j (in-memory, greedy refill).

**Why:** Mitigates brute-force and credential stuffing attacks without requiring external infrastructure (Redis). For multi-instance deployments, migrate to `bucket4j-redis`.

### Async Audit Logging
Authentication and authorization events are published via Spring's `ApplicationEventPublisher` and persisted asynchronously.

**Why:** Decouples audit concerns from business logic. A failure in audit persistence does not fail the auth request. Logged data (event type, user ID, IP, timestamp) supports incident response and compliance.

### Explicit Token Uniqueness (`jti`)
All tokens include a unique `jti` (JWT ID) claim.

**Why:** Prevents deterministic token generation. Ensures safe refresh rotation and improves traceability in audit logs.

### Method-level Authorization (`@PreAuthorize`)
Authorization is enforced at the method level, not only at the filter chain.

**Why:** Keeps rules close to business logic. Allows fine-grained, per-method access control that survives routing refactors.

### JSON-only Security Errors
All authentication and authorization failures return structured JSON (no redirects, no HTML).

**Why:** API-first behavior. Easy to parse by frontend clients and monitoring systems.

---

## 🧪 Test Coverage

| Test | What it validates |
|---|---|
| `AuthFlowIT` | Full flow: register → login → access protected → admin denied → refresh rotation → reuse detection → logout |
| `AuthControllerIT` | Invalid credentials return 401 |
| `AdminAuthorizationIT` | ADMIN role accesses admin endpoint; USER role is denied with 403 |

---

## 👤 Author

**David Moura**  
Software Engineering · Backend Development · Application Security

Focus areas: Java · Spring · Security · Backend Architecture · Linux

---

## 🧠 Final Notes

This project is not intended to be a full IAM solution.  
Its goal is to demonstrate **secure backend design**, **clean architecture**, and **explicit engineering decisions** in a realistic Spring Boot application — with real infrastructure, real security patterns, and production-grade code quality.