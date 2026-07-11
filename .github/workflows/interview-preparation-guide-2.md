# Staylo Interview Preparation Guide

Repository reviewed: `K-Nishant-18/Staylo`

Staylo is a Spring Boot 3 REST API backend for hostel and nearby accommodation management. It covers authentication, role-based access control, student records, hostel rooms, room allocation, payments, PG/home listings, Swagger documentation, seed data, Docker, and CI.

Use this document as a rehearsal sheet. The strongest interview answers should be specific to the codebase, honest about tradeoffs, and connected to real backend concepts.

---

## 1. Fast Project Pitch

### Q1. Explain Staylo in 60 seconds.

**Answer:**  
Staylo is a Java 17 Spring Boot backend for managing hostel and home accommodation workflows. It exposes REST APIs for authentication, students, hostel rooms, room allocation, payments, and PG listings. The backend uses Spring Security with JWT for stateless authentication, method-level RBAC with roles like `ADMIN`, `WARDEN`, `PROPERTY_OWNER`, and `STUDENT`, Spring Data JPA with MySQL for persistence, DTOs for request and response separation, global exception handling, Swagger/OpenAPI documentation, Docker containerization, and a GitHub Actions workflow for Maven tests.

### Q2. What problem does Staylo solve?

**Answer:**  
It centralizes accommodation workflows that are usually scattered across manual records: student registration, room inventory, occupancy tracking, hostel room allocation, payment tracking, and browsing nearby PG/home listings.

### Q3. What are the main modules?

**Answer:**  
The main modules are:

- Authentication and users
- Student profile management
- Hostel room management
- Room allocation and vacating
- Payment recording and tracking
- PG/home listing browsing and owner management
- Security, exception handling, API documentation, testing, and deployment configuration

### Q4. Why did you choose Spring Boot?

**Answer:**  
Spring Boot is a good fit for a production-style REST backend because it gives fast setup, embedded server support, dependency injection, Spring MVC, Spring Security, Spring Data JPA, validation, and testing support. It lets the project focus on business workflows rather than low-level server and database boilerplate.

### Q5. What makes this project resume-worthy?

**Answer:**  
It demonstrates backend fundamentals interviewers care about: layered architecture, REST API design, JWT security, RBAC, JPA relationships, DTOs, validation, global error handling, transactional business logic, database seed data, Swagger docs, Docker, and automated tests.

---

## 2. Architecture Questions

### Q6. Describe the architecture.

**Answer:**  
The project follows a layered architecture:

`Controller -> Service -> Repository -> Database`

Controllers expose REST endpoints and handle HTTP concerns. Services contain business rules, transactions, and mapping to DTOs. Repositories use Spring Data JPA to abstract database access. Entities represent persisted tables.

### Q7. Why use a layered architecture?

**Answer:**  
It separates responsibilities. Controllers stay thin, services own business logic, repositories handle data access, and entities model persistence. This makes the code easier to test, maintain, and extend.

### Q8. What are the packages in the backend?

**Answer:**  
The main packages are:

- `com.staylo.controller`
- `com.staylo.service`
- `com.staylo.repository`
- `com.staylo.entity`
- `com.staylo.dto`
- `com.staylo.security`
- `com.staylo.config`
- `com.staylo.exception`

### Q9. What is the responsibility of controllers?

**Answer:**  
Controllers receive HTTP requests, validate request bodies using `@Valid`, apply endpoint-level authorization with `@PreAuthorize`, call service methods, and wrap responses inside `ApiResponse`.

### Q10. What is the responsibility of services?

**Answer:**  
Services implement business logic such as preventing duplicate students, checking room availability before allocation, updating room occupancy, validating ownership of listings, and converting entities into response DTOs.

### Q11. What is the responsibility of repositories?

**Answer:**  
Repositories extend `JpaRepository` and provide CRUD operations plus query methods like `findByEmail`, `findAvailableRooms`, `findByStudentId`, and custom JPQL queries.

### Q12. Why use DTOs?

**Answer:**  
DTOs decouple API payloads from database entities. They prevent exposing sensitive fields like password hashes, avoid leaking internal JPA relationships, control response shape, and allow request validation annotations.

### Q13. What is the role of `ApiResponse<T>`?

**Answer:**  
It standardizes API responses with `success`, `message`, and `data`. This gives clients a predictable response structure for both successful and failed operations.

### Q14. What are the main design patterns used?

**Answer:**  
Dependency Injection through Spring, Repository pattern through Spring Data JPA, DTO pattern, layered architecture, builder pattern via Lombok, filter pattern for JWT authentication, and global exception handling through `@RestControllerAdvice`.

### Q15. How does a request flow through the app?

**Answer:**  
For a protected request, the JWT filter checks the `Authorization` header, validates the token, sets authentication in the security context, Spring Security checks access rules, the controller validates the request and calls the service, the service applies business logic and repositories persist or fetch data, then the controller returns an `ApiResponse`.

---

## 3. Technology Stack Questions

### Q16. What Java version does the project use?

**Answer:**  
Java 17.

### Q17. What Spring Boot version is used?

**Answer:**  
The `pom.xml` uses Spring Boot `3.2.5`.

### Q18. What database is configured?

**Answer:**  
MySQL is configured for the main application. H2 is configured for tests through `application-test.yml`.

### Q19. What dependencies are important?

