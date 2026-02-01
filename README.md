# 🔐 Secure Auth API

Secure authentication and authorization API built with Spring Boot and Spring Security, focused on stateless JWT authentication, role-based access control (RBAC), and production-oriented security design.

#### |- This project is designed as a reference backend authentication service, demonstrating real-world security patterns, clean architecture, and explicit design decisions.

## 🚧 Project Status

### Active / Stable (v0.1.0)
Core authentication and authorization features are implemented and validated.
Further improvements focus on observability, testing, and hardening.

## 🎯 Project Goals

• Provide stateless authentication using JWT

• Implement database-backed RBAC (USER / ADMIN)

• Demonstrate secure token lifecycle management

• Serve as a portfolio-grade backend security project

• Reflect real-world Spring Security usage, not tutorial shortcuts

## ✨ Features

### ✅ Implemented

• Stateless JWT authentication

• Secure password hashing with BCrypt

• Role-based access control (USER / ADMIN)

• Database-backed roles (User ↔ Role many-to-many)

• JWT propagation of roles via claims

• Refresh token with rotation

• Refresh token persistence with hashing (SHA-256)

• Automatic refresh token revocation on reuse

• Unique token generation via JWT jti

• Admin-protected endpoints using @PreAuthorize

• Custom JWT authentication filter

• JSON responses for:

|- 401 Unauthorized

|- 403 Forbidden

• Clean separation between authentication, authorization, and business logic

• In-memory database (H2) for local development

### 🔜 Planned

• Automated security tests (authorization & token lifecycle)

• Audit logs for authentication and authorization events

• Rate limiting / brute-force protection

• CI pipeline (GitHub Actions)

• Dockerized environment

• PostgreSQL profile for production-like setup

## 🧱 Tech Stack

• Java 17+

• Spring Boot

• Spring Security

• JWT (JJWT)

• JPA / Hibernate

• H2 Database (dev)

• Maven

# 🗂️ Project Structure
    secure-auth-api/
    ├── src/main/java/com/davidmoura/secureauth
    │   ├── config/        # application & database seeders
    │   ├── controller/    # REST controllers
    │   ├── domain/        # JPA entities (User, Role, RefreshToken)
    │   ├── dto/           # request / response DTOs
    │   ├── repository/    # data access layer
    │   ├── security/      # JWT, filters, handlers, security config
    │   ├── service/       # business logic
    │   └── SecureAuthApiApplication.java
    ├── src/main/resources
    │   ├── application.properties
    │   └── application.yml
    ├── pom.xml
    └── README.md

## 🚀 Getting Started (Local)
### 1️⃣ Configure environment

Set JWT configuration in [ application.yml ] or [ application.properties ] :

    security.jwt.secret=change-this-secret-to-a-long-random-value
    security.jwt.access-expiration-seconds=900
    security.jwt.refresh-expiration-seconds=604800

    spring.jpa.open-in-view=false

### 2️⃣ Run the application
    ./mvnw clean spring-boot:run

## 🔐 Authentication Flow

### 1. Register user

### 2.  Login

• Returns accessToken + refreshToken

### 3. Access protected endpoints

• Send Authorization: Bearer <accessToken>

### 4. Refresh access token

• Use [/api/v1/auth/refresh]

• Old refresh token is revoked (rotation)

### 5. Logout

• Refresh token is revoked

## 🔐 Security Design Decisions

This project explicitly documents its security decisions to demonstrate engineering reasoning, not just implementation.

### Stateless Authentication

JWT is used instead of server-side sessions.

### Why:

• Horizontal scalability

• No server-side session storage

• Clear separation between authentication and application state

## Database-backed RBAC

Roles are stored in the database and associated via a many-to-many relationship.

### Why:

• Avoids hardcoded roles

• Enables dynamic role assignment

• Reflects real-world authorization models

### Refresh Token Rotation

### Refresh tokens are:

• Persisted in the database

• Stored as hashes

• Revoked after use

• Reissued with a new token on refresh

### Why:

• Prevents token replay attacks

• Enables explicit session invalidation

• Aligns with production-grade auth systems

### Explicit Token Uniqueness

All tokens include a unique [jti] claim.

### Why:

• Prevents deterministic token generation

• Ensures safe refresh rotation

• Improves traceability and audit potential

### Method-level Authorization

Authorization is enforced via @PreAuthorize.

### Why:

• Keeps rules close to business logic

• Improves readability and maintainability

• Allows fine-grained access control

### JSON-only Security Errors

Authentication and authorization failures return structured JSON.

### Why:

• API-first behavior

• No redirects or HTML responses

• Easier integration with clients and frontends

## 🧪 Example Endpoints

    • POST /api/v1/auth/login

    • POST /api/v1/auth/refresh

    • POST /api/v1/auth/logout

    • GET /api/v1/admin/ping (ADMIN only)

## 👤 Author

### David Moura
Software Engineering • Backend Development • Application Security

### Focus areas:
Java • Spring • Security • Backend Architecture • Linux

## 🧠 Final Notes

This project is not intended to be a full IAM solution.
Its goal is to demonstrate secure backend design, clean structure, and explicit security decisions in a realistic Spring Boot application.