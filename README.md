# Secure Auth API

### A secure authentication and authorization API built with **Spring Boot** and **Spring Security**, focused on **stateless JWT authentication**, **role-based access control (RBAC)**, and **production-oriented security design (AppSec)**.

> This project is designed as a **reference backend authentication service**, demonstrating real-world security patterns, clean architecture, and explicit design decisions — not tutorial shortcuts.

---

## Project Status

### Active / Stable (v1.0.0)

Core authentication, authorization, infrastructure, advanced account recovery, and observability features are fully implemented and documented via OpenAPI.

---

## Project Goals

- Provide stateless authentication using JWT
- Implement database-backed RBAC (USER / ADMIN)
- Demonstrate secure token lifecycle management
- Implement robust AppSec defenses (Anti-enumeration, brute-force mitigation)
- Serve as a **portfolio-grade backend security project**
- Reflect **real-world Spring Security usage**, not simplified demos

---

## Features

### Implemented

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

**Identity & Account Management**
- Secure Public Registration
- Async Email Verification flow (Mock sender)
- Secure Password Reset flow with short-lived TTL tokens (15 minutes)
- **Anti-enumeration defenses** on account recovery endpoints

**Security Hardening**
- Rate limiting on `/login` — **10 requests/minute per IP** (Bucket4j)
- Structured JSON responses for `401 Unauthorized` and `403 Forbidden`
- `429 Too Many Requests` with `Retry-After` header on rate limit breach
- Explicit endpoint whitelisting using Spring Security `SecurityFilterChain`

**Audit Logging**
- Async audit trail for: `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `LOGOUT`, `TOKEN_REFRESHED`, `TOKEN_REUSE_DETECTED`, `USER_REGISTERED`, `ROLE_GRANTED`, `ROLE_REVOKED`, `PASSWORD_RESET`
- IP address captured per event (supports `X-Forwarded-For` proxy headers)
- Events published via Spring's `ApplicationEventPublisher` (decoupled from business logic)

**Infrastructure & Documentation**
- Flyway migrations for schema versioning
- PostgreSQL for production
- Docker multi-stage build (non-root user, layer-optimized)
- Docker Compose for local development (app + PostgreSQL with healthchecks)
- Automated refresh token cleanup (scheduled daily at 03:00)
- **Interactive API Documentation** via Swagger UI / OpenAPI 3.0

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5+ |
| Security | Spring Security 6 |
| JWT | JJWT 0.12.6 |
| Rate Limiting | Bucket4j 8.x (in-memory) |
| Persistence | JPA / Hibernate + Flyway |
| Database (dev/test) | Testcontainers (PostgreSQL) |
| Database (production) | PostgreSQL 16 |
| Build | Maven |
| Container | Docker (multi-stage, eclipse-temurin:17-alpine) |
| API Docs | SpringDoc OpenAPI 2.8.x / Swagger UI |

---

## Getting Started

### Option A — Docker Compose (recommended)

```bash
# 1. Clone the repository
git clone [https://github.com/DavidHMoura/secure-auth-api.git](https://github.com/DavidHMoura/secure-auth-api.git)
cd secure-auth-api

# 2. Configure environment variables
cp .env.example .env
# Edit .env and set JWT_SECRET (min. 64 chars)

# 3. Start the stack (PostgreSQL + app)
docker compose up --build
```
# App available at http://localhost:8080
# Swagger UI at http://localhost:8080/swagger-ui/index.html
Option B — Local (Maven + external PostgreSQL)Bash# 1. Set environment variables
export SPRING_PROFILES_ACTIVE=dev
export JWT_SECRET=your-secret-with-at-least-64-characters-here
export DB_URL=jdbc:postgresql://localhost:5432/secureauth
export DB_USERNAME=postgres
export DB_PASSWORD=postgres

# 2. Run the application
    ./mvnw spring-boot:run

## Authentication Flow
    POST /api/v1/auth/register        → Register (public)

    GET  /api/v1/auth/verify-email    → Verify account using token

    POST /api/v1/auth/login           → Returns accessToken + refreshToken

    GET  /api/v1/me                   → Authenticated endpoint (Bearer token)

    POST /api/v1/auth/refresh         → New access + refresh token (old revoked)

    POST /api/v1/auth/forgot-password → Request password reset (Anti-enumeration)

    POST /api/v1/auth/reset-password  → Reset password using TTL token

    POST /api/v1/auth/logout          → Refresh token revoked

## API Endpoints

    Method, Endpoint,   Auth,   Description

    POST,/api/v1/auth/register,Public,Register a new user
    GET,/api/v1/auth/verify-email,Public,Verify user email via token
    POST,/api/v1/auth/login,Public,Login (rate limited)
    POST,/api/v1/auth/refresh,Public,Refresh JWT tokens
    POST,/api/v1/auth/forgot-password,Public,Request password reset
    POST,/api/v1/auth/reset-password,Public,Confirm new password
    POST,/api/v1/auth/logout,Public,Revoke session
    GET,/api/v1/me,Bearer,Current user info
    GET,/api/v1/admin/ping,ADMIN,Admin health check
    POST,/api/v1/admin/grant,ADMIN,Grant role to user
    POST,/api/v1/admin/revoke,ADMIN,Revoke role from user
    GET,/health,Public,Application health

## Security Design Decisions

### Anti-Enumeration Defenses
The /forgot-password endpoint is designed to return a generic 200 OK response regardless of whether the provided email exists in the database.
Why: Prevents malicious actors from using the recovery flow to harvest registered emails or map the user base.

### Account Verification Lock
Users cannot log in immediately after registration. A 403 Forbidden is strictly enforced until the user verifies their email via a unique token.
Why: Prevents mass creation of ghost accounts and ensures communication channels are valid before granting system access.

### Stateless Authentication
JWT is used instead of server-side sessions.
Why: Horizontal scalability without shared session state. Clear separation between authentication and application state.

### Refresh Token Rotation & Reuse Detection
Refresh tokens are persisted as SHA-256 hashes, revoked after use, and reissued with every refresh. When a revoked refresh token is presented, all active sessions for that user are immediately revoked.
Why: Token reuse strongly suggests theft. Revoking all sessions forces a full re-authentication, limiting the attack window and preventing replay attacks.

### Rate Limiting on Login
The /api/v1/auth/login endpoint is limited to 10 requests per minute per IP using Bucket4j (in-memory, greedy refill).
Why: Mitigates brute-force and credential stuffing attacks without requiring external infrastructure (Redis).

### Async Audit Logging
Authentication and authorization events are published via Spring's ApplicationEventPublisher and persisted asynchronously.
Why: Decouples audit concerns from business logic. A failure in audit persistence does not fail the auth request. Logged data supports incident response and compliance.

### Interactive API Documentation (Swagger)
The API exposes an OpenAPI 3.0 specification via Swagger UI, configured to support JWT Bearer injection directly in the browser.
Why: Provides a clear contract for frontend consumers and allows seamless manual testing of protected endpoints during development.

## Author

### David Moura
Software Engineering · Backend Development · Application Security

Focus areas: Java · Spring · Security · Backend Architecture · Linux

## Final Notes
### This project is not intended to be a full IAM solution off-the-shelf.

### Its goal is to demonstrate secure backend design, clean architecture, and explicit engineering decisions in a realistic Spring Boot application — with real infrastructure, real security patterns, and production-grade code quality.