**Answer:**  
Important dependencies include:

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- MySQL connector
- JJWT libraries
- Lombok
- SpringDoc OpenAPI
- Spring Boot Test
- Spring Security Test
- H2 for testing

### Q20. Why use Lombok?

**Answer:**  
Lombok reduces boilerplate for getters, setters, constructors, and builders. In this project, annotations like `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, and `@RequiredArgsConstructor` keep classes concise.

### Q21. What are the risks of Lombok?

**Answer:**  
It hides generated code, can confuse newcomers, requires IDE support, and can make debugging slightly less direct. In production, I would ensure the team is comfortable with it and avoid using Lombok in ways that obscure domain behavior.

### Q22. Why use SpringDoc OpenAPI?

**Answer:**  
It generates interactive Swagger documentation so developers can inspect and test endpoints, including JWT-protected APIs through the configured bearer authentication scheme.

---

## 4. Domain Model Questions

### Q23. What are the main entities?

**Answer:**  
The main entities are `User`, `Student`, `HostelRoom`, `Allocation`, `Payment`, and `PGListing`.

### Q24. Explain the `User` entity.

**Answer:**  
`User` stores common account details: name, email, password, role, created time, and active status. It implements `UserDetails`, so Spring Security can use it directly for authentication and authorization.

### Q25. Why does `User` implement `UserDetails`?

**Answer:**  
Spring Security works with `UserDetails` to load username, password, authorities, and account status. By implementing it, the custom `User` entity integrates directly with `DaoAuthenticationProvider`.

### Q26. What roles exist?

**Answer:**  
The roles are `ADMIN`, `WARDEN`, `PROPERTY_OWNER`, and `STUDENT`.

### Q27. How are authorities generated?

**Answer:**  
`User.getAuthorities()` returns `ROLE_` plus the role name, for example `ROLE_ADMIN`. This matches Spring Security's `hasRole` and `hasAnyRole` checks.

### Q28. Explain the `Student` entity.

**Answer:**  
`Student` represents a student profile linked one-to-one with a user. It stores enrollment number, course, year, contact number, guardian details, and creation timestamp.

### Q29. Why separate `User` and `Student`?

**Answer:**  
`User` represents login identity and role. `Student` represents student-specific profile data. This separation allows non-student users, like wardens and property owners, to exist without student fields.

### Q30. Explain the `HostelRoom` entity.

**Answer:**  
`HostelRoom` stores room number, hostel block, floor, room type, capacity, occupied count, fee, bathroom and AC flags, and status. It has an `isAvailable()` helper that checks both occupancy and room status.

### Q31. What room statuses exist?

**Answer:**  
`AVAILABLE`, `FULL`, and `MAINTENANCE`.

### Q32. What room types exist?

**Answer:**  
`SINGLE`, `DOUBLE`, and `TRIPLE`.

### Q33. Explain the `Allocation` entity.

**Answer:**  
`Allocation` links a student to a hostel room with check-in and optional check-out dates. It tracks status as `ACTIVE`, `VACATED`, or `CANCELLED`, stores who allocated the room, and maintains created and updated timestamps.

### Q34. Explain the `Payment` entity.

**Answer:**  
`Payment` links payments to a student. It stores amount, payment type, payment status, payment date, due date, transaction ID, mode, remarks, and creation timestamp.

### Q35. Explain the `PGListing` entity.

**Answer:**  
`PGListing` represents nearby accommodation listed by a property owner. It stores owner, title, address, city, monthly rent, listing type, room counts, amenities, contact number, availability, gender preference, and timestamps.

### Q36. What listing types exist?

**Answer:**  
`PG`, `FLAT`, `ROOM`, and `HOSTEL`.

### Q37. What gender preferences exist?

**Answer:**  
`MALE`, `FEMALE`, and `ANY`.

---

## 5. JPA and Database Questions

### Q38. What JPA relationships are used?

**Answer:**  
The project uses:

- `User` to `Student`: one-to-one
- `User` to `PGListing`: one-to-many conceptually, represented as many listings pointing to one owner
- `Student` to `Allocation`: many allocations can belong to one student
- `HostelRoom` to `Allocation`: many allocations can belong to one room over time
- `Student` to `Payment`: many payments can belong to one student

### Q39. Why use `@ManyToOne(fetch = FetchType.LAZY)`?

**Answer:**  
Lazy loading avoids fetching related entities until needed. For example, fetching a `Payment` does not immediately fetch the whole `Student` object unless the service maps it to a response.

### Q40. What is the risk with lazy loading?

**Answer:**  
If related data is accessed outside a transaction or persistence context, it can cause `LazyInitializationException`. It can also create N+1 query issues when mapping many records and accessing nested relationships.

### Q41. Does this project risk N+1 queries?

**Answer:**  
Yes, potentially. Mapping lists of payments, allocations, students, or listings accesses related entities such as `student.user` or `listing.owner`. For small datasets it is fine, but for production-scale data I would consider fetch joins, entity graphs, or custom projection queries.

### Q42. What is `@PrePersist` used for?

**Answer:**  
It sets timestamps before a new entity is inserted, such as `createdAt` and sometimes `updatedAt`.

### Q43. What is `@PreUpdate` used for?

**Answer:**  
It updates `updatedAt` before an existing entity is modified.

### Q44. Why use `EnumType.STRING`?

**Answer:**  
It stores enum names as readable strings in the database. This is safer than ordinal storage because changing enum order will not corrupt meaning.

### Q45. What is `ddl-auto: update`?

**Answer:**  
It tells Hibernate to update the schema based on entities. It is convenient for development, but for production I would use migration tools like Flyway or Liquibase.

### Q46. Why is MySQL configured with `createDatabaseIfNotExist=true`?

**Answer:**  
It improves local developer setup by creating the database automatically if it does not exist.

### Q47. What does `data.sql` do?

**Answer:**  
It seeds initial users and hostel rooms. It includes admin, warden, property owner, and student users with BCrypt-encoded passwords.

### Q48. Why use `INSERT IGNORE` in seed data?

**Answer:**  
It prevents duplicate insert failures when seed data runs multiple times against the same MySQL database.

### Q49. What database indexes would you add?

**Answer:**  
Useful indexes include:

- `users.email`
- `students.enrollment_no`
- `students.user_id`
- `hostel_rooms.room_number`
- `allocations.student_id, status`
- `allocations.room_id, status`
- `payments.student_id`
- `payments.status`
- `pg_listings.city`
- `pg_listings.owner_id`

### Q50. Why use repositories instead of writing JDBC manually?

**Answer:**  
Spring Data JPA reduces boilerplate, provides CRUD methods automatically, supports derived queries, and lets us express domain queries through method names or JPQL.

### Q51. Explain one custom JPQL query.

**Answer:**  
`HostelRoomRepository.findAvailableRooms()` uses JPQL to return rooms where `occupied < capacity` and status is `AVAILABLE`. This matches the domain definition of an available room.

### Q52. Why does `PaymentRepository.totalPaidByStudent` return `Double` and not primitive `double`?

**Answer:**  
`SUM` can return `null` if there are no matching rows. The service handles this by returning `0.0` when the repository result is null.

---

## 6. REST API Questions

### Q53. What are the auth endpoints?

**Answer:**  
`POST /api/auth/register` creates a user and returns a JWT. `POST /api/auth/login` authenticates an existing user and returns a JWT.

### Q54. What are the student endpoints?

**Answer:**  
They include create, get all, get by ID, get by enrollment number, update, and delete student.

### Q55. What are the room endpoints?

**Answer:**  
They include add room, get all rooms, get available rooms optionally by type, get room by ID, update room, and update room status.

### Q56. What are the allocation endpoints?

**Answer:**  
They include allocate room, get all allocations, get active allocations, get allocation by ID, get a student's active allocation, and vacate a room.

### Q57. What are the payment endpoints?

**Answer:**  
They include record payment, get all payments, get payment by ID, get payments for a student, get total paid by student, get overdue payments, and update payment status.

### Q58. What are the listing endpoints?

**Answer:**  
They include public listing browse, public listing details, create listing, get my listings, admin get all listings, update listing, and delete listing.

### Q59. Why are some listing endpoints public?

**Answer:**  
Browsing available PG/home listings is a public use case, similar to accommodation search. Creating, editing, and deleting listings requires authentication and role checks.

### Q60. Why use `@RequestParam` for filters?

**Answer:**  
Filters like `city`, `maxRent`, room `type`, and payment `status` are optional query criteria, so request parameters are appropriate.

### Q61. What HTTP status codes are used?

**Answer:**  
Create operations return `201 Created`. Successful reads and updates generally return `200 OK`. Not found returns `404`, validation and business errors return `400`, bad credentials return `401`, and access denied returns `403`.

### Q62. Why use `ResponseEntity`?

**Answer:**  
It allows the controller to control HTTP status codes and response bodies explicitly.

### Q63. Why use nested `Request` and `Response` DTO classes?

**Answer:**  
It groups request and response payloads by domain, for example `StudentDTO.Request` and `StudentDTO.Response`, which keeps related DTOs organized.

---

## 7. Security Questions

### Q64. How does JWT authentication work in this project?

**Answer:**  
After registration or login, `AuthService` generates a JWT using `JwtUtil`. The client sends it in the `Authorization: Bearer <token>` header. `JwtAuthFilter` extracts the token, validates it, loads the user, and sets authentication in `SecurityContextHolder`.

### Q65. Why use stateless sessions?

**Answer:**  
JWT lets the server avoid storing session state. This improves scalability because any backend instance can validate the token if it has the signing key.

### Q66. Where is stateless behavior configured?

**Answer:**  
In `SecurityConfig`, with `SessionCreationPolicy.STATELESS`.

### Q67. Why disable CSRF?

**Answer:**  
For stateless REST APIs using bearer tokens rather than browser cookies, CSRF protection is usually not required. If the app later used cookies for auth, CSRF would need to be reconsidered.

### Q68. How is password hashing handled?

**Answer:**  
`BCryptPasswordEncoder` is configured as the `PasswordEncoder`. Registration stores `passwordEncoder.encode(request.getPassword())`.

### Q69. Why BCrypt?

**Answer:**  
BCrypt is a slow adaptive hashing algorithm designed for passwords. It includes salt and makes brute-force attacks harder than simple hashes like SHA-256.

### Q70. What does `AuthenticationManager` do in login?

**Answer:**  
It authenticates the email and password using `DaoAuthenticationProvider`, which loads the user through `UserDetailsService` and checks the password with BCrypt.

### Q71. What does `UserDetailsService` do?

**Answer:**  
It loads a user by email from `UserRepository`. Spring Security uses it during login and JWT validation.

### Q72. Why is `UserDetailsService` in `ApplicationConfig`?

**Answer:**  
It separates user loading from `SecurityConfig` and avoids circular dependency issues.

### Q73. How is RBAC implemented?

**Answer:**  
RBAC is implemented with Spring Security authorities and method-level annotations like `@PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")`.

