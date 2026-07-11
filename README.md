# Staylo 🏠
### Hostel & Home Accommodation Management System

A production-quality **REST API** backend built with **Java 17 + Spring Boot 3 + MySQL**.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8 |
| API Docs | Swagger UI (SpringDoc OpenAPI 3) |
| Build | Maven |
| Tests | JUnit 5 + MockMvc + Mockito |
| Container | Docker + Docker Compose |

---

## ✨ Features

- **Role-Based Access Control** – ADMIN, WARDEN, PROPERTY_OWNER, STUDENT
- **JWT Authentication** – Stateless token-based auth
- **Hostel Room Management** – Add, update, track occupancy
- **Student Registration** – Link students to user accounts
- **Room Allocation** – Assign rooms, track check-in/out, vacate
- **PG / Home Listings** – Property owners can post nearby accommodation
- **Payment Tracking** – Record fees, mark overdue, view history
- **Swagger UI** – Interactive API documentation
- **Global Exception Handling** – Consistent JSON error responses

---

## 🏃 Quick Start

### Option 1 — Docker (Recommended)
```bash
docker-compose up --build
```
App runs at: `http://localhost:8080`

### Option 2 — Local Setup
1. Create MySQL database:
```sql
CREATE DATABASE staylo_db;
```
2. Update `src/main/resources/application.yml` with your DB credentials
3. Run:
```bash
mvn spring-boot:run
```

---

## 📖 Swagger UI

Open in browser: **http://localhost:8080/swagger-ui.html**

1. Call `POST /api/auth/login` with:
```json
{ "email": "admin@staylo.com", "password": "password123" }
```
2. Copy the token from the response
3. Click **Authorize** button → paste: `Bearer <your-token>`
4. All endpoints are now unlocked!

---

## 👤 Default Users (Seed Data)

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@staylo.com | password123 |
| WARDEN | warden@staylo.com | password123 |
| PROPERTY_OWNER | owner@staylo.com | password123 |
| STUDENT | student@staylo.com | password123 |

---

## 🗂️ API Endpoints Overview

### Auth
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Students
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/students` | ADMIN, WARDEN |
| GET | `/api/students` | ADMIN, WARDEN |
| GET | `/api/students/{id}` | ADMIN, WARDEN, STUDENT |
| PUT | `/api/students/{id}` | ADMIN, WARDEN |
| DELETE | `/api/students/{id}` | ADMIN |

### Hostel Rooms
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/rooms` | ADMIN, WARDEN |
| GET | `/api/rooms` | ADMIN, WARDEN |
| GET | `/api/rooms/available` | ADMIN, WARDEN, STUDENT |
| PUT | `/api/rooms/{id}` | ADMIN, WARDEN |
| PATCH | `/api/rooms/{id}/status` | ADMIN, WARDEN |

### Allocations
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/allocations` | ADMIN, WARDEN |
| GET | `/api/allocations` | ADMIN, WARDEN |
| GET | `/api/allocations/active` | ADMIN, WARDEN |
| PUT | `/api/allocations/{id}/vacate` | ADMIN, WARDEN |

### PG Listings
| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/listings` | Public |
| POST | `/api/listings` | ADMIN, PROPERTY_OWNER |
| PUT | `/api/listings/{id}` | ADMIN, PROPERTY_OWNER |
| DELETE | `/api/listings/{id}` | ADMIN, PROPERTY_OWNER |

### Payments
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/payments` | ADMIN, WARDEN |
| GET | `/api/payments/overdue` | ADMIN, WARDEN |
| GET | `/api/payments/student/{id}` | ADMIN, WARDEN, STUDENT |
| PATCH | `/api/payments/{id}/status` | ADMIN, WARDEN |

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📁 Project Structure

```
src/main/java/com/staylo/
├── config/          # SecurityConfig, SwaggerConfig, ApplicationConfig
├── controller/      # REST controllers (6 controllers)
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities (6 entities)
├── exception/       # GlobalExceptionHandler + custom exceptions
├── repository/      # Spring Data JPA repositories (6 repos)
├── security/        # JwtUtil, JwtAuthFilter
└── service/         # Business logic (6 services)
```

---

## 📝 Database Schema

```
users ──< students ──< allocations >── hostel_rooms
users ──< pg_listings
students ──< payments
```

---

*Built with ❤️ as a portfolio project by a fresher developer.*
