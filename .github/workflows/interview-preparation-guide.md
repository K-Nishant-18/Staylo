# Staylo Comprehensive Interview Preparation Guide

Repository reviewed: [K-Nishant-18/Staylo](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo)

Staylo is a production-style Spring Boot 3 REST API backend designed for hostel and nearby accommodation management. It covers authentication, role-based access control (RBAC), student records, hostel rooms, room allocation, payments, PG/home listings, Swagger documentation, database seeding, Docker containerization, and GitHub Actions CI.

Use this guide to master the details of your project. The answers are structured to help you demonstrate deep architectural understanding, security awareness, data consistency practices, and production-level reasoning.

---

## Table of Contents
1. [Fast Project Pitch & High-Level Overview](#1-fast-project-pitch--high-level-overview)
2. [Architectural Patterns & Request Lifecycle](#2-architectural-patterns--request-lifecycle)
3. [Database Design, JPA, & Hibernate Deep-Dive](#3-database-design-jpa--hibernate-deep-dive)
4. [Spring Security & JWT Authentication Architecture](#4-spring-security--jwt-authentication-architecture)
5. [REST API Design, Validation, & Global Error Handling](#5-rest-api-design-validation--global-error-handling)
6. [Business Logic & Concurrency Control (Allocation Flow)](#6-business-logic--concurrency-control-allocation-flow)
7. [Swagger / OpenAPI Documentation Config](#7-swagger--openapi-documentation-config)
8. [Docker & Containerized Deployment Architecture](#8-docker--containerized-deployment-architecture)
9. [GitHub Actions CI Workflow & Build Pipelines](#9-github-actions-ci-workflow--build-pipelines)
10. [Testing Strategy (Unit, Integration, & Mocking)](#10-testing-strategy-unit-integration--mocking)
11. [System Design: Scaling, Hardening, & Future Improvements](#11-system-design-scaling-hardening--future-improvements)
12. [Resume Defense & Handling Difficult Interview Questions](#12-resume-defense--handling-difficult-interview-questions)

---

## 1. Fast Project Pitch & High-Level Overview

### Q1. Explain Staylo in 60 seconds.
**Answer:**
Staylo is a Java 17, Spring Boot 3 REST backend that automates student housing and rental accommodation management. It exposes endpoints for:
1. **User Authentication & Authorization:** Implements stateless JWT authentication with role-based access control (RBAC) supporting four distinct roles: `ADMIN`, `WARDEN`, `PROPERTY_OWNER`, and `STUDENT`.
2. **Hostel Operations:** Manages room inventory, tracks occupancy, automates room allocations, and logs transactions.
3. **Rental Listings (PG/Flats):** Allows property owners to publish and manage nearby housing options, and enables students to search and filter them.
4. **Billing & Payments:** Records student fees and monitors overdue balances.

Architecturally, it follows a clean layered design (`Controller -> Service -> Repository -> Database`), uses Spring Data JPA with MySQL for persistence, is fully containerized using a multi-stage Docker build, and has continuous integration automated via GitHub Actions.

### Q2. What real-world problem does Staylo solve?
**Answer:**
In many educational institutions, housing workflows are fragmented:
* **Hostel Wardens** track room occupancy and room conditions using physical registers or spreadsheets.
* **Students** struggle to search for off-campus accommodation (PGs/flats) when on-campus hostels are full.
* **Property Owners** lack a direct channel to advertise to nearby student populations.
* **Finance Teams** struggle to link room allocations to monthly fee payments.

Staylo solves this by consolidating on-campus hostel administration (inventory, allocation, payments) and off-campus housing listings into a single system, ensuring consistent data, role-based workflows, and automated tenancy tracking.

### Q3. Walk me through the tech stack and why you chose each technology.
**Answer:**
* **Java 17:** Selected for long-term support (LTS) stability, high performance, and modern features like Switch Expressions, Text Blocks, and records.
* **Spring Boot 3.2.5:** Simplifies enterprise Java development with autoconfiguration, dependency injection, and embedded Tomcat. It integrates cleanly with Spring Security 6.x and Spring Data JPA.
* **Spring Security 6.x & JSON Web Tokens (JJWT):** Enables stateless security, prevents session hijacking, and supports declarative method-level authorization (RBAC) via `@PreAuthorize`.
* **Spring Data JPA & Hibernate:** Eliminates JDBC boilerplate. Hibernate handles object-relational mapping (ORM) and manages relationships natively.
* **MySQL 8.0:** A reliable relational database choice that ensures ACID transactions, supports complex joins, and manages primary-key relational constraints for students, rooms, and payments.
* **H2 Database (In-Memory):** Used strictly for unit and integration testing via a dedicated `test` profile to keep tests fast, isolated, and repeatable.
* **Lombok:** Eliminates boilerplate code like getters, setters, builders, and constructors.
* **SpringDoc OpenAPI / Swagger UI:** Automates API contract documentation and simplifies API testing for developers and front-end teams.
* **Docker & Docker Compose:** Standardizes deployment environments, guaranteeing that local dev environments match testing and production environments.
* **GitHub Actions:** Provides automated testing on every code push or pull request.

---

## 2. Architectural Patterns & Request Lifecycle

```
[Client Request] 
       │
       ▼
┌──────────────────────────────┐
│       Servlet Filter         │ (JwtAuthFilter extracts & validates token)
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│      Spring Security         │ (Checks endpoint permits & @PreAuthorize)
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Controller           │ (Validates input DTOs with @Valid)
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           Service            │ (Manages business logic & @Transactional)
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│         Repository           │ (Interacts with DB using Spring Data JPA)
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│           Database           │ (MySQL persistence)
└──────────────────────────────┘
```

### Q4. Describe the software architecture of Staylo.
**Answer:**
Staylo follows a standard **Layered (n-tier) Architecture** consisting of:
1. **API Presentation Layer (Controllers):** Handles HTTP mapping, JSON serialization/deserialization, input validation (`@Valid`), and maps requests/responses to DTOs.
2. **Business Logic Layer (Services):** Implements business validation rules, manages transactional boundaries (`@Transactional`), orchestrates calls to repositories, and maps entities to DTOs.
3. **Data Access Layer (Repositories):** Defines database interactions by extending `JpaRepository`.
4. **Domain Layer (Entities):** Defines database tables, mappings, and relationships using JPA annotations.

It also includes cross-cutting layers:
* **Security Configuration:** Intercepts incoming requests for JWT validation.
* **Exception Handling:** Intercepts service/controller exceptions globally and returns uniform error payloads.

### Q5. What is the lifecycle of a secured request in Staylo?
**Answer:**
1. **TCP Connection & HTTP Request:** The client sends an HTTP request containing an `Authorization` header with a bearer token: `Authorization: Bearer <JWT>`.
2. **Security Filter Chain interception:** The request hits `JwtAuthFilter` (which extends `OncePerRequestFilter`).
   * The filter extracts the JWT token, extracts the subject (email) from it, and verifies its signature and expiration.
   * If valid, it loads `UserDetails` from the DB, generates a `UsernamePasswordAuthenticationToken`, attaches the user's role authorities, and stores it in the thread-local `SecurityContextHolder`.
3. **Controller Mapping & Authorization:** The request is routed to the dispatcher servlet, which resolves the target controller. Method-level security intercepts the call and checks `@PreAuthorize` rules (e.g., matching the user's role).
4. **Input Validation:** If authorized, parameters are validated using Jakarta Bean Validation. If invalid, a `MethodArgumentNotValidException` is thrown and handled by the `GlobalExceptionHandler`.
5. **Business Logic Execution:** The controller calls the service layer. If the method is annotated with `@Transactional`, a transaction boundary is established.
6. **Repository Execution:** The service accesses database records via repository proxies.
7. **Response Envelope Wrapping:** The service returns mapped DTO data to the controller. The controller wraps this data in a standard `ApiResponse` utility class, and returns it as a `ResponseEntity` with a success status (e.g., `200 OK` or `201 Created`).

### Q6. Why did you use DTOs (Data Transfer Objects) instead of returning JPA entities?
**Answer:**
Returning JPA entities directly from controllers is a major anti-pattern in production environments for several reasons:
1. **Security Vulnerabilities (Over-posting):** If an entity is used directly as a request body, an attacker can modify fields they shouldn't (e.g., updating user roles or set values of internal audit fields).
2. **Data Leakage:** JPA entities often contain sensitive attributes like password hashes, internal IDs, and audit fields that should never be returned to the client.
3. **Lazy Initialization Exception:** If a controller tries to serialize an entity containing lazy-loaded relationships (e.g., `Student.getUser()`) outside of an active database transaction, Hibernate will throw a `LazyInitializationException`.
4. **Coupling API to DB Schema:** Any database schema change (like renaming columns) would break the API contract with clients. DTOs decouple the external interface from the internal storage layer.

Staylo separates request and response DTOs using nested static classes (e.g., `StudentDTO.Request` and `StudentDTO.Response`), keeping payload definitions concise and organized.

### Q7. How does DTO mapping occur in your service layer? What are the tradeoffs?
**Answer:**
In Staylo, mapping is handled manually in helper methods (e.g., `toResponse` inside services):
```java
private StudentDTO.Response toResponse(Student student) {
    return StudentDTO.Response.builder()
            .id(student.getId())
            .userId(student.getUser().getId())
            .name(student.getUser().getName())
            .email(student.getUser().getEmail())
            .enrollmentNo(student.getEnrollmentNo())
            .course(student.getCourse())
            .year(student.getYear())
            .contactNo(student.getContactNo())
            .guardianName(student.getGuardianName())
            .guardianContact(student.getGuardianContact())
            .build();
}
```
**Tradeoffs:**
* **Pros:** Manual mapping is explicit, compile-time safe, requires no extra library dependencies, and is easy to debug. It prevents unwanted lazy-loading triggers because you choose exactly when to access properties.
* **Cons:** It introduces boilerplate code, which increases as the project grows.
* **Alternative:** In larger applications, I would integrate **MapStruct**, which generates type-safe mappings at compile time, maintaining speed while reducing manual code.

---

## 3. Database Design, JPA, & Hibernate Deep-Dive

### Q8. Draw the Database Entity Relationship diagram of Staylo.
**Answer:**
```
  ┌──────────────┐
  │    users     │
  └──────┬───────┘
         │ 1
         │
         │ 1 (Foreign Key: user_id)
  ┌──────▼───────┐             1 ┌──────────────┐
  │   students   ├──────────────►│ allocations  │
  └──────┬───────┘               └──────▲───────┘
         │ 1                            │ * (Foreign Key: room_id)
         │                              │
         │ * (Foreign Key: student_id)  │
  ┌──────▼───────┐               ┌──────┴───────┐
  │   payments   │               │ hostel_rooms │
  └──────────────┘               └──────────────┘
         
  ┌──────────────┐
  │ pg_listings  │◄────── [users] (Property Owner)
  └──────────────┘ 1 (Foreign Key: owner_id)
```

* **users:** Primary account storage. Linked 1:1 to `students`, and 1:N to `pg_listings` (where a user acts as the property owner).
* **students:** Linked 1:1 to `users`. Holds profile metadata.
* **hostel_rooms:** Holds room layouts. Linked 1:N to `allocations`.
* **allocations:** Relational join table mapping a `student` to a `hostel_room` for a specific date range.
* **payments:** Linked 1:N to `students`. Contains transaction histories.
* **pg_listings:** Represents off-campus housing. Linked 1:N to `users` (owners).

### Q9. Explain the difference between LAZY and EAGER loading. What strategies are implemented in Staylo?
**Answer:**
* **EAGER loading** tells Hibernate to fetch the related entity immediately in the same database query (typically via an outer join). This is default for `@ManyToOne` and `@OneToOne` relationships in JPA.
* **LAZY loading** tells Hibernate to load the associated entity dynamically only when it is accessed in code (using generated database proxy objects). This is default for `@OneToMany` and `@ManyToMany`.

In Staylo, all `@ManyToOne` and `@OneToOne` associations are explicitly configured to use lazy loading to optimize performance:
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false, unique = true)
private User user;
```
Without `fetch = FetchType.LAZY`, fetching a list of student profiles would run an implicit query on the `users` table for every single student profile, wasting memory and I/O cycles.

### Q10. What is the N+1 query problem, and how do you detect and solve it in Spring Data JPA?
**Answer:**
The **N+1 query problem** occurs when you fetch a parent entity and loop through its children. Hibernate runs 1 query to fetch the parent entities, and then runs N queries to fetch the child entities for each parent.
* **Example in Staylo:** If you fetch a list of 50 `Payment` records and map each payment to a DTO containing the student's name, Hibernate fetches 50 payments in one query, then makes 50 separate queries to load student details.
* **Detection:** Enable SQL statement logging in `application.yml` via:
  ```yaml
  spring:
    jpa:
      show-sql: true
      properties:
        hibernate:
          format_sql: true
  ```
  If you see duplicate queries for the same child tables, you have an N+1 issue.
* **Solutions:**
  1. **Fetch Join (JPQL):** Use a `JOIN FETCH` clause in a custom repository query:
     ```java
     @Query("SELECT p FROM Payment p JOIN FETCH p.student s JOIN FETCH s.user u")
     List<Payment> findAllWithStudentAndUser();
     ```
  2. **Entity Graphs:** Annotate repository methods with `@EntityGraph(attributePaths = {"student", "student.user"})`.

### Q11. Explain the `@PrePersist` and `@PreUpdate` annotations. How are they used in Staylo?
**Answer:**
These annotations define lifecycle callback methods on JPA entities:
* **`@PrePersist`:** Executed before a new entity is saved (inserted) to the database.
* **`@PreUpdate`:** Executed before an existing entity is updated in the database.

In Staylo, these are used to automate audit tracking:
```java
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@Column(name = "updated_at")
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```
This guarantees timestamps are recorded consistently without manual service intervention, while `updatable = false` ensures `createdAt` remains unchanged after inserts.

### Q12. Why is the database schema set to `ddl-auto: update` in the application configuration? What are the risks of this in production?
**Answer:**
`ddl-auto: update` tells Hibernate to inspect the schema, compare it with the JPA entity classes, and modify the schema automatically to match (e.g., adding columns).
* **Pros:** Highly convenient for local development because database tables update automatically as you edit Java entities.
* **Production Risks:**
  1. **Data Loss:** While Hibernate avoids deleting columns, if you change a field type or rename a field, it adds a new column, leaving the old one orphaned.
  2. **Lack of Control:** Schema modifications execute implicitly on startup without approval, which can lead to table locks.
  3. **Incompatibility:** It doesn't support complex migrations, like splitting tables or adding complex indexes.
* **Production Best Practice:** Disable Hibernate schema generation completely (`ddl-auto: validate` or `none`) and manage database migrations using structured versioned scripts via **Flyway** or **Liquibase**.

### Q13. What is the role of `data.sql` and how is it configured in `application.yml`?
**Answer:**
`data.sql` contains seed data to populate tables on startup:
```sql
INSERT IGNORE INTO users (name, email, password, role, is_active, created_at) VALUES ...
```
* **Configuration:**
  ```yaml
  spring:
    jpa:
      defer-datasource-initialization: true
    sql:
      init:
        mode: always
        data-locations: classpath:data.sql
  ```
* **Mechanics:**
  * `defer-datasource-initialization: true` tells Spring Boot to create tables using JPA entities first, and run the `data.sql` seed script only after tables are ready.
  * The `INSERT IGNORE` SQL dialect is used to prevent duplicate key constraint errors when the application restarts.

### Q14. Why is the payment amount represented as a `Double` and not `BigDecimal`?
**Answer:**
In the current Staylo codebase, payment amount is represented as a `Double`:
```java
@Column(nullable = false)
private Double amount;
```
* **Tradeoffs & Issues:**
  Using float or double values to represent currency is a common trap. Floating-point arithmetic is subject to rounding errors due to how fractional numbers are represented in binary.
* **Production Correction:**
  In a real financial application, payment values must always be stored using `java.math.BigDecimal` or a dedicated currency wrapper to prevent rounding errors. In the database, the column type should be configured as `DECIMAL(10, 2)`.

---

## 4. Spring Security & JWT Authentication Architecture

### Q15. Walk me through the security design of Staylo.
**Answer:**
Staylo uses **Spring Security 6.x** configured for stateless JWT-based authentication.

```
                    ┌────────────────────────────┐
                    │     HTTP Client Request    │
                    └─────────────┬──────────────┘
                                  │
                                  ▼
                    ┌────────────────────────────┐
                    │       JwtAuthFilter        │
                    └─────────────┬──────────────┘
                                  │ Token Valid?
                       ┌──────────┴──────────┐
                    No │                 Yes │
                       ▼                     ▼
              ┌─────────────────┐   ┌────────────────────────────────┐
              │ Skip Filter,    │   │ Load UserDetails from DB       │
              │ Continue Chain  │   ├────────────────────────────────┤
              └─────────────────┘   │ Create AuthToken & Populate    │
                                    │ Granted Authorities            │
                                    ├────────────────────────────────┤
                                    │ Set in SecurityContextHolder   │
                                    └────────────────┬───────────────┘
                                                     │
                                                     ▼
                                            [Resource Controller]
```

1. **CSRF (Cross-Site Request Forgery) is disabled:** CSRF attacks rely on sessions and browser cookies. Because our backend is stateless and authenticated using JWT bearer tokens in the `Authorization` header, CSRF is disabled.
2. **Session Creation Policy is Stateless:** No HTTP sessions are created or maintained on the server side (`SessionCreationPolicy.STATELESS`).
3. **JwtAuthFilter intercepts requests:** Inspects the request header, decodes the JWT token, and registers the user in the security context.
4. **Security Filter Chain configuration:**
   * All requests to `/`, `/index.html`, `/api/auth/**`, `/swagger-ui/**`, and `/api/listings` (browse listings) are configured as **public** (`permitAll()`).
   * All other endpoints are **protected** and require authorization.
5. **Method-Level Security:** Enable method security via `@EnableMethodSecurity` to declare role-based rules on specific controller methods using `@PreAuthorize("hasRole('...')")`.

### Q16. How is user information loaded during authentication? Explain the circular dependency problem.
**Answer:**
User information is loaded using a bean that implements `UserDetailsService`:
```java
@Bean
public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
}
```
* **Circular Dependency Issue:**
  In Spring Boot, putting `UserDetailsService` and `PasswordEncoder` inside the same configuration class as `SecurityFilterChain` often triggers a circular dependency on startup:
  `SecurityConfig` -> needs `UserDetailsService` -> needs `UserRepository` -> needs `SecurityConfig` (for password encoder injection).
* **Staylo's Solution:**
  Staylo isolates `UserDetailsService` inside a separate `ApplicationConfig` class, decoupling core security beans from the web security configuration chain.

### Q17. Walk me through the implementation of `JwtUtil.java`.
**Answer:**
`JwtUtil` handles token management operations:
* **Key Generation:**
  ```java
  private Key getSigningKey() {
      byte[] keyBytes = Decoders.BASE64.decode(
              java.util.Base64.getEncoder().encodeToString(secretKey.getBytes())
      );
      return Keys.hmacShaKeyFor(keyBytes);
  }
  ```
  *Note:* Base64 encoding the bytes of `secretKey` and decoding it immediately satisfies JJWT's parser requirements, providing an HMAC signing key.
* **Token Creation:**
  Uses `Jwts.builder()` to define token properties:
  * **Claims:** Stores custom role claims: `.setClaims(extraClaims)` (e.g. `role: ROLE_STUDENT`).
  * **Subject:** Sets the authenticated user's email: `.setSubject(subject)`.
  * **Timestamps:** Stores generation time and expiration offset (e.g., 24 hours).
  * **Signature:** Signs the payload using the secret signing key: `.signWith(key, SignatureAlgorithm.HS256)`.
* **Token Parsing & Validation:**
  ```java
  public boolean isTokenValid(String token, UserDetails userDetails) {
      final String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }
  ```
  It parses the claims using `Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)` and checks signature validity and expiration.

### Q18. How does `JwtAuthFilter` validate requests?
**Answer:**
`JwtAuthFilter` extends `OncePerRequestFilter`, which ensures it executes exactly once per incoming request:
1. **Extract Authorization Header:** It reads the `Authorization` header value:
   ```java
   String authHeader = request.getHeader("Authorization");
   ```
2. **Bearer Check:** If the header is null or does not start with `Bearer `, it skips the check and calls `filterChain.doFilter(request, response)` to let the request continue to the next filter.
3. **Parse Token & Extract User Email:**
   ```java
   String jwt = authHeader.substring(7);
   String userEmail = jwtUtil.extractUsername(jwt);
   ```
4. **Context Check & Database Verification:** If the email is extracted successfully and the security context has no existing authentication details:
   * It loads `UserDetails` from the DB using `UserDetailsService`.
   * It verifies token validity against the user details using `jwtUtil.isTokenValid(jwt, userDetails)`.
5. **Set Authentication Context:** If valid, it wraps the user in a `UsernamePasswordAuthenticationToken` along with their database roles:
   ```java
   UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
           userDetails, null, userDetails.getAuthorities()
   );
   ```
   It registers the token in the `SecurityContextHolder`, authorizing the request for downstream resource processing.

### Q19. How are user roles mapped to Spring Security permissions?
**Answer:**
Our `User` entity implements Spring Security's `UserDetails` interface. The roles are defined in `User.Role` (`ADMIN`, `WARDEN`, `PROPERTY_OWNER`, `STUDENT`).
We override `getAuthorities()` to map user roles:
```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```
* **Spring Security Rule:**
  Role strings must be prefixed with `ROLE_` (e.g., `ROLE_ADMIN`) for Spring's security checks to parse them correctly when using `@PreAuthorize("hasRole('ADMIN')")` or `hasAnyRole('ADMIN', 'WARDEN')`.

### Q20. Identify security vulnerabilities in the current Staylo implementation and how you would fix them.
**Answer:**
During my review of the codebase, I identified three security vulnerabilities:
1. **Insecure Hardcoded Secrets:**
   The JWT signing key (`jwt.secret`) and database credentials are committed in plain text inside `application.yml`.
   * **Fix:** Exclude passwords and secrets from configurations. Read them from environment variables (e.g., `${JWT_SECRET}`) or load them from a vault service like AWS Secrets Manager or HashiCorp Vault.
2. **Insecure Direct Object Reference (IDOR) on Student Profiles:**
   Inside `StudentController.java`, the endpoint `@GetMapping("/{id}")` is protected by `@PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")`.
   However, the service layer doesn't verify if the logged-in student matches the requested `id`. Any authenticated student can pass a random ID to inspect another student's profile.
   * **Fix:** Modify the check or write a custom security helper bean:
     ```java
     @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN') or (hasRole('STUDENT') and @securityService.isSelfStudent(authentication, #id))")
     ```
3. **No Password Policy:**
   `AuthDTO.RegisterRequest` only checks password length using `@Size(min = 6)`.
   * **Fix:** Add a validation pattern checking for uppercase letters, digits, and special characters.

---

## 5. REST API Design, Validation, & Global Error Handling

### Q21. What is the standard response structure of your API? Why is it structured this way?
**Answer:**
All API responses are wrapped in a generic `ApiResponse<T>` container:
```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
```
* **Why?**
  Using a consistent JSON envelope helps front-end teams handle responses predictably.
  * **Success:** Returns `success: true`, a success message, and the requested payload inside `data`.
  * **Failure:** Returns `success: false`, an error description in `message`, and `data` is set to null (or holds a validation errors map).

### Q22. How is request validation implemented in Staylo? Give code examples.
**Answer:**
Request payloads are validated using Jakarta validation annotations on DTOs, triggered by the `@Valid` annotation in controllers:
```java
public class StudentDTO {
    @Getter @Setter @Builder
    public static class Request {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotBlank(message = "Enrollment number is required")
        private String enrollmentNo;

        @NotBlank(message = "Contact number is required")
        private String contactNo;
        
        @Min(value = 1, message = "Year must be at least 1")
        @Max(value = 5, message = "Year cannot exceed 5")
        private Integer year;
    }
}
```
In the controller:
```java
@PostMapping
public ResponseEntity<ApiResponse<StudentDTO.Response>> register(
        @Valid @RequestBody StudentDTO.Request request) { ... }
```
If a payload fails checks, validation throws a `MethodArgumentNotValidException`.

### Q23. Walk me through the implementation of `GlobalExceptionHandler.java`.
**Answer:**
`GlobalExceptionHandler` is annotated with `@RestControllerAdvice`, allowing it to intercept exceptions globally across all controller components:
* **ResourceNotFoundException:** Maps to `404 Not Found`.
* **StayloException (Business Rules):** Maps to `400 Bad Request`.
* **BadCredentialsException:** Maps to `401 Unauthorized`.
* **AccessDeniedException (Spring Security):** Maps to `403 Forbidden`.
* **MethodArgumentNotValidException (Validation):** Intercepts fields failing annotation validations, builds a map of errors, and returns a detailed `400 Bad Request` payload:
  ```java
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
          MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach(error -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          errors.put(fieldName, errorMessage);
      });
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(ApiResponse.<Map<String, String>>builder()
                      .success(false)
                      .message("Validation failed")
                      .data(errors)
                      .build());
  }
  ```
* **Exception.class (Unexpected errors):** Catch-all that returns a `500 Internal Server Error`. In production, this should return a generic message to prevent leaking database details or stack traces, while logging the stack trace internally for monitoring.

---

## 6. Business Logic & Concurrency Control (Allocation Flow)

### Q24. Explain the Room Allocation workflow step-by-step.
**Answer:**
The room allocation flow is managed in `AllocationService.allocateRoom` and executes under a transaction:
1. **Lookup Entities:** Looks up the student profile and the requested room.
2. **Prevent Multiple Active Allocations:** Verifies that the student doesn't already have an active room allocation:
   ```java
   if (allocationRepository.existsByStudentIdAndStatus(studentId, Active)) {
       throw new StayloException("Student already has an active room allocation");
   }
   ```
3. **Verify Room Availability:** Checks if the room is available (meaning `occupied < capacity` and status is `AVAILABLE`).
4. **Audit Logger:** Retrieves the email of the logged-in administrator from the security context to record who performed the allocation:
   ```java
   String allocatedBy = SecurityContextHolder.getContext().getAuthentication().getName();
   ```
5. **Update Occupancy:** Increments the room's occupied count. If the room is now at full capacity, its status is updated to `FULL`.
6. **Save Changes:** Saves both the updated room status and the new allocation record to the database.

### Q25. Why is `@Transactional` critical on room allocation and vacating methods? How does it work?
**Answer:**
`@Transactional` ensures database operations are treated as a single atomic unit of work (all operations commit or all roll back):
* **Why it's needed:** Room allocation modifies two separate tables: it inserts an allocation record, and updates the occupancy and status in the `hostel_rooms` table.
* **Failure Scenario:** Without `@Transactional`, if inserting the allocation succeeds but saving the room updates fails (due to a database disconnect or validation error), the database will be left in an inconsistent state: the student is allocated to the room, but the room's occupancy isn't updated.
* **Under the hood:** Spring creates a dynamic database proxy class around the service bean. On method entry, the proxy opens a database transaction. If the method finishes successfully, the proxy commits the transaction. If a runtime exception is thrown, the proxy rolls back all database modifications performed within the method call.

### Q26. What concurrency issues could occur if two admins allocate the same room simultaneously? How do you prevent this?
**Answer:**
If two admins attempt to allocate the last bed in room `A-101` to two different students at the same time:
1. **Race Condition:**
   * Both threads read the room state simultaneously and see `occupied = 0` and `capacity = 1`.
   * Both threads proceed with the allocation, incrementing occupancy.
   * Both write changes back to the database. The room ends up with `occupied = 2` for a capacity of 1, resulting in an overbooking.

```
Admin 1 (Thread 1)             Admin 2 (Thread 2)             Database State
       │                              │                              │
       ├─► Read Room A-101 ───────────┼─────────────────────────────►│ occupied: 0, capacity: 1
       │                              ├─► Read Room A-101 ──────────►│ occupied: 0, capacity: 1
       │                              │                              │
       │ (Sees occupancy < capacity)  │ (Sees occupancy < capacity)  │
       ├─► Increment occupied to 1 ───┼─────────────────────────────►│ occupied: 1
       │                              ├─► Increment occupied to 1 ──►│ occupied: 1 (overwritten!)
       ▼                              ▼                              ▼
```

2. **How to prevent this:**
   * **Optimistic Locking:** Add a version field to the `HostelRoom` entity:
     ```java
     @Version
     private Long version;
     ```
     Hibernate checks the version field on update. If another transaction updated the room first, a `OptimisticLockException` is thrown, rolling back the second update.
   * **Pessimistic Locking:** Fetch the room using a database row lock (e.g., `SELECT FOR UPDATE` in SQL) to prevent other transactions from reading or updating the row until the transaction commits.
     ```java
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<HostelRoom> findWithLockById(Long id);
     ```

### Q27. Explain the listing ownership protection logic in PGListingService.
**Answer:**
In `PGListingService`, update and delete actions are protected to ensure users can only modify listings they own:
```java
String email = SecurityContextHolder.getContext().getAuthentication().getName();
if (!listing.getOwner().getEmail().equals(email)) {
    User user = userRepository.findByEmail(email).orElseThrow();
    if (user.getRole() != User.Role.ADMIN) {
        throw new StayloException("You can only update your own listings");
    }
}
```
* **Design Pattern:**
  * Checks if the authenticated user's email matches the listing owner's email.
  * If it doesn't match, it verifies if the logged-in user has the `ADMIN` role.
  * If the user is neither the owner nor an admin, it blocks the operation.
  This enforces ownership rules at the service layer, keeping business-specific security rules close to the data access layer.

---

## 7. Swagger / OpenAPI Documentation Config

### Q28. How is Swagger/OpenAPI configured in Staylo? Explain the code.
**Answer:**
Swagger documentation is configured in `SwaggerConfig.java` using a custom `OpenAPI` Bean:
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Staylo API")
                        .description("Hostel & Accommodation Management System API")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}
```
* **`SecurityScheme`:** Defines our authentication type as HTTP Bearer with a JWT format.
* **`SecurityRequirement`:** Automatically attaches the configured authorization scheme globally to all API routes in the Swagger UI. This adds an **"Authorize"** button to the Swagger UI page, letting developers paste a JWT token once to authenticate all requests.

### Q29. How do you test protected endpoints in the Swagger UI?
**Answer:**
1. Call the public login endpoint: `/api/auth/login`.
2. Copy the generated JWT token string from the JSON response.
3. Click the **"Authorize"** padlock button at the top of the Swagger page.
4. Paste the token into the input field and click Authorize.
5. All subsequent requests made using the "Try it out" feature will now include the token in the `Authorization` header.

### Q30. What OpenAPI annotations can be used to document controllers?
**Answer:**
* **`@Tag`:** Groups related API endpoints under a single category header in Swagger UI (e.g. `@Tag(name = "Students")`).
* **`@Operation`:** Adds a summary and description to a specific endpoint (e.g., `@Operation(summary = "Get student profile by ID")`).
* **`@ApiResponse`:** Documents potential HTTP status codes and responses returned by the endpoint.

---

## 8. Docker & Containerized Deployment Architecture

### Q31. Walk me through the `Dockerfile` structure of Staylo. Why did you choose this setup?
**Answer:**
Staylo uses a **multi-stage Docker build**:
```dockerfile
# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar staylo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "staylo.jar"]
```
* **Why use a multi-stage build?**
  1. **Smaller Image Size:** The build stage uses a full Java Development Kit (JDK) to compile code and build the jar file. The runtime stage only needs a Java Runtime Environment (JRE) to run the jar file. This reduces the final image size from over 400MB to under 150MB.
  2. **Security:** Excluding build tools (like Maven and compilation libraries) from the runtime image reduces its attack surface.
  3. **Dependency Caching:** The command `RUN ./mvnw dependency:go-offline` caches project dependencies. The cached layer is reused on subsequent builds unless `pom.xml` changes, speeding up build times.

### Q32. Explain the `docker-compose.yml` file. How do the services communicate?
**Answer:**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: staylo_mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: staylo_db
    ports:
      - "3306:3306"
    volumes:
      - staylo_mysql_data:/var/lib/mysql
    networks:
      - staylo_network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  app:
    build: .
    container_name: staylo_app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/staylo_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      mysql:
        condition: service_healthy
    networks:
      - staylo_network

volumes:
  staylo_mysql_data:

networks:
  staylo_network:
    driver: bridge
```
* **Service Communication:**
  Both containers are connected to the custom bridge network `staylo_network`.
  Instead of using localhost, the app container connects to MySQL using the database service name: `jdbc:mysql://mysql:3306/staylo_db`.
* **Health Check & Startup Order:**
  Using `depends_on` with `condition: service_healthy` ensures the app container waits to start until the MySQL container is healthy and ready to accept connections, preventing connection errors on startup.
* **Volume Persistence:**
  `staylo_mysql_data` maps database storage to host memory, preventing data loss when the containers stop or restart.

---

## 9. GitHub Actions CI Workflow & Build Pipelines

### Q33. Walk me through the GitHub Actions workflow in `.github/workflows/ci.yml`.
**Answer:**
```yaml
name: Java CI with Maven

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout repository
      uses: actions/checkout@v4

    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build and Run Tests
      run: mvn -B clean test
```
* **Trigger Conditions:** Runs automatically on pushes or pull requests targeting the `main` or `master` branches.
* **Checkout:** Pulls the repository code onto the runner virtual machine using `actions/checkout@v4`.
* **JDK Setup:** Configures Eclipse Temurin JDK 17. The setting `cache: maven` caches Maven dependencies to speed up subsequent workflow runs.
* **Execution:** Runs `mvn -B clean test` in non-interactive batch mode (`-B`), compiling the project and running all test suites.

### Q34. How does Maven caching improve CI pipeline speeds?
**Answer:**
Without caching, Maven downloads all dependencies defined in `pom.xml` from the Maven Central Repository on every run, which can take several minutes.
Setting `cache: maven` in `actions/setup-java` tells GitHub Actions to store the local Maven repository directory (`~/.m2/repository`) on the runner.
If `pom.xml` hasn't changed, subsequent workflow runs restore the cache, reducing build times.

---

## 10. Testing Strategy (Unit, Integration, & Mocking)

### Q35. What is the difference between a Unit Test and an Integration Test?
**Answer:**
* **Unit Tests** test a single class in isolation. They mock all external dependencies (such as repositories) using Mockito to focus tests on the business logic.
  * **Example:** `StudentServiceTest` mocks `StudentRepository` and `UserRepository` to test registration rules.
* **Integration Tests** verify that multiple layers work together correctly (e.g., verifying controllers can map endpoints, perform security checks, and save to a database).
  * **Example:** `AuthControllerTest` uses `@SpringBootTest` and `@AutoConfigureMockMvc` to spin up the application context and run requests against an H2 database.

### Q36. Explain Mockito mocking annotations.
**Answer:**
* **`@Mock`:** Creates a mock instance of a class or interface (e.g., `studentRepository`).
* **`@InjectMocks`:** Creates an instance of the class being tested and injects all `@Mock` fields into it (e.g., injecting mocked repositories into `StudentService`).
* **`when(...).thenReturn(...)`:** Configures stub behavior for mocked dependencies (e.g., `when(userRepository.findById(1L)).thenReturn(Optional.of(testUser))`).
* **`verify(...)`:** Verifies that a mocked dependency was called with specific arguments a certain number of times during the test.

### Q37. What is `@ActiveProfiles("test")` and how is it used in Staylo?
**Answer:**
`@ActiveProfiles("test")` tells Spring to load application properties from `application-test.yml` rather than the default `application.yml` during test execution.
* **Why it's critical:**
  In `application-test.yml`, we configure an in-memory H2 database for testing:
  ```yaml
  spring:
    datasource:
      url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
      driver-class-name: org.h2.Driver
  ```
  This ensures tests run against an isolated in-memory database, avoiding changes to the local MySQL database.

---

## 11. System Design: Scaling, Hardening, & Future Improvements

### Q38. How would you scale the Staylo backend horizontally to handle thousands of concurrent requests?
**Answer:**
To scale Staylo horizontally:

```
                          ┌──────────────┐
                          │  Load Balancer│ (e.g., NGINX / AWS ALB)
                          └──────┬───────┘
                                 │
                 ┌───────────────┼───────────────┐
                 ▼               ▼               ▼
           ┌───────────┐   ┌───────────┐   ┌───────────┐
           │ Staylo #1 │   │ Staylo #2 │   │ Staylo #3 │ (Stateless Instances)
           └─────┬─────┘   └─────┬─────┘   └─────┬─────┘
                 │               │               │
                 ▼               ▼               ▼
           ┌─────────────────────────────────────────┐
           │          Distributed Redis Cache        │ (Caching listings/rooms)
           └────────────────────┬────────────────────┘
                                │
                 ┌──────────────┴──────────────┐
                 ▼                             ▼
           ┌───────────┐                 ┌───────────┐
           │ DB Primary│ ──────────────► │DB Replica │ (Read/Write Splitting)
           │  (Write)  │  Replication    │  (Read)   │
           └───────────┘                 └───────────┘
```

1. **Stateless App Nodes:** Because the application uses JWT authentication, server nodes don't share session state. We can deploy multiple app instances behind a load balancer (like NGINX or AWS ALB) to distribute incoming traffic.
2. **Database Read/Write Splitting:** Configure a primary database instance for writes, and deploy multiple read replicas to handle read requests (such as browsing listings).
3. **Caching Layer:** Integrate an in-memory database like **Redis** to cache frequent database queries (such as listing searches).
4. **Connection Pooling:** Optimize the HikariCP database connection pool size in Spring Boot to handle database traffic efficiently.

### Q39. How would you implement a pagination and sorting system for the PG listing search endpoint?
**Answer:**
We can use Spring Data's `Pageable` and `Page` abstractions:
1. Update the repository query to accept a `Pageable` argument:
   ```java
   Page<PGListing> findByCityIgnoreCase(String city, Pageable pageable);
   ```
2. Update the service to request page and size parameters:
   ```java
   public Page<PGListingDTO.Response> getListingsByCity(String city, int page, int size, String sortBy, String direction) {
       Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
       Pageable pageable = PageRequest.of(page, size, sort);
       return pgListingRepository.findByCityIgnoreCase(city, pageable).map(this::toResponse);
   }
   ```
3. This restricts queries to returning subset pages (e.g., returning records 10-20), protecting database performance as datasets grow.

### Q40. How would you handle image uploads for PG listings?
**Answer:**
To handle image uploads:
1. **Avoid storing raw binary data (BLOBs) in the database:** This slows down database backups and increases storage costs.
2. **Object Storage Integration:** Use an object storage service like **Amazon S3** or Google Cloud Storage.
3. **Workflow:**
   * The owner uploads an image file via the API (`MultipartFile`).
   * The backend validates the file type (allowing only JPEG/PNG) and size.
   * The file is uploaded to the S3 bucket using a uniquely generated UUID filename.
   * The S3 bucket URL string is saved in the `pg_listings` table database record.
   * Clients fetch listing details and load images directly from the storage URL.

### Q41. How would you design a notifications service for payment reminders?
**Answer:**
1. **Asynchronous Processing:** To keep user interactions fast, sending notifications should run on background threads.
2. **Event-Driven Architecture:** Publish events when triggers occur (such as room allocations or payment updates) using Spring's application event publisher or a message broker (like RabbitMQ or Apache Kafka).
3. **Scheduled Jobs:** Run cron-style scheduled background tasks using Spring's `@Scheduled` annotation to query overdue balances daily and trigger email alerts.

---

## 12. Resume Defense & Handling Difficult Interview Questions

### Q42. Why are credentials committed to the source control repository?
**Answer:**
"For this demo repository, I included the default MySQL database credentials and the JWT secret key directly in the configuration file to make it simple for other developers to download and run the project locally. However, I am aware this is a major security risk for production environments. If I were deploying this application, I would read all secrets from environment variables using Spring's `${VARIABLE_NAME}` property injection, and document setup steps in a `.env.example` file instead."

### Q43. What was the most challenging feature to implement in Staylo?
**Answer:**
"Implementing the **Room Allocation Flow** was the most challenging task because it required ensuring data consistency across multiple database tables.
I had to check student status, verify that the student had no active allocations, check room capacity, and update the room occupancy.
Wrapping these operations in a `@Transactional` block was critical to prevent partial updates, and I had to design validation logic to handle concurrent allocation requests cleanly."

### Q44. I see that tests are failing to compile in some environments due to missing DTO classes. How would you debug this build issue?
**Answer:**
"I would debug this by verifying classpaths and ensuring clean builds:
1. Run a clean build using the Maven wrapper: `./mvnw clean compile test`.
2. Inspect the generated target directory structure (`target/classes/com/staylo`) to verify that the compiled DTO class files are present.
3. If the compiler is failing to resolve references, I would check for package naming issues, IDE configuration errors, or Lombok compilation conflicts.
4. Verify that Lombok is properly configured in the compiler path of the IDE and build plugins."

---

## 13. Core Java, Spring Boot, SQL, & Git Fundamentals (For Freshers)

### Q45. What are the four core principles of Object-Oriented Programming (OOP) and how does Staylo demonstrate them?
**Answer:**
The four core principles of OOP are **Inheritance**, **Polymorphism**, **Encapsulation**, and **Abstraction**.
1. **Encapsulation:** Wrapping code data and methods into a single class unit, and protecting access using access modifiers (e.g., `private`, `protected`, `public`).
   * *Staylo Example:* All entity classes (like `User` or `HostelRoom`) use private member variables (like `private String email`) and expose access via public getters and setters (managed by Lombok's `@Getter` and `@Setter` annotations). This hides internal state representation.
2. **Abstraction:** Hiding execution complexity and exposing only core features via interfaces or abstract classes.
   * *Staylo Example:* Our repositories (e.g., `UserRepository` extending `JpaRepository`) are declared as interfaces. The service layer interacts with the repository interfaces without knowing the underlying implementation details generated by Spring Data JPA at runtime.
3. **Inheritance:** Enabling one class to inherit states and behaviors from another, promoting code reuse.
   * *Staylo Example:* `JwtAuthFilter` extends Spring's `OncePerRequestFilter`. By inheriting from it, `JwtAuthFilter` reuses filter interception lifecycles and only overrides the specific `doFilterInternal` method.
4. **Polymorphism:** The ability of an object to take on many forms (via Method Overloading or Method Overriding).
   * *Staylo Example:* Overriding the `getAuthorities()` method from the `UserDetails` interface inside our custom `User` class to return role-based authorization collections.

### Q46. Explain the difference between Method Overloading and Method Overriding in Java.
**Answer:**
* **Method Overloading (Compile-Time / Static Polymorphism):**
  Occurs when two or more methods in the same class have the same name but different parameter lists (different types, number, or order of arguments). The compiler resolves which method to call based on the arguments passed at compile-time.
  * *Example:*
    ```java
    public void printInfo(String name) { ... }
    public void printInfo(String name, int age) { ... }
    ```
* **Method Overriding (Runtime / Dynamic Polymorphism):**
  Occurs when a subclass provides a specific implementation for a method that is already defined in its superclass or interface. The method name, return type, and parameters must match exactly. The JVM resolves which method to call at runtime based on the actual object type.
  * *Example:* Implementing the `UserDetailsService` interface's `loadUserByUsername` method in a custom configuration or service class.

### Q47. Why are Strings immutable in Java?
**Answer:**
Strings are immutable (cannot be modified after creation) in Java for several design reasons:
1. **String Pool Cache:** Java optimizes memory by storing string literals in a "String Pool". If multiple reference variables point to the same literal `"password123"`, they share the same memory slot. If strings were mutable, changing one reference would affect all other shared references.
2. **Security:** Strings are used for security-sensitive attributes like database credentials, passwords, network URLs, and file paths. If strings were mutable, an attacker could bypass authentication by altering connection parameters after validation checks.
3. **Thread Safety:** Immutability makes String objects inherently thread-safe. Multiple threads can read a string concurrently without synchronization, avoiding data race conditions.
4. **HashCode Caching:** Since string values cannot change, their hashcode is calculated and cached on creation. This makes strings fast keys in collection classes like `HashMap` or `HashSet`.

### Q48. What is the difference between `==` and the `equals()` method in Java?
**Answer:**
* **`==` Operator (Reference Comparison):**
  Compares memory addresses (references) of objects to check if they point to the exact same memory location. For primitive types (like `int`, `double`), it compares actual values.
  * *Example:* `user1 == user2` checks if both variables refer to the same object instance in memory.
* **`equals()` Method (Content Comparison):**
  Compares the actual content or state of two objects to check if they are logically equivalent. By default, `Object.equals()` uses the `==` operator, but classes should override `equals()` to implement custom value-based comparison logic.
  * *Example:* `user1.getEmail().equals(user2.getEmail())` compares the text contents of the emails, regardless of where the String objects are stored in memory.

### Q49. Explain the contract between `equals()` and `hashCode()` in Java.
**Answer:**
The contract defined in the Java Specification states:
1. **Consistent returns:** If two objects are equal according to the `equals(Object)` method, calling `hashCode()` on each of the two objects must produce the exact same integer value.
2. **Unequal hashcodes:** If two objects are unequal according to `equals()`, their `hashCode()` outputs do not have to be distinct. However, generating unique hashcodes for distinct objects improves search performance in hash-based structures.
3. **Implications:** Overriding `equals()` requires overriding `hashCode()`. Breaking this contract causes hash-based collections (like `HashMap`, `HashSet`, or `Hashtable`) to behave unpredictably, resulting in duplicate keys or failing to find objects in maps.

### Q50. What is the difference between `final`, `finally`, and `finalize` in Java?
**Answer:**
* **`final` Keyword:**
  Used as a modifier to apply restrictions:
  * On a **variable:** Makes the variable a constant (cannot be reassigned after initialization).
  * On a **method:** Prevents the method from being overridden by subclasses.
  * On a **class:** Prevents the class from being inherited (subclassed) (e.g., `public final class ClassName`).
* **`finally` Block:**
  Used in exception handling (`try-catch-finally`). It defines a block of code that is guaranteed to execute after exiting a try-catch block, regardless of whether an exception is thrown or caught. Typically used to release system resources (like database connections or file streams).
* **`finalize()` Method:**
  A protected method defined in `java.lang.Object`. It was called by the Garbage Collector before reclaiming an object's memory. It has been deprecated since Java 9 and removed in modern Java versions because it causes performance issues and resource leaks.

### Q51. Explain the difference between Checked and Unchecked Exceptions in Java.
**Answer:**
* **Checked Exceptions (Compile-Time Exceptions):**
  Exceptions that inherit from `java.lang.Exception` (excluding classes extending `RuntimeException`). The compiler forces you to handle these exceptions using a `try-catch` block or declare them in the method signature using the `throws` keyword.
  * *Example:* `IOException`, `SQLException`, `ClassNotFoundException`.
* **Unchecked Exceptions (Runtime Exceptions):**
  Exceptions that inherit from `java.lang.RuntimeException` or `java.lang.Error`. The compiler does not force you to handle or declare them. They usually indicate programming errors or unexpected state conditions.
  * *Example:* `NullPointerException`, `ArrayIndexOutOfBoundsException`, `IllegalArgumentException`.
  * *Staylo Application:* Custom exceptions like `StayloException` and `ResourceNotFoundException` extend `RuntimeException` (unchecked), allowing cleaner service layer interfaces by avoiding repetitive method-level `throws` declarations.

### Q52. What is the difference between `ArrayList` and `LinkedList`? When would you use each?
**Answer:**
* **`ArrayList` (Array-Backed):**
  Uses a dynamic, resizable array to store elements internally.
  * **Lookup Speed:** `O(1)` (direct index access is extremely fast).
  * **Modification Speed:** `O(N)` (inserting or deleting elements in the middle requires shifting remaining array elements).
  * **Use Case:** Best when read operations are frequent and insertions/deletions occur primarily at the end of the list.
* **`LinkedList` (Node-Backed):**
  Uses a doubly-linked list structure, where each node stores references to its value, the next node, and the previous node.
  * **Lookup Speed:** `O(N)` (requires traversing nodes sequentially from the head or tail to find the index).
  * **Modification Speed:** `O(1)` (inserting or deleting only requires modifying node references, without shifting elements).
  * **Use Case:** Best when insertions or deletions in the middle or at the beginning of the collection are frequent.

### Q53. How does `HashMap` work internally in Java?
**Answer:**
`HashMap` stores key-value pairs using a process called **Hashing**:
1. **Storage Array:** It maintains an internal array of nodes (buckets).
2. **Hash Calculation:** When calling `put(key, value)` or `get(key)`:
   * It calls `key.hashCode()` to generate an integer.
   * It calculates the index of the storage bucket using an index formula: `index = hash & (n - 1)` (where `n` is bucket size).
3. **Node Insertion:** If the bucket at the calculated index is empty, it inserts the node.
4. **Collision Handling:** If two different keys map to the same bucket index (a collision), `HashMap` resolves this by storing nodes in a Singly Linked List at that bucket index.
5. **Java 8 Optimization (Treeification):** If a linked list at a single bucket index grows past a threshold of 8 nodes and the map's total capacity exceeds 64, Java converts the list into a self-balancing **Red-Black Tree** (`TreeMap`), optimizing collision lookups from `O(N)` to `O(log N)`.

### Q54. What are the major Java 8 features used in this project? Explain Lambdas and Streams.
**Answer:**
The project uses several modern Java features introduced in Java 8:
* **Lambda Expressions:**
  Provides a concise way to represent anonymous function implementations (functional interfaces).
  * *Staylo Example:* Passing arrow syntax to filter arrays:
    ```java
    userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(...));
    ```
* **Stream API:**
  Provides functional-style pipelines for processing collections of data (filtering, transforming, collecting) in declarative chains.
  * *Staylo Example:* Transforming database entity collections to response DTO collections:
    ```java
    public List<RoomDTO.Response> getAvailableRooms() {
        return roomRepository.findAvailableRooms()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    ```
* **`Optional<T>` Class:**
  A container object which may or may not contain a non-null value, helping to prevent `NullPointerException` errors.

### Q55. What is Inversion of Control (IoC) and Dependency Injection (DI) in Spring?
**Answer:**
* **Inversion of Control (IoC):**
  A design principle where control over object lifecycles, configuration, and dependency resolution is transferred from the application code to a framework container (the Spring IoC Container). Instead of classes instantiating their dependencies manually, the container manages bean creation and wiring.
* **Dependency Injection (DI):**
  The design pattern that implements IoC. It is the process of providing dependent objects (dependencies) to a class instance from the outside, rather than having the class create them internally (using the `new` keyword).
  * *Staylo Example:* `AllocationService` requires `AllocationRepository`. Instead of initializing the repository manually, the Spring container injects the repository bean automatically.

### Q56. What are the different types of Dependency Injection in Spring? Why is Constructor Injection preferred?
**Answer:**
Spring supports three primary types of Dependency Injection:
1. **Field Injection:** Uses the `@Autowired` annotation directly on private variables. It is discouraged because it tightly couples the code to Spring, hides class dependencies, and makes unit tests harder (requires mocking libraries or reflection to inject dependencies).
2. **Setter Injection:** Uses setter methods annotated with `@Autowired`. Useful for optional dependencies that can change at runtime.
3. **Constructor Injection:** Dependencies are passed to the class constructor.
   * **Why it's preferred:**
     * **Immutability:** Allows declaring dependency fields as `final`, ensuring fields cannot be changed after instantiation.
     * **NonNull Guarantees:** Ensures the bean cannot be instantiated without its required dependencies, preventing runtime `NullPointerException` errors.
     * **Testing simplicity:** Does not require Mockito annotations or Spring context to run tests; you can instantiate the class using standard Java constructors: `new ServiceClass(mockRepository)`.
     * *Staylo Implementation:* Uses Lombok's `@RequiredArgsConstructor` to generate constructor injection boilerplate code automatically.

### Q57. What are the different scopes of a Spring Bean? What is the default scope?
**Answer:**
Spring supports 6 bean scopes (4 of which are web-aware):
1. **Singleton (Default):** The Spring container creates exactly one instance of the bean. This single instance is cached in memory and shared across all application threads.
2. **Prototype:** The Spring container creates a new bean instance every time it is requested by the application.
3. **Request (Web):** Creates a new bean instance for every incoming HTTP request. The bean is destroyed when the request completes.
4. **Session (Web):** Creates a new bean instance for the duration of an HTTP Session.
5. **Application (Web):** Creates a new bean instance once per ServletContext lifecycle.
6. **WebSocket (Web):** Creates a new bean instance for the duration of a WebSocket session.

### Q58. Explain the difference between `@Component`, `@Service`, and `@Repository` annotations.
**Answer:**
All three annotations mark a class as a Spring Bean, allowing the IoC container to discover it during classpath scans.
* **`@Component`:** The general-purpose stereotype annotation for any Spring-managed component.
* **`@Service`:** A specialization of `@Component` used to identify classes in the business logic layer. It has no special runtime behavior, but clarifies intent.
* **`@Repository`:** A specialization of `@Component` used to identify classes in the data access layer.
  * *Special Behavior:* It enables automatic translation of low-level database exceptions (like database connection issues or constraint violations) into Spring's unified `DataAccessException` hierarchy.

### Q59. Explain the difference between `@Controller` and `@RestController` in Spring Boot.
**Answer:**
* **`@Controller`:**
  Used for traditional web applications that render user interface templates (like Thymeleaf or JSP). Methods typically return a `String` representing the name of the template page to render. To return serialized response bodies directly, methods must be annotated with `@ResponseBody`.
* **`@RestController`:**
  A convenience annotation that combines `@Controller` and `@ResponseBody`.
  It tells Spring that every method within the class will serialize its return value directly into the HTTP response body (typically as JSON) instead of resolving templates. This is ideal for REST APIs.

### Q60. What is the purpose of `@Autowired` and `@Qualifier` annotations?
**Answer:**
* **`@Autowired`:** Marks a constructor, field, or setter method to resolve and inject matching bean dependencies automatically from the Spring container.
* **`@Qualifier`:** Used alongside `@Autowired` when multiple beans implement the same interface. It specifies the exact bean name to inject to resolve dependency conflicts.
  * *Example:* If we have two repository implementations, `MySqlStudentRepo` and `MongoStudentRepo`, we write:
    ```java
    @Autowired
    @Qualifier("mySqlStudentRepo")
    private StudentRepo studentRepo;
    ```

### Q61. Walk me through the Spring Bean Lifecycle.
**Answer:**
1. **Instantiation:** The container instantiates the bean class (running default constructors).
2. **Populate Properties:** Inject dependencies into bean fields.
3. **Aware Interfaces:** Call Aware interface methods if implemented (e.g., `BeanNameAware`, `ApplicationContextAware`).
4. **Pre-Initialization:** Spring invokes BeanPostProcessors: `postProcessBeforeInitialization()`.
5. **Initialization Callbacks:**
   * Runs methods annotated with `@PostConstruct`.
   * Invokes `afterPropertiesSet()` if the bean implements `InitializingBean`.
   * Invokes custom initialization methods configured via `@Bean(initMethod = "...")`.
6. **Post-Initialization:** Spring invokes BeanPostProcessors: `postProcessAfterInitialization()`.
7. **Ready for Use:** The bean is ready to use by the application.
8. **Destruction Callbacks:** When the application context closes:
   * Runs methods annotated with `@PreDestroy`.
   * Invokes `destroy()` if the bean implements `DisposableBean`.
   * Invokes custom destroy methods configured via `@Bean(destroyMethod = "...")`.

### Q62. What is the ACID model in Relational Databases? How does it apply to MySQL?
**Answer:**
The ACID model defines transaction properties that guarantee database integrity:
* **Atomicity:** Guarantees that all database modifications in a transaction complete successfully, or all changes roll back together.
* **Consistency:** Guarantees that transactions transition the database from one valid state to another, maintaining constraints and relations.
* **Isolation:** Guarantees that concurrent transactions execute independently of each other without cross-transaction conflicts.
* **Durability:** Guarantees that once a transaction commits, its modifications are written to non-volatile disk storage, persisting even during system failures.

In MySQL, ACID compliance is managed by the default **InnoDB** storage engine using Transaction Logs (Undo logs for Atomicity, Redo logs for Durability) and Lock management (for Isolation).

### Q63. Explain the difference between different types of SQL Joins.
**Answer:**
* **`INNER JOIN`:** Returns rows only when there is a match in both tables.
* **`LEFT JOIN` (Left Outer Join):** Returns all rows from the left table, and the matched rows from the right table. If there is no match, the right side returns `NULL`.
* **`RIGHT JOIN` (Right Outer Join):** Returns all rows from the right table, and the matched rows from the left table. If there is no match, the left side returns `NULL`.
* **`FULL JOIN` (Full Outer Join):** Returns all rows when there is a match in either the left or right table. (In MySQL, Full Join is simulated using a `UNION` of Left Join and Right Join).

```
    INNER JOIN              LEFT JOIN             RIGHT JOIN
   ┌───┬───┬───┐          ┌───┬───┬───┐          ┌───┬───┬───┐
   │ A │ x │ B │          │ A │ x │ B │          │ A │ x │ B │
   └───┴───┴───┘          ├───┼───┼───┤          ├───┼───┼───┤
                          │ A │   │   │          │   │   │ B │
                          └───┴───┴───┘          └───┴───┴───┘
```

### Q64. What is the difference between Primary Key and Foreign Key constraints?
**Answer:**
* **Primary Key:**
  A constraint that uniquely identifies each record in a table. It cannot contain `NULL` values, and there can be only one primary key per table. It automatically creates a clustered index.
* **Foreign Key:**
  A constraint that establishes a relationship link between records in two tables. It points to the primary key of another table, enforcing referential integrity (e.g., preventing deleting a record if other tables reference its primary key). Unlike Primary Keys, Foreign Keys can accept `NULL` values and can duplicate.

### Q65. What is the difference between DDL, DML, and DCL commands in SQL?
**Answer:**
* **DDL (Data Definition Language):**
  Commands used to define and modify the database structure (tables, schemas, indexes).
  * *Commands:* `CREATE`, `ALTER`, `DROP`, `TRUNCATE`.
* **DML (Data Manipulation Language):**
  Commands used to manage and modify data stored within database tables.
  * *Commands:* `SELECT`, `INSERT`, `UPDATE`, `DELETE`.
* **DCL (Data Control Language):**
  Commands used to manage database privileges and access control permissions.
  * *Commands:* `GRANT`, `REVOKE`.

### Q66. What is the difference between JPA and Hibernate?
**Answer:**
* **JPA (Jakarta Persistence API):**
  An abstract specification (API standard) that defines how to manage relational data in Java applications. It defines annotations (like `@Entity`, `@Id`) and interfaces (like `EntityManager`) but does not contain any executable database logic.
* **Hibernate:**
  An Object-Relational Mapping (ORM) framework that implements the JPA specification. It provides the concrete database implementation logic to convert Java objects into SQL queries.

### Q67. Explain the difference between `@Entity` and `@Table` annotations in Hibernate.
**Answer:**
* **`@Entity` (JPA):**
  Tells JPA/Hibernate that the class represents a database entity mapping to a database table, and its lifecycle should be managed by the persistence context.
* **`@Table` (JPA):**
  An optional annotation used to specify custom database table properties.
  * *Use Case:* If table properties are omitted, Hibernate names the table based on the entity class name. `@Table` allows you to customize the table name (e.g., mapping class `User` to table `users`), specify schema boundaries, or define unique multi-column index constraints.

### Q68. What are transaction propagation levels in Spring Boot (like `REQUIRED`, `REQUIRES_NEW`)?
**Answer:**
Transaction propagation defines how transactions behave when nesting transactional service methods:
* **`REQUIRED` (Default):**
  If a transaction already exists, the current method joins it. If no transaction exists, Spring creates a new transaction.
* **`REQUIRES_NEW`:**
  Spring always creates a new transaction for the method, suspending any existing transaction until the new transaction completes.
* **`MANDATORY`:**
  The method must run within an existing transaction. If no transaction is active, it throws an exception.
* **`NEVER`:**
  The method must run without transactions. If a transaction is active, it throws an exception.

### Q69. What is the difference between `git merge` and `git rebase`?
**Answer:**
* **`git merge`:**
  Integrates source branch updates into the target branch by creating a new "merge commit". It preserves the chronological history of both branches, showing a branch branching off and merging back.
* **`git rebase`:**
  Moves the branch's starting point to another commit. It rewrites git history by reapplying commits from the source branch on top of the target branch's latest commit, creating a linear history without merge commits.

### Q70. What is the difference between `git pull` and `git fetch`?
**Answer:**
* **`git fetch`:**
  Downloads latest commits and branch metadata from remote repositories to your local tracking branch (e.g., `origin/main`), but does not modify your active working directory or merge changes. It is safe to use to preview changes.
* **`git pull`:**
  Downloads remote changes and immediately attempts to merge them into your active local branch. It is a shortcut for running `git fetch` followed immediately by `git merge`.

---

## Technical Concept Quick Reference

| Feature | Code Location | Key Technologies |
| :--- | :--- | :--- |
| **Authentication Filter** | [JwtAuthFilter.java](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/java/com/staylo/security/JwtAuthFilter.java) | Spring Security, JJWT |
| **User Access Control** | [SecurityConfig.java](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/java/com/staylo/config/SecurityConfig.java) | Method Security, RBAC |
| **Room Allocation** | [AllocationService.java](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/java/com/staylo/service/AllocationService.java#L29-L66) | Transaction Management |
| **Validation Error Handling** | [GlobalExceptionHandler.java](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/java/com/staylo/exception/GlobalExceptionHandler.java#L48-L63) | `@RestControllerAdvice` |
| **Database Seed Script** | [data.sql](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/src/main/resources/data.sql) | SQL initialization |
| **Container Setup** | [Dockerfile](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo/Dockerfile) | Multi-stage build |
