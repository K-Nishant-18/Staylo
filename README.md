# Staylo 🏠
### Hostel & Home Accommodation Management System

[![Build Status](https://github.com/K-Nishant-18/Staylo/actions/workflows/ci.yml/badge.svg)](https://github.com/K-Nishant-18/Staylo/actions)
[![Java Version](https://img.shields.io/badge/Java-17-orange.svg?style=flat&logo=openjdk)](https://openjdk.org/projects/jdk17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?style=flat&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg?style=flat&logo=docker)](https://www.docker.com/)

Staylo is a production-ready, high-performance **REST API Backend** built using **Spring Boot 3** and **MySQL** to streamline accommodation search, hostel management, roommate/room allocation, and payment workflows. 

Designed with enterprise best practices in mind, this project demonstrates clean architecture, solid security patterns, global exception handling, database optimizations, and a containerized workflow suitable for modern cloud environments.

---

## 💡 Key Architectural & Technical Highlights (For Interviewers)

This codebase was developed to showcase core software engineering principles and production-grade backend design patterns:

*   **Secure API Design:** Implemented stateless **JWT (JSON Web Token)** authentication integrated with **Spring Security**. Access is restricted using custom **Role-Based Access Control (RBAC)** across four user roles (`ADMIN`, `WARDEN`, `PROPERTY_OWNER`, `STUDENT`).
*   **Decoupled Architecture (DTO Pattern):** Used **Data Transfer Objects (DTOs)** across all endpoints to decouple database entities from the REST layer, preventing sensitive data exposure (like password hashes) and optimizing payload sizes.
*   **Database Best Practices:** Configured **JPA & Hibernate** with explicit relationship mappings (`@OneToMany`, `@ManyToOne`) and lazy loading to prevent $N+1$ query issues. Includes automated seed data utilizing schema-compliant `data.sql`.
*   **Robust Error Handling:** Designed a centralized `@ControllerAdvice` global exception handling mechanism, yielding consistent, user-friendly JSON error payloads (`timestamp`, `message`, `details`) instead of exposing stack traces.
*   **Automated Testing & CI:** Achieved high-speed local and remote test execution using **JUnit 5**, **Mockito**, and **MockMvc** with an isolated **H2 in-memory database** (`application-test.yml`), fully automated via **GitHub Actions**.
*   **Docker Containerization:** Implemented a **multi-stage Dockerfile** to compile the Maven artifact in a build container, resulting in a lightweight, production-ready JRE-only final image.

---

## 🚀 Tech Stack & Libraries

| Layer | Technology | Key Usage / Library |
| :--- | :--- | :--- |
| **Language** | Java 17 | OpenJDK 17 with Modern Stream & Optional API usage |
| **Backend Framework**| Spring Boot 3.2.x | Core, Web, Security, Data JPA |
| **Security** | Spring Security + JWT | Stateless Filters, Token Validation, RBAC |
| **Database** | MySQL 8.0 & H2 | Relational Database (Prod) & In-Memory (Testing) |
| **Documentation** | Swagger / OpenAPI 3 | Interactive Swagger UI for live API testing |
| **Build & CI** | Maven & GitHub Actions | Dependency management, Automated Test Runners |
| **Containerization** | Docker & Docker Compose | Containerized application & MySQL setup orchestration |

---

## 🗺️ System Architecture & Data Model

The application follows a clean 3-tier layer architecture:

```
[Client / Swagger UI] ──> [Controllers (REST)] ──> [Services (Business Logic)] ──> [Repositories (JPA)] ──> [MySQL DB]
```

### Entity Relationship Diagram
```
             ┌─────────────────┐
             │      User       │
             └──────┬───┬──────┘
                    │   │
        ┌───────────┘   └──────────┐
        ▼                          ▼
┌──────────────┐            ┌──────────────┐
│   Student    │            │  PG Listing  │
└──────┬───────┘            └──────────────┘
       │
       ├─────────────────┐
       ▼                 ▼
┌──────────────┐  ┌──────────────┐
│  Allocation  │  │   Payment    │
└──────┬───────┘  └──────────────┘
       ▼
┌──────────────┐
│ Hostel Room  │
└──────────────┘
```

---

## 🏃 Quick Start (Local Setup)

You can run this project locally in two ways.

### Option 1: Docker (Recommended - Zero Setup)
Make sure you have Docker and Docker Compose installed:

```bash
# Clone the repository
git clone https://github.com/K-Nishant-18/Staylo.git
cd Staylo

# Start the application and database
docker-compose up --build
```
*   The application will compile inside Docker and start at `http://localhost:8080`.
*   Swagger documentation is automatically available at `http://localhost:8080/swagger-ui.html`.

### Option 2: Standard Maven Build
1. Create a MySQL database named `staylo_db` in your local server.
2. Open [application.yml](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/resources/application.yml) and configure your MySQL database credentials:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/staylo_db
       username: <your_username>
       password: <your_password>
   ```
3. Build the project and run the Spring Boot application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## 📖 Interactive API Documentation

Interactive API specs are exposed via **SpringDoc OpenAPI**. Once the server is running, visit:
👉 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### 🔑 Authentication Walkthrough:
1. Locate the `POST /api/auth/login` endpoint inside Swagger UI.
2. Authenticate using one of the pre-configured accounts:
   ```json
   {
     "email": "admin@staylo.com",
     "password": "password123"
   }
   ```
3. Copy the returned JWT token from the JSON response.
4. Click the **Authorize** button at the top-right of the Swagger page.
5. Paste the token in this format: `Bearer <YOUR_JWT_TOKEN>` and click Authorize.
6. All secured API endpoints are now unlocked for testing!

### 👤 Pre-Seeded Accounts (Testing Roles)

| Role | Email | Password | Allowed Context |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@staylo.com` | `password123` | Full access, user creation, room & PG management |
| **WARDEN** | `warden@staylo.com` | `password123` | Hostel room allocations, payment checks |
| **PROPERTY_OWNER** | `owner@staylo.com` | `password123` | Create and manage local PG listings |
| **STUDENT** | `student@staylo.com` | `password123` | Check available rooms, view personal invoices |

---

## 🧪 Comprehensive Testing

To run the automated test suite locally:
```bash
mvn test
```
The testing configuration is isolated in [application-test.yml](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/test/resources/application-test.yml), utilizing an in-memory **H2 Database** ensuring tests do not corrupt your local or production database, nor do they require a running database server to pass.

---

## 📂 Project Structure Directory View

```
Staylo/
├── .github/workflows/   # CI/CD Workflows (GitHub Actions)
├── src/
│   ├── main/
│   │   ├── java/com/staylo/
│   │   │   ├── config/      # Security, JWT, & OpenAPI Configurations
│   │   │   ├── controller/  # REST Endpoints (RBAC Controlled)
│   │   │   ├── dto/         # Request & Response payload structures (DTOs)
│   │   │   ├── entity/      # JPA Database Models (Hibernate)
│   │   │   ├── exception/   # Centralized GlobalExceptionHandler & custom exceptions
│   │   │   ├── repository/  # Spring Data JPA Data Access interfaces
│   │   │   ├── security/    # JWT Authentication Filters & Entry Points
│   │   │   └── service/     # Domain Services (Business Logic)
│   │   └── resources/
│   │       ├── application.yml  # Main configurations & DB credentials
│   │       └── data.sql         # Seed data script for initial test users
│   └── test/                # Unit & Integration tests using H2 Database
├── Dockerfile           # Multi-stage container setup
├── docker-compose.yml   # Multi-container orchestrator
└── pom.xml              # Maven dependencies & build settings
```