### Q74. Why use `@EnableMethodSecurity`?

**Answer:**  
It enables annotations like `@PreAuthorize`, allowing authorization rules directly on controller methods.

### Q75. What endpoints are public?

**Answer:**  
Public endpoints include root/static page, auth endpoints, public listing browse, Swagger UI, and API docs.

### Q76. What is a security weakness in the current project?

**Answer:**  
The main `application.yml` contains a hardcoded MySQL password and JWT secret. In production, those should be moved to environment variables or a secret manager.

### Q77. Another security weakness?

**Answer:**  
Some student-facing endpoints accept `studentId` directly and rely only on role checks. A `STUDENT` could potentially request another student's data if they know the ID. A production version should enforce object-level authorization.

### Q78. What is object-level authorization?

**Answer:**  
It checks not only the user's role but also whether the authenticated user owns or is allowed to access the specific resource being requested.

### Q79. Does JWT contain the role?

**Answer:**  
Yes. `JwtUtil.generateToken` adds a `role` claim from the user's authorities.

### Q80. Is the role claim used for authorization?

**Answer:**  
The app primarily loads the user from the database and uses the user's authorities from `UserDetails`. The role claim is present but not the only source of truth.

### Q81. How would you support token revocation?

**Answer:**  
Options include short token expiry with refresh tokens, a token blacklist, a token version field on the user, or storing refresh tokens server-side and invalidating them on logout.

