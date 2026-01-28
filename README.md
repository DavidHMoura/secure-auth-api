## 🔐 Secure Auth API

Secure authentication and authorization API built with Spring Boot and Spring Security, focused on stateless JWT authentication, role-based access control (RBAC), and clean security architecture.

Secure Auth API is an educational yet production-oriented authentication service, designed to reflect real-world backend and application security practices.

## 🚧 Project Status

In progress

This project is actively being developed.
Security features are implemented incrementally with a focus on correctness, clarity, and extensibility.

## 🎯 Project Goals

Provide stateless authentication using JWT

Implement role-based access control (RBAC) with real database-backed roles

Serve as a reference backend project for secure API design

Demonstrate clean separation of concerns in authentication systems

Act as a foundation for future extensions (refresh tokens, audits, hardening)

## ✨ Features
✅ Implemented

JWT-based stateless authentication

Secure password hashing with BCrypt

Role-based access control (USER / ADMIN)

Database-backed roles (User ↔ Role many-to-many)

JWT role propagation via claims

Method-level authorization with @PreAuthorize

Custom JWT authentication filter

Clear security boundaries (no session state)

Global exception handling

## 🔜 Planned / In Progress

Refresh token with rotation

Admin role management endpoints

Security audit logs (login, failures, access denied)

Brute-force protection / rate limiting

Automated security tests

CI pipeline (lint + tests)

## 🧱 Tech Stack

Java 17+

Spring Boot

Spring Security

JWT (JJWT)

Maven

PostgreSQL

Docker (planned)

## 🗂️ Project Structure
secure-auth-api/
├── src/main/java/com/davidmoura/secureauth
│   ├── config/        # application & security configuration
│   ├── controller/    # REST controllers
│   ├── domain/        # JPA entities (User, Role)
│   ├── dto/           # request / response DTOs
│   ├── exception/     # global exception handling
│   ├── repository/    # data access layer
│   ├── security/      # JWT, filters, security config
│   ├── service/       # business logic
│   └── SecureAuthApiApplication.java
├── src/main/resources
│   └── application.yml
├── pom.xml
└── README.md

## 🚀 Getting Started (Local)
1️⃣ Configure environment

Set JWT secret in application.yml or environment variables:

security:
jwt:
secret: your-secret-key
expiration-seconds: 3600

2️⃣ Run the application
./mvnw spring-boot:run

3️⃣ Authentication flow

Create a user

Login with email and password

Receive JWT access token

Send token via Authorization: Bearer <token>

Access protected endpoints based on roles

## 🔐 Security Notes

Stateless authentication (no HTTP session)

JWT roles are sourced directly from the database

RBAC enforced at method level and optionally at route level

Clear separation between:

authentication

authorization

business logic

Designed with defensive, backend-first mindset

## 🔐 Security Design Decisions

This project intentionally documents its security decisions to demonstrate engineering reasoning, not just implementation.

## Stateless Authentication

The API uses stateless JWT authentication instead of server-side sessions.

Why:

Horizontal scalability

No server-side session storage

Clear separation between authentication and application state

Common pattern in modern APIs and microservices

## Database-backed Roles (RBAC)

Roles are stored in the database and modeled as a many-to-many relationship between users and roles.

Why:

• Avoids hardcoded roles in code

• Allows dynamic role assignment and revocation

• Scales beyond simple USER / ADMIN models

• Reflects real-world access control systems

## JWT Role Propagation

User roles are embedded as claims inside the JWT at login time.

Why:

• Eliminates database lookups on every request

• Keeps authorization decisions fast and stateless

• Aligns with standard RBAC implementations in distributed systems

## Method-level Authorization

Authorization rules are enforced using method-level security (@PreAuthorize).

Why:

• Keeps business rules close to the code they protect

• Improves readability and maintainability

• Prevents over-reliance on route-based security alone

• Enables fine-grained access control

## Separation of Security Responsibilities

Authentication, authorization, and business logic are strictly separated:

• Authentication: credential validation and token issuance

• Authorization: role and permission checks

• Business logic: application behavior

Why:

• Reduces coupling

• Improves testability

• Prevents security logic from leaking into business code

## Explicit Failure Handling

Authentication and authorization failures are handled explicitly.

Why:

• Prevents ambiguous error states

• Improves auditability and observability

• Avoids leaking sensitive information through error messages

## Defensive-by-Design Mindset

The project prioritizes clarity and correctness over shortcuts.

Examples:

• No silent role coercion (e.g., auto-prefixing roles)

• No implicit privilege escalation

• Fail-fast behavior when security invariants are violated

Why:

• Security systems should fail loudly, not silently

• Hidden assumptions are a common source of vulnerabilities

## Future Security Considerations

The current design intentionally leaves room for:

• Refresh token rotation

• Audit logs for authentication and authorization events

• Rate limiting and brute-force protection

• Integration with external identity providers

These features can be added without architectural changes, validating the current design choices.

## 🧠 Why This Matters

This project is not intended to be a full IAM solution.
Its goal is to demonstrate how security decisions are reasoned about, documented, and implemented in a clean backend system.

## 👤 Author

David Moura
Software Engineering • Backend Development • Cybersecurity

Tech focus:
Linux • Java • Spring • Security • Backend Architecture