### Q82. How would you improve JWT secret handling?

**Answer:**  
Read the secret from environment variables, ensure it is high entropy, rotate it safely, and avoid committing it to source control.

---

## 8. Business Logic Questions

### Q83. How does student registration work?

**Answer:**  
`StudentService.registerStudent` checks that the linked user exists, the enrollment number is unique, and no student profile already exists for that user. It then creates and saves the student entity.

### Q84. Why check duplicate enrollment number?

**Answer:**  
Enrollment number uniquely identifies a student. The service checks it before saving, and the entity also marks it unique at the database level.

### Q85. How does room creation work?

**Answer:**  
`RoomService.addRoom` first checks that room number is not already present. Then it builds a `HostelRoom` with capacity, type, fee, and facility flags, and saves it.

### Q86. How is room availability decided?

**Answer:**  
A room is available if `occupied < capacity` and status is `AVAILABLE`.

### Q87. How does allocation work?

**Answer:**  
`AllocationService.allocateRoom` checks the student exists, verifies the student does not already have an active allocation, checks the room exists and is available, records the authenticated user's email as `allocatedBy`, creates the allocation, increments room occupancy, and marks the room `FULL` if occupancy reaches capacity.

### Q88. Why is allocation transactional?

**Answer:**  
Allocation modifies both `Allocation` and `HostelRoom`. `@Transactional` ensures both changes commit together or roll back together.

### Q89. How does vacating a room work?

**Answer:**  
`vacateRoom` checks the allocation exists and is active, marks it `VACATED`, sets checkout date to today, decrements room occupancy without going below zero, and changes room status from `FULL` back to `AVAILABLE` when appropriate.

### Q90. Why use `Math.max(0, room.getOccupied() - 1)`?

**Answer:**  
It prevents the occupied count from becoming negative if data is inconsistent or vacate is called unexpectedly.

### Q91. What concurrency issue can happen in room allocation?

**Answer:**  
Two simultaneous allocation requests might both see the same room as available and overbook it. A production solution could use optimistic locking with `@Version`, pessimistic locking, or a database constraint plus retry logic.

### Q92. How are payments recorded?

**Answer:**  
`PaymentService.recordPayment` verifies the student exists, builds a payment object from the request, defaults status to `PAID` if not provided, and saves it.

### Q93. Why default payment status to `PAID`?

**Answer:**  
The endpoint is called “record payment,” so the default assumes a payment was completed. If the system also generated invoices, defaulting to `PENDING` might be better for invoice creation.

### Q94. How does total paid calculation work?

**Answer:**  
The repository sums paid payment amounts for a student. The service converts a null result to `0.0`.

### Q95. How does listing creation work?

**Answer:**  
`PGListingService.createListing` gets the authenticated user's email from `SecurityContextHolder`, loads that user as owner, builds the listing, defaults gender preference to `ANY` if missing, and saves it.

### Q96. How does listing ownership protection work?

**Answer:**  
For update and delete, the service compares the listing owner's email with the authenticated email. If they differ, it checks whether the user is `ADMIN`; otherwise it throws an exception.

### Q97. Why enforce ownership in the service and not only the controller?

**Answer:**  
The controller can check broad roles, but ownership is business-specific. Service-level checks keep object-level business rules near the operation.

### Q98. What business validations could be added?

**Answer:**  
Possible validations include:

- Check student user role is actually `STUDENT`
- Prevent allocation check-in date in the past
- Ensure checkout date is after check-in date
- Ensure `availableRooms <= totalRooms`
- Ensure payment due date and payment date are logically valid
- Prevent room capacity update below current occupancy

---

## 9. Validation and Error Handling Questions

### Q99. How is request validation implemented?

**Answer:**  
DTO request classes use Jakarta validation annotations like `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Positive`, `@Min`, `@Max`, and `@Pattern`. Controllers use `@Valid` to trigger validation.

### Q100. Give examples of validations.

**Answer:**  
Email must be valid, password must be at least six characters, student contact number must be 10 digits, room capacity must be between 1 and 6, and payment amount must be positive.

### Q101. How are validation errors returned?

**Answer:**  
`GlobalExceptionHandler.handleValidationErrors` catches `MethodArgumentNotValidException`, builds a field-to-message map, and returns an `ApiResponse` with `success=false`.

### Q102. What custom exceptions exist?

**Answer:**  
`ResourceNotFoundException` for missing resources and `StayloException` for domain/business errors.

### Q103. Why have a global exception handler?

**Answer:**  
It avoids repeating try/catch blocks in controllers and gives clients consistent error responses.

### Q104. What is one issue with the general exception handler?

**Answer:**  
It returns `"An unexpected error occurred: " + ex.getMessage()`, which could leak internal details. In production, I would log the exception server-side and return a generic message.

### Q105. How would you improve error responses?

**Answer:**  
Add timestamp, path, error code, trace ID, and structured validation details. Also avoid exposing internal exception messages for unexpected errors.

---

## 10. Testing Questions

### Q106. What testing tools are used?

**Answer:**  
JUnit 5, Mockito, AssertJ, MockMvc, Spring Boot Test, Spring Security Test, and H2.

### Q107. What does `StudentServiceTest` test?

**Answer:**  
It unit-tests student registration and retrieval logic using mocked `StudentRepository` and `UserRepository`.

### Q108. What does `AuthControllerTest` test?

**Answer:**  
It uses `@SpringBootTest` and `MockMvc` to test registration, duplicate email behavior, login success, wrong password handling, and invalid email validation.

### Q109. Why use H2 for tests?

**Answer:**  
H2 allows tests to run without a MySQL server. It makes local and CI test execution faster and more isolated.

### Q110. What does `@ActiveProfiles("test")` do?

**Answer:**  
It activates `application-test.yml`, which configures H2 and disables SQL seed initialization.

### Q111. Why use Mockito in service tests?

**Answer:**  
Mockito isolates the service from database access, allowing focused unit tests for business logic.

### Q112. Why use MockMvc?

**Answer:**  
MockMvc tests the web layer and controller behavior without starting a real server.

### Q113. What tests are missing?

**Answer:**  
Missing tests include room allocation, vacating, payment workflows, listing ownership rules, JWT filter behavior, RBAC failures, repository queries, and integration tests for protected endpoints.

### Q114. What happened when this guide ran tests locally?

**Answer:**  
The environment did not have global Maven, so the Maven wrapper was used. Main sources compiled, but test compilation failed with errors such as `package com.staylo.dto does not exist`. The source packages and compiled classes appear consistent, so this should be investigated separately, possibly as an environment/classpath or wrapper issue. In an interview, be honest: “I would reproduce locally and in GitHub Actions, inspect the Maven compiler classpath, and compare with CI.”

### Q115. How would you debug that test compile failure?

**Answer:**  
I would run `./mvnw clean test -e`, inspect `target/classes`, confirm test classpath includes main output, check CI logs, verify no unusual Maven wrapper behavior, and try a clean clone on another machine. Since main classes compiled into `target/classes/com/staylo/...`, the next step is to inspect Maven Surefire/compiler configuration and environment-specific path issues.

### Q116. How would you improve test coverage?

**Answer:**  
Add unit tests for each service's business rules, integration tests for controller endpoints with JWT auth, tests for forbidden access by role, and concurrency tests for room allocation edge cases.

---

## 11. Docker and Deployment Questions

### Q117. Explain the Dockerfile.

**Answer:**  
It uses a multi-stage build. The first stage uses `eclipse-temurin:17-jdk-alpine` to build the Maven project. The second stage uses `eclipse-temurin:17-jre-alpine` to run only the packaged jar, making the final image smaller than a full JDK image.

### Q118. Why multi-stage Docker build?

**Answer:**  
It separates build-time dependencies from runtime dependencies, reducing image size and attack surface.

### Q119. Explain `docker-compose.yml`.

**Answer:**  
It starts a MySQL 8 container and the Staylo app container. The app gets datasource config through environment variables and depends on MySQL's health check before starting.

### Q120. Why use a MySQL health check?

**Answer:**  
It prevents the app from trying to connect before MySQL is ready.

### Q121. How is configuration overridden in Docker Compose?

**Answer:**  
Environment variables such as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` override application configuration.

### Q122. How would you deploy this to production?

**Answer:**  
Build a Docker image, push it to a registry, deploy it behind a reverse proxy or load balancer, use managed MySQL, configure secrets through environment variables or a secret manager, enable HTTPS, add observability, and run migrations with Flyway or Liquibase.

### Q123. What production changes are needed?

**Answer:**  
Externalize secrets, disable `show-sql`, avoid `ddl-auto: update`, add migrations, improve logging, add object-level authorization, add monitoring, strengthen tests, and configure CORS carefully if a frontend consumes the API.

---

## 12. CI/CD Questions

### Q124. What does the GitHub Actions workflow do?

**Answer:**  
It runs on pushes and pull requests to `main` or `master`, checks out the repository, sets up JDK 17 using Temurin, caches Maven dependencies, and runs `mvn -B clean test`.

### Q125. Why run tests in CI?

**Answer:**  
CI catches build and test failures before merging and gives confidence that the project works in a clean environment.

### Q126. How would you improve the CI pipeline?

**Answer:**  
Add build artifact generation, Docker image build, dependency scanning, code coverage reporting, static analysis, and separate jobs for tests and packaging.

### Q127. What is the difference between local tests and CI tests?

**Answer:**  
CI runs in a clean, repeatable environment. Local tests can be affected by installed tools, cached dependencies, local database state, and environment variables.

---

## 13. Swagger and API Documentation Questions

### Q128. How is Swagger configured?

**Answer:**  
`SwaggerConfig` creates an `OpenAPI` bean with project info and a bearer JWT security scheme named `bearerAuth`.

### Q129. How do you test protected endpoints in Swagger?

**Answer:**  
First call `/api/auth/login`, copy the JWT, click Authorize in Swagger UI, paste `Bearer <token>`, and then call protected endpoints.

### Q130. Why annotate controllers with `@Operation` and `@Tag`?

**Answer:**  
They make generated API docs clearer by adding endpoint summaries and grouping related endpoints.

---

## 14. Code-Level Deep Dive Questions

### Q131. Why use `@RequiredArgsConstructor`?

**Answer:**  
It creates a constructor for final fields, enabling constructor-based dependency injection without boilerplate.

### Q132. Why constructor injection?

**Answer:**  
It makes dependencies explicit, supports immutability with `final`, improves testability, and avoids hidden field injection.

### Q133. Why use `@Transactional` only on some service methods?

**Answer:**  
Methods that modify multiple database records or need atomic writes use `@Transactional`, such as allocation, vacating, student registration, updates, and listing changes. Pure read methods generally do not need it.

### Q134. Would read methods benefit from `@Transactional(readOnly = true)`?

**Answer:**  
Yes. It can clarify intent and sometimes optimize persistence behavior. It can also help with lazy loading during DTO mapping.

### Q135. Why map entities manually instead of using ModelMapper or MapStruct?

**Answer:**  
Manual mapping is simple and explicit for a small project. For a larger project with many DTOs, MapStruct could reduce repetitive mapping while remaining compile-time safe.

### Q136. What is a downside of manual mapping here?

**Answer:**  
Mapping logic is repeated in each service and can become verbose. It may also trigger lazy loads during list mapping.

### Q137. Why use nested static DTO classes?

**Answer:**  
It keeps related request and response shapes together under a domain DTO class.

### Q138. Why is `roomNumber` not updated in `updateRoom`?

**Answer:**  
It appears intentionally immutable after creation because room number is unique and may be used as a stable identifier. If updates were allowed, the service should validate uniqueness.

### Q139. Why does `AllocationService` use `SecurityContextHolder`?

**Answer:**  
It captures the currently authenticated user's email as `allocatedBy`, which provides audit context for who performed the allocation.

### Q140. Why does `PGListingService` use `SecurityContextHolder`?

**Answer:**  
It determines the logged-in owner for creating listings and enforces ownership rules for updates and deletes.

### Q141. What is the danger of directly using `SecurityContextHolder` in services?

**Answer:**  
It couples services to Spring Security context and makes unit testing slightly harder. A cleaner approach could use a small `CurrentUserService`.

### Q142. Why return `ApiResponse<Void>` for delete?

**Answer:**  
The operation has no domain data to return, but still returns a success message in the standard response envelope.

---

## 15. System Design Follow-Up Questions

### Q143. How would you scale this backend?

**Answer:**  
Because JWT auth is stateless, multiple app instances can run behind a load balancer. The database would need proper indexing, connection pooling, read replicas for heavy reads, and caching for frequent public listing searches.

### Q144. How would you prevent room overbooking?

**Answer:**  
Use database-level locking or optimistic locking on `HostelRoom`, wrap allocation in a transaction, verify capacity at commit time, and possibly add constraints or a separate occupancy table.

### Q145. How would you add search for PG listings?

**Answer:**  
Start with indexed SQL filters for city, rent, type, and gender preference. For advanced text search, use MySQL full-text indexes or integrate Elasticsearch/OpenSearch.

### Q146. How would you add image upload for PG listings?

**Answer:**  
Store image files in object storage such as S3, store only metadata and URLs in the database, validate file type and size, and secure upload endpoints.

### Q147. How would you add notifications?

**Answer:**  
Use asynchronous events for actions like allocation, payment due, or overdue payment. Send email/SMS/push through a message queue or background worker.

### Q148. How would you add audit logging?

**Answer:**  
Create audit tables or use entity listeners to record who changed what, when, and from which previous value. For security-sensitive changes, log immutable audit records.

### Q149. How would you design a frontend for Staylo?

**Answer:**  
Admin and warden dashboards for room occupancy, student profiles, allocations, and payments; property owner dashboard for listings; student view for available rooms, allocation, payments, and PG search.

### Q150. How would you expose analytics?

**Answer:**  
Add endpoints for occupancy rate, revenue collected, overdue payments, available rooms by block/type, and listing availability by city. Use aggregate repository queries or reporting tables for large datasets.

---

## 16. Common Java and Spring Questions Connected to Staylo

### Q151. What is dependency injection?

**Answer:**  
Dependency injection means objects receive their dependencies from the framework rather than creating them manually. In Staylo, controllers receive services and services receive repositories through constructor injection.

### Q152. What is inversion of control?

**Answer:**  
The Spring container controls object creation, dependency wiring, lifecycle, and configuration. The application code declares dependencies, and Spring provides them.

### Q153. What is `@Service`?

**Answer:**  
It marks a class as a service-layer Spring bean. It is semantically used for business logic.

### Q154. What is `@Repository`?

**Answer:**  
It marks a data access component. Spring Data JPA repositories also get automatic implementations at runtime.

### Q155. What is `@RestController`?

**Answer:**  
It combines `@Controller` and `@ResponseBody`, meaning methods return response bodies such as JSON directly.

### Q156. What is `@RequestMapping`?

**Answer:**  
It defines the base URL path for a controller or maps methods to HTTP routes.

### Q157. What is `@PathVariable`?

**Answer:**  
It binds a value from the URL path, such as `/api/rooms/{id}`.

### Q158. What is `@RequestBody`?

**Answer:**  
It deserializes JSON request body into a Java object.

### Q159. What is `@RequestParam`?

**Answer:**  
It binds query string parameters, such as `/api/rooms/available?type=SINGLE`.

### Q160. What is `Optional` used for?

**Answer:**  
Repositories return `Optional` for queries that may not find a row. Services call `orElseThrow` to return a clear not-found error.

---

## 17. Resume Defense Questions

### Q161. What was your contribution?

**Answer:**  
Say this honestly based on your actual work. A strong answer: “I designed and implemented the backend API structure, entity model, Spring Security JWT flow, role-based controller access, service-level business rules for room allocation and listings, global error handling, test setup, Swagger docs, and Docker configuration.”

### Q162. What was the hardest part?

**Answer:**  
Room allocation is a good answer because it requires consistency across multiple tables: checking availability, preventing duplicate active allocations, updating occupancy, marking rooms full, and rolling back on failure.

### Q163. What did you learn?

**Answer:**  
I learned how to structure a Spring Boot REST API beyond CRUD: authentication, authorization, DTO design, JPA relationships, transaction boundaries, validation, testing, Swagger documentation, and Dockerized local setup.

### Q164. What would you improve if you had more time?

**Answer:**  
I would externalize secrets, improve object-level authorization, add database migrations, increase test coverage, fix/verify CI test reliability, add optimistic locking for room allocation, add pagination, and introduce better observability.

### Q165. What is one honest limitation?

**Answer:**  
The current project is a strong backend prototype, but not fully production-hardened. Secrets are in config, tests are limited, object-level authorization needs tightening, and concurrency control for allocation needs improvement.

### Q166. How would you answer if asked why credentials are committed?

**Answer:**  
“This was a resume/demo project to simplify local setup. In production I would never commit DB passwords or JWT secrets. I would use environment variables or a secret manager and provide `.env.example` with placeholder values.”

### Q167. How would you answer if asked about test failure?

**Answer:**  
“The project includes tests and CI configuration, but when I ran the wrapper in this environment, test compilation failed even though main classes compiled. I would debug by comparing local and CI classpaths, running with `-e`, checking wrapper behavior, and ensuring the build is clean in CI. I would not ignore it; build reliability is part of production readiness.”

### Q168. How do you show ownership in an interview?

**Answer:**  
Walk through a concrete flow, such as allocation: request enters `AllocationController`, role check allows admin/warden, service checks student and room, prevents duplicate active allocation, creates allocation, updates occupancy, and transaction protects consistency.

---

## 18. Scenario-Based Questions

### Q169. A student says they can see another student's payments. What do you check?

**Answer:**  
I would check authorization logic. The controller allows `STUDENT` role for `/api/payments/student/{studentId}`, but it should verify the authenticated student owns that `studentId`. I would add object-level checks in the service or a security expression.

### Q170. A room shows available but allocation fails. Why?

**Answer:**  
Possible reasons include room status changed between fetch and allocation, occupancy reached capacity, student already has an active allocation, or room is in maintenance.

### Q171. A room gets overbooked during high traffic. What happened?

**Answer:**  
Race condition. Two requests read the room before either commits occupancy update. The fix is locking or optimistic concurrency control.

### Q172. Login fails even for correct credentials. What would you inspect?

**Answer:**  
Check BCrypt password hash, user active status, `UserDetailsService`, `AuthenticationProvider`, configured password encoder, request email, and whether seed data password hash matches expected password.

### Q173. JWT validation fails. What would you inspect?

**Answer:**  
Check `Authorization` header format, token expiry, JWT secret consistency, signing algorithm, subject email, user existence, and whether `JwtAuthFilter` is registered before `UsernamePasswordAuthenticationFilter`.

### Q174. Swagger protected endpoints return 403. What should the user do?

**Answer:**  
Login, copy JWT, click Authorize, paste `Bearer <token>`, and ensure the logged-in user has the required role.

### Q175. Data is duplicated on app startup. What do you check?

**Answer:**  
Check `data.sql`, `spring.sql.init.mode`, `ddl-auto`, and whether seed inserts use idempotent statements like `INSERT IGNORE`.

### Q176. App cannot connect to MySQL in Docker. What do you check?

**Answer:**  
Check MySQL container health, network, service name `mysql`, datasource URL, username/password, exposed ports, and whether app waits for the database health check.

---

## 19. Advanced Improvement Answers

### Q177. How would you add pagination?

**Answer:**  
Use Spring Data `Pageable` in repository methods and return `Page<T>` or a custom paginated response with content, page number, size, total elements, and total pages.

### Q178. How would you add sorting?

**Answer:**  
Accept `sortBy` and `direction` query parameters or use Spring's `Sort`/`Pageable` support.

### Q179. How would you handle API versioning?

**Answer:**  
Use URL versioning like `/api/v1/...` or header-based versioning. URL versioning is simple and visible for public APIs.

### Q180. How would you handle database migrations?

**Answer:**  
Use Flyway or Liquibase and change `ddl-auto` to `validate` or `none` in production.

### Q181. How would you add refresh tokens?

**Answer:**  
Issue short-lived access tokens and longer-lived refresh tokens stored securely. Store refresh tokens or hashes in the database, rotate them, and revoke them on logout.

### Q182. How would you protect against brute-force login?

**Answer:**  
Add rate limiting, account lockout after repeated failures, CAPTCHA for suspicious attempts, audit logs, and alerts.

### Q183. How would you add role hierarchy?

**Answer:**  
Configure Spring Security role hierarchy so `ADMIN` can automatically inherit permissions from lower roles, reducing repeated `hasAnyRole` declarations.

### Q184. How would you add caching?

**Answer:**  
Cache public listings or available room queries using Spring Cache with Redis, and invalidate cache when listings or rooms change.

### Q185. How would you add observability?

**Answer:**  
Use structured logs, request IDs, Spring Boot Actuator, metrics via Micrometer/Prometheus, dashboards in Grafana, and centralized logs.

### Q186. How would you secure Swagger in production?

**Answer:**  
Disable it in production or restrict it behind authentication/VPN/internal network access.

### Q187. How would you handle file uploads?

**Answer:**  
Use object storage, validate file MIME and size, generate safe object keys, scan files if needed, and store metadata in DB.

### Q188. How would you handle soft deletes?

**Answer:**  
Add an `isDeleted` flag and filter queries to hide deleted records, or use Hibernate filters. Useful for preserving audit history.

### Q189. How would you model amenities better?

**Answer:**  
Instead of comma-separated strings, use a separate `Amenity` entity or enum collection so amenities are queryable and normalized.

### Q190. How would you handle monetary values better?

**Answer:**  
Use `BigDecimal` instead of `Double` for fees and payments to avoid floating-point precision issues.

### Q191. How would you handle time zones?

**Answer:**  
Use `Instant` or offset-aware types for timestamps, store in UTC, and format for users at the edge.

### Q192. How would you add email verification?

**Answer:**  
Generate a verification token at registration, email it to the user, store expiry in DB, and activate account only after verification.

### Q193. How would you add payment gateway integration?

**Answer:**  
Create payment orders, redirect or invoke the gateway, handle webhooks securely, verify signatures, and update payment status idempotently.

### Q194. How would you make webhook handling idempotent?

**Answer:**  
Store gateway event IDs or transaction IDs with a unique constraint and ignore already processed events.

### Q195. How would you protect update/delete listing endpoints better?

**Answer:**  
Keep role checks at controller level and owner/admin checks at service level, add tests for non-owner access, and return 403 instead of generic bad request for authorization failures.

---

## 20. Questions You Should Ask the Interviewer

### Q196. If this were your production system, would you prefer optimistic or pessimistic locking for room allocation?

Use this when the interviewer asks about concurrency. It shows you understand tradeoffs.

### Q197. Would your team prefer DTO mapping manually or using MapStruct?

Use this when discussing maintainability.

### Q198. How strict are your production standards around API versioning and backward compatibility?

Use this when discussing API evolution.

### Q199. What observability stack does your team use for Spring Boot services?

Use this when discussing deployment and operations.

### Q200. How do you usually separate authentication, authorization, and object ownership checks?

Use this when discussing security architecture.

---

## 21. Most Important Answers to Memorize

1. **Project pitch:** Staylo is a Spring Boot REST backend for hostel and accommodation workflows with JWT auth, RBAC, JPA, DTOs, validation, Swagger, Docker, and CI.

2. **Architecture:** Controller handles HTTP, service handles business logic and transactions, repository handles data access, entities model tables, DTOs shape API payloads.

3. **Security:** Login uses `AuthenticationManager`; passwords use BCrypt; JWT is generated by `JwtUtil`; `JwtAuthFilter` validates bearer tokens; `@PreAuthorize` enforces roles.

4. **Allocation flow:** Check student, check active allocation, check room availability, create allocation, increment occupancy, mark room full if needed, all inside a transaction.

5. **Production improvements:** Externalize secrets, use migrations, add object-level authorization, add concurrency control, use `BigDecimal` for money, add pagination, improve tests, add observability.

6. **Honest limitation:** It is a strong resume backend prototype, but not fully production-hardened yet. Knowing the gaps and how to fix them is a strength.

