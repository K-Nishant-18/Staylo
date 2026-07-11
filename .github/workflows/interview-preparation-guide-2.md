# Staylo Complete Interview Preparation Guide (Detailed Edition)

Repository reviewed: [K-Nishant-18/Staylo](file:///c:/Users/itsni/Desktop/GitHub%20Projects/Staylo)

This guide provides a detailed, comprehensive walkthrough of 200 interview questions designed to prepare you for senior or mid-level backend engineering interviews. The answers are tailored specifically to the Staylo codebase.

---

## 1. Fast Project Pitch

### Q1. Explain Staylo in 60 seconds.
**Answer:**  
Staylo is a Java 17, Spring Boot 3.2.5 REST backend designed to centralize and automate student hostel and rental accommodation management. It exposes endpoints for authentication, student profile tracking, room inventory, room allocation, fee recording, and off-campus PG listings. 
* It uses **Spring Security 6.x** and stateless **JWT** bearer tokens for authentication.
* Role-Based Access Control (RBAC) is enforced at the method level using `@PreAuthorize` across four roles: `ADMIN`, `WARDEN`, `PROPERTY_OWNER`, and `STUDENT`.
* Data persistence is handled via **Spring Data JPA** targeting a **MySQL 8** database.
* The application runs locally in a containerized environment via **Docker Compose** (including a database healthcheck) and features automated build validation with **GitHub Actions**.

### Q2. What problem does Staylo solve?
**Answer:**  
Traditional accommodation workflows are highly fragmented. Wardens track occupancies on spreadsheets, students seek off-campus rentals manually when hostels are full, owners lack a platform to reach students, and payments aren't connected to room tenancies. Staylo unifies these components:
1. **Centralizes Admin Operations:** Wardens and Admins manage room inventory, room conditions, and room check-in/check-out allocations.
2. **Exposes Rental Market:** Connects property owners with students by allowing owners to create listings, and students to browse and search listings.
3. **Connects Tenancy to Billing:** Logs student payments, calculates total balances paid, and exposes overdue bills.

### Q3. What are the main modules?
**Answer:**  
Staylo contains the following modules:
* **Identity & Authentication:** Handles registration, credentials verification, password encryption, and JWT token issuance.
* **Student Directory:** Manages profiles, enrollment numbers, parent/guardian contact info, and courses.
* **Hostel Inventory:** Tracks room number, block, floor, AC/attached bathroom amenities, and room status.
* **Room Allocation:** Handles student room check-in and check-out transactions, updates room capacities, and records administrators.
* **Billing & Payments:** Tracks invoice statuses (`PAID`, `PENDING`, `OVERDUE`) and sums payment streams.
* **PG Listings:** Allows owners to list flats, rooms, or hostels, and public users to browse and filter listings.

### Q4. Why did you choose Spring Boot?
**Answer:**  
Spring Boot is the standard for production-grade Java enterprise applications due to:
* **Embedded Server:** Integrates Tomcat natively, removing external servlet container configurations.
* **Dependency Injection:** Promotes loose coupling and simplifies unit testing.
* **Spring MVC:** Makes building RESTful web services simple using annotations like `@RestController` and `@GetMapping`.
* **Spring Data JPA:** Automates database repository queries, eliminating JDBC boilerplate.
* **Security Autoconfig:** Simplifies securing APIs and configuring JWT validation layers.

### Q5. What makes this project resume-worthy?
**Answer:**  
It goes beyond simple CRUD operations to demonstrate real-world backend engineering practices:
* **Stateless Token Authentication:** Integrates JWT tokens with custom request filters.
* **Database Transaction Management:** Protects relational updates across tables using `@Transactional`.
* **Containerization:** Packages compile and runtime environments using multi-stage Docker builds.
* **CI/CD Automation:** Automates compiling and testing code via GitHub Actions.
* **API Documentation:** Auto-generates OpenAPI contracts using Swagger UI.

---

## 2. Architecture Questions

### Q6. Describe the architecture.
**Answer:**  
The project uses a standard **Layered (3-Tier) Architecture**:
```
[Client Request] ──► [Controller Layer] ──► [Service Layer] ──► [Repository Layer] ──► [MySQL Database]
```
1. **Controller Layer (Presentation):** Validates input models (`@Valid`), enforces method-level roles, and manages HTTP responses.
2. **Service Layer (Business Logic):** Validates business rules, manages transaction boundaries (`@Transactional`), and maps entity objects to DTOs.
3. **Repository Layer (Data Access):** Interacts with MySQL using Spring Data JPA.
4. **Entity Layer (Domain):** Represents database tables and maps relationships.

### Q7. Why use a layered architecture?
**Answer:**  
Layered architecture separates responsibilities:
* **Separation of Concerns:** Changes in database schemas only affect entities and repositories, not controllers.
* **Testability:** Business services can be tested in isolation by mocking repositories.
* **Reusability:** Multiple controllers can reuse the same service methods.
* **Maintainability:** Team members can work on different layers without overriding each other's changes.

### Q8. What are the packages in the backend?
**Answer:**  
* `com.staylo.config`: Configures UserDetailsService, Security Filter Chains, and Swagger OpenAPI schemas.
* `com.staylo.controller`: Exposes REST routes and maps payloads to service logic.
* `com.staylo.dto`: Groups nested request and response payloads.
* `com.staylo.entity`: Declares JPA entity classes mapping database tables.
* `com.staylo.exception`: Handles exceptions globally.
* `com.staylo.repository`: Declares interface proxies for database operations.
* `com.staylo.security`: Declares JWT generation, claims parsing, and request interception filters.
* `com.staylo.service`: Manages transactions and core business logic.

### Q9. What is the responsibility of controllers?
**Answer:**  
Controllers act as the gateway for external requests:
* **Route Mapping:** Map HTTP verbs and endpoints using annotations like `@PostMapping` or `@GetMapping`.
* **Input Validation:** Enforces constraints (e.g., non-empty strings) using `@Valid`.
* **Role Check Interception:** Restricts access using `@PreAuthorize`.
* **Payload Deserialization:** Convers JSON inputs to DTO objects.
* **HTTP Response Management:** Wraps outputs in the standard `ApiResponse` envelope and sets HTTP status codes using `ResponseEntity`.

### Q10. What is the responsibility of services?
**Answer:**  
Services run the core business rules:
* **Validation Checks:** Verifies room capacities and student room states before allocation.
* **Security & Ownership verification:** Compares authenticated user tokens against listing owner emails.
* **Transactional boundaries:** Ensures multi-table database operations commit or roll back together.
* **Mapping:** Converts entities into response DTO objects.

### Q11. What is the responsibility of repositories?
**Answer:**  
Repositories translate Java method calls into database queries:
* **CRUD Operations:** Extends `JpaRepository` to provide standard save, find, and delete methods out of the box.
* **Derived Query Methods:** Autogenerates database queries from method names (e.g., `findByEnrollmentNo`).
* **Custom Queries:** Executes custom JPQL or native SQL queries using `@Query`.

### Q12. Why use DTOs?
**Answer:**  
DTOs decouple internal database structures from public APIs:
* **Security:** Prevents clients from updating database fields like password hashes or user roles.
* **Data Masking:** Hides sensitive fields like passwords from API responses.
* **API Stability:** Database columns can be renamed without breaking the API contract.
* **Performance:** Avoids lazy-loading issues when serializing entities.

### Q13. What is the role of `ApiResponse<T>`?
**Answer:**  
`ApiResponse<T>` standardizes all JSON responses returned by the API:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```
This gives front-end clients a predictable response contract, making it easier to handle success and error states consistently.

### Q14. What are the main design patterns used?
**Answer:**  
* **Dependency Injection (DI):** Managed by Spring's IoC container to wire beans.
* **Repository Pattern:** Implemented by Spring Data JPA to abstract database access.
* **Data Transfer Object (DTO):** Isolates database models from public REST payloads.
* **Builder Pattern:** Generated by Lombok's `@Builder` annotation to construct immutable DTO and entity objects.
* **Filter (Chain of Responsibility):** Used by `JwtAuthFilter` to intercept and validate requests.
* **Global Exception Handler (AOP):** Implemented by `@RestControllerAdvice` to intercept exceptions across controllers.

### Q15. How does a request flow through the app?
**Answer:**  
1. **Filter Chain:** `JwtAuthFilter` intercepts the request, reads the token, and authenticates the user in `SecurityContextHolder`.
2. **Authorization Interceptor:** Spring Security verifies if the user's role matches the controller's `@PreAuthorize` rules.
3. **Controller:** Deserializes and validates the request body, and calls the service layer.
4. **Service:** Runs the business logic and queries database records.
5. **Repository:** Executes database queries using Hibernate.
6. **Response:** The service maps database entities to DTOs, and the controller wraps the results in an `ApiResponse` and returns it as a JSON payload.

---

## 3. Technology Stack Questions

### Q16. What Java version does the project use?
**Answer:**  
The project uses **Java 17**, which is a Long-Term Support (LTS) release. This allows the application to benefit from modern language features like switch expressions, text blocks, and record types.

### Q17. What Spring Boot version is used?
**Answer:**  
The project uses **Spring Boot 3.2.5**. This version requires Java 17 as a minimum, uses Jakarta EE namespace specifications, and integrates Spring Security 6.x.

### Q18. What database is configured?
**Answer:**  
* **Production/Dev:** **MySQL 8.0** is used, configured via `application.yml` and run in a containerized environment.
* **Testing:** **H2 In-Memory Database** is used, configured via `application-test.yml` to keep tests isolated and repeatable.

### Q19. What dependencies are important?
**Answer:**  
* `spring-boot-starter-web`: Builds RESTful web services using Spring MVC.
* `spring-boot-starter-security`: Secures endpoints and manages user roles.
* `spring-boot-starter-data-jpa`: Manages database transactions using Hibernate.
* `spring-boot-starter-validation`: Validates request bodies.
* `mysql-connector-j`: The database driver for MySQL.
* `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: Manages creating, parsing, and signing JWT tokens.
* `lombok`: Generates getters, setters, and constructors.
* `springdoc-openapi-starter-webmvc-ui`: Autogenerates API documentation.
* `h2`: An in-memory database used for running tests.

### Q20. Why use Lombok?
**Answer:**  
Lombok removes boilerplate code at compile time:
* `@Getter` and `@Setter` generate accessor methods automatically.
* `@NoArgsConstructor` and `@AllArgsConstructor` generate constructors automatically.
* `@Builder` implements the builder pattern for constructing objects.
* `@RequiredArgsConstructor` generates constructors for final fields, enabling constructor injection.

### Q21. What are the risks of Lombok?
**Answer:**  
* **IDE Dependency:** Requires installing specific IDE plugins to compile code without errors.
* **Hides Code Logic:** Hiding getter and setter methods can make debugging stack traces slightly less direct.
* **Hidden Costs:** Annotations like `@EqualsAndHashCode` or `@ToString` on lazy-loaded JPA entities can trigger unexpected database queries, causing performance issues.

### Q22. Why use SpringDoc OpenAPI?
**Answer:**  
It replaces older libraries like Springfox to support OpenAPI 3 specifications. It scans code annotations automatically to generate interactive API documentation at `/swagger-ui.html` on startup.

---

## 4. Domain Model Questions

### Q23. What are the main entities?
**Answer:**  
* `User`: Stores credentials, emails, passwords, roles, and account statuses.
* `Student`: Stores student profile metadata, linked 1:1 to a `User`.
* `HostelRoom`: Tracks room number, block, floor, capacity, occupancy, and status.
* `Allocation`: Maps a `Student` to a `HostelRoom` for a specific date range.
* `Payment`: Logs payment amounts, invoice statuses, and due dates for a `Student`.
* `PGListing`: Stores off-campus housing advertisements created by a `User` (owner).

### Q24. Explain the `User` entity.
**Answer:**  
The `User` entity represents login accounts. It contains fields for name, email, password, role, creation timestamp, and active status. It implements Spring Security's `UserDetails` interface, allowing it to serve as the principal user object in the security context.

### Q25. Why does `User` implement `UserDetails`?
**Answer:**  
`UserDetails` is Spring Security's core user interface. By implementing it, our custom `User` class can be returned directly by `UserDetailsService`. This lets Spring Security read the user's username (email), password (hash), roles, and account state natively without requiring custom mapping layers.

### Q26. What roles exist?
**Answer:**  
Staylo defines four user roles:
* `ADMIN`: Has full system administration access.
* `WARDEN`: Manages rooms, student profiles, and room allocations.
* `PROPERTY_OWNER`: Creates and manages off-campus housing listings.
* `STUDENT`: Can search rooms, browse listings, and view payment histories.

### Q27. How are authorities generated?
**Answer:**  
Inside the `User` entity, the roles are converted to Spring Security authorities by prefixing the role name with `ROLE_`:
```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
}
```
This role prefix matches Spring Security's authorization checks when using `@PreAuthorize("hasRole('ADMIN')")`.

### Q28. Explain the `Student` entity.
**Answer:**  
The `Student` entity stores profile metadata for students. It contains fields for enrollment number, course name, study year, contact number, parent/guardian name, and guardian contact details. It is linked to a parent `User` record via a unique one-to-one foreign key relationship.

### Q29. Why separate `User` and `Student`?
**Answer:**  
This separation decouples credentials from profile information.
* **Separation of Concerns:** The `User` entity only handles credentials and login details. The `Student` entity only handles student profile metadata.
* **Flexibility:** This allows non-student users (like wardens or property owners) to log in using the same authentication flows, without needing student-specific columns (like enrollment numbers) in their database records.

### Q30. Explain the `HostelRoom` entity.
**Answer:**  
`HostelRoom` represents a physical room. It contains fields for room number, block name, floor number, room type (Single, Double, Triple), maximum capacity, current occupancy, fee, AC/attached bathroom flags, and room status. It contains an `isAvailable()` helper method to check if the room can accept new allocations.

### Q31. What room statuses exist?
**Answer:**  
* `AVAILABLE`: The room is active and has vacant beds.
* `FULL`: The room's occupancy has reached its capacity.
* `MAINTENANCE`: The room is offline for repairs, preventing new allocations.

### Q32. What room types exist?
**Answer:**  
* `SINGLE`: Maximum capacity of 1.
* `DOUBLE`: Maximum capacity of 2.
* `TRIPLE`: Maximum capacity of 3.

### Q33. Explain the `Allocation` entity.
**Answer:**  
`Allocation` maps a student to a room. It stores the check-in date, optional check-out date, allocation status, and the email of the administrator who performed the allocation. It contains many-to-one relationships to both the `Student` and the `HostelRoom`.

### Q34. Explain the `Payment` entity.
**Answer:**  
`Payment` tracks financial transactions. It contains fields for amount, payment type (e.g., hostel fee, mess fee), payment status (`PAID`, `PENDING`, `OVERDUE`), transaction ID, payment mode, and remarks. It is linked to a `Student` via a many-to-one relationship.

### Q35. Explain the `PGListing` entity.
**Answer:**  
`PGListing` represents off-campus student housing listings. It contains fields for listing title, address details, city, monthly rent, listing type (e.g., PG, Flat), total rooms, available rooms, contact number, gender preference, and amenities. It is linked to a `User` (acting as the owner) via a many-to-one relationship.

### Q36. What listing types exist?
**Answer:**  
* `PG`: Paying Guest shared accommodation.
* `FLAT`: Independent apartment rentals.
* `ROOM`: Single private room rentals in shared flats.
* `HOSTEL`: Private commercial hostels.

### Q37. What gender preferences exist?
**Answer:**  
* `MALE`: Only accepts male tenants.
* `FEMALE`: Only accepts female tenants.
* `ANY`: Co-ed or accepts all tenants.

---

## 5. JPA and Database Questions

### Q38. What JPA relationships are used?
**Answer:**  
* **One-to-One:** `Student` to `User` (each student profile maps to exactly one login account).
* **Many-to-One:**
  * `Allocation` to `Student` and `HostelRoom` (a student/room can have multiple allocations over time).
  * `Payment` to `Student` (a student can have multiple payments).
  * `PGListing` to `User` (a property owner can have multiple listings).

### Q39. Why use `@ManyToOne(fetch = FetchType.LAZY)`?
**Answer:**  
In JPA, `@ManyToOne` associations load eagerly by default, which can cause performance issues. Using `FetchType.LAZY` tells the persistence framework to fetch related entities from the database only when they are accessed in code. This avoids fetching unnecessary relational data, optimizing application performance.

### Q40. What is the risk with lazy loading?
**Answer:**  
* **LazyInitializationException:** If a lazy-loaded property is accessed outside an active database transaction (meaning after the entity manager has closed), Hibernate will throw a `LazyInitializationException`.
* **N+1 Queries:** If you fetch a list of entities and loop through them to access lazy-loaded fields, the application will run one query to fetch the list, followed by N separate queries to fetch the lazy-loaded details for each record, degrading performance.

### Q41. Does this project risk N+1 queries?
**Answer:**  
Yes. For example, retrieving all payments in `PaymentService.getAllPayments()` fetches the payment records in one query, but mapping them to response DTOs accesses `payment.getStudent().getUser().getName()`. This triggers additional database queries to load the associated student and user details for each payment record. This can be resolved using **Fetch Joins** in custom repository queries.

### Q42. What is `@PrePersist` used for?
**Answer:**  
`@PrePersist` defines a callback method that executes before a new entity is saved to the database. In Staylo, it is used to set the initial creation timestamp (`createdAt`) automatically on new records.

### Q43. What is `@PreUpdate` used for?
**Answer:**  
`@PreUpdate` defines a callback method that executes before an existing entity is updated in the database. In Staylo, it is used to update the modification timestamp (`updatedAt`) automatically on modified records.

### Q44. Why use `EnumType.STRING`?
**Answer:**  
By default, JPA stores enums as integers (ordinals) matching their declaration order. If you modify the enum class by inserting a new value in the middle, all existing database values will become mismatched. Using `@Enumerated(EnumType.STRING)` stores the enum names as readable strings in the database, protecting data integrity.

### Q45. What is `ddl-auto: update`?
**Answer:**  
It tells Hibernate to compare your JPA entities against the database schema on startup and update tables automatically to match your code. While convenient for local development, it should be disabled in production because automatic updates can lock tables or cause data loss.

### Q46. Why is MySQL configured with `createDatabaseIfNotExist=true`?
**Answer:**  
It simplifies local setup for developers by telling the database driver to create the target schema automatically on startup if it doesn't already exist in MySQL.

### Q47. What does `data.sql` do?
**Answer:**  
It contains database seed data that runs automatically on startup if configured. In Staylo, it pre-loads default test users (Admin, Warden, Property Owner, Student) and several test hostel rooms into the database.

### Q48. Why use `INSERT IGNORE` in seed data?
**Answer:**  
Without `IGNORE`, restarting the application would try to insert the seed data again, triggering duplicate primary key violations and causing startup errors. `INSERT IGNORE` skips the insert if a record with the same primary key already exists.

### Q49. What database indexes would you add?
**Answer:**  
To optimize query performance, I would add indexes to fields used frequently in lookup and filter queries:
* `users(email)`: For fast lookups during login.
* `students(enrollment_no)` and `students(user_id)`: For fast profile lookups.
* `hostel_rooms(room_number)`: For fast room inventory lookups.
* `allocations(student_id, status)`: For fast active allocation verification.
* `payments(student_id)`: For mapping student billing histories.
* `pg_listings(city, monthly_rent)`: For optimizing off-campus housing searches.

### Q50. Why use repositories instead of writing JDBC manually?
**Answer:**  
* **Reduces Boilerplate:** Eliminates SQL query writing, connection management, and mapping results manually.
* **Auto-generated Queries:** Generates database queries automatically from method names.
* **Database Agnostic:** Allows changing database dialects without rewriting SQL queries.
* **Standard Pagination & Sorting:** Integrates paging and sorting capabilities out of the box.

### Q51. Explain one custom JPQL query.
**Answer:**  
In `HostelRoomRepository`:
```java
@Query("SELECT r FROM HostelRoom r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE'")
List<HostelRoom> findAvailableRooms();
```
This JPQL query fetches all room entities where the current occupancy is less than the capacity and the status is active, returning available rooms.

### Q52. Why does `PaymentRepository.totalPaidByStudent` return `Double` and not primitive `double`?
**Answer:**  
If a student has no payment records in the database, the SQL `SUM` function will return `null`. A primitive `double` cannot store null values and would throw a NullPointerException. Using the wrapper object `Double` allows storing null values safely, which the service layer maps to `0.0`.

---

## 6. REST API Questions

### Q53. What are the auth endpoints?
**Answer:**  
* `POST /api/auth/register`: Registers a new user account with a specified role.
* `POST /api/auth/login`: Authenticates user credentials and returns a stateless JWT bearer token.

### Q54. What are the student endpoints?
**Answer:**  
* `POST /api/students`: Registers a new student profile linked to a user.
* `GET /api/students`: Retrieves all registered student profiles.
* `GET /api/students/{id}`: Fetches a student profile by its database ID.
* `GET /api/students/enrollment/{enrollmentNo}`: Fetches a student profile by their enrollment number.
* `PUT /api/students/{id}`: Updates profile details for a student.
* `DELETE /api/students/{id}`: Deletes a student profile.

### Q55. What are the room endpoints?
**Answer:**  
* `POST /api/rooms`: Creates a new hostel room.
* `GET /api/rooms`: Retrieves all hostel rooms.
* `GET /api/rooms/available`: Fetches all available rooms.
* `GET /api/rooms/{id}`: Fetches a room by its database ID.
* `PUT /api/rooms/{id}`: Updates room configurations.
* `PATCH /api/rooms/{id}/status`: Updates a room's active status.

### Q56. What are the allocation endpoints?
**Answer:**  
* `POST /api/allocations`: Creates a new room allocation record.
* `GET /api/allocations`: Retrieves all room allocation records.
* `GET /api/allocations/active`: Fetches all active allocations.
* `GET /api/allocations/{id}`: Fetches an allocation record by its database ID.
* `GET /api/allocations/student/{studentId}/active`: Fetches a student's current active allocation.
* `PUT /api/allocations/{id}/vacate`: Marks an allocation as vacated, releasing the bed.

### Q57. What are the payment endpoints?
**Answer:**  
* `POST /api/payments`: Records a new payment transaction.
* `GET /api/payments`: Retrieves all payment logs.
* `GET /api/payments/{id}`: Fetches a payment record by its database ID.
* `GET /api/payments/student/{studentId}`: Fetches all payments recorded for a student.
* `GET /api/payments/student/{studentId}/total-paid`: Calculates the total payments made by a student.
* `GET /api/payments/overdue`: Fetches all overdue payments.
* `PATCH /api/payments/{id}/status`: Updates a payment's status.

### Q58. What are the listing endpoints?
**Answer:**  
* `GET /api/listings`: Public route to search and filter active housing listings.
* `GET /api/listings/{id}`: Public route to view listing details by ID.
* `POST /api/listings`: Creates a new housing listing.
* `GET /api/listings/my`: Fetches listings created by the logged-in owner.
* `GET /api/listings/all`: Fetches all listings (Admin only).
* `PUT /api/listings/{id}`: Updates listing details.
* `DELETE /api/listings/{id}`: Deletes a listing.

### Q59. Why are some listing endpoints public?
**Answer:**  
Public endpoints allow anyone to browse available accommodation without registering, matching real-world search portal behaviors. Modifying listings (creation, updates, deletion) requires authenticated ownership checks.

### Q60. Why use `@RequestParam` for filters?
**Answer:**  
Filters (like search query strings, maximum rent thresholds, or room types) are optional search criteria. `@RequestParam` allows these parameters to be optional in URLs (e.g., `/api/listings?city=Delhi&maxRent=5000`), keeping REST endpoints clean.

### Q61. What HTTP status codes are used?
**Answer:**  
* `200 OK`: Successful read or update operations.
* `201 Created`: Successful creation operations.
* `400 Bad Request`: Input validation errors or business rule violations.
* `401 Unauthorized`: Invalid credentials or missing authentication headers.
* `403 Forbidden`: Authenticated users trying to access unauthorized routes.
* `404 Not Found`: Requesting a resource that does not exist in the database.
* `500 Internal Server Error`: Unexpected system errors.

### Q62. Why use `ResponseEntity`?
**Answer:**  
`ResponseEntity` represents the entire HTTP response. It allows you to configure headers, the response body, and the HTTP status code explicitly within controller methods.

### Q63. Why use nested `Request` and `Response` DTO classes?
**Answer:**  
This keeps related request and response payload configurations organized within a single parent DTO namespace (e.g., `StudentDTO`), improving code readability.

---

## 7. Security Questions

### Q64. How does JWT authentication work in this project?
**Answer:**  
1. **Login:** A user calls `/api/auth/login` with their credentials.
2. **Token Generation:** The server authenticates the credentials and generates a signed JWT token containing user email and role details.
3. **Storage:** The client saves this token locally.
4. **Subsequent Calls:** The client attaches the token to the header of subsequent API requests: `Authorization: Bearer <token>`.
5. **Validation:** `JwtAuthFilter` intercepts the request, validates the token signature, and registers the user in the security context.

### Q65. Why use stateless sessions?
**Answer:**  
Stateless sessions remove the need to store session state on the server, improving horizontal scalability. Any application node can process incoming requests if it has the token signing key.

### Q66. Where is stateless behavior configured?
**Answer:**  
In `SecurityConfig.java`, by setting the session creation policy to stateless in the `SecurityFilterChain`:
```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

### Q67. Why disable CSRF?
**Answer:**  
Cross-Site Request Forgery attacks target cookie-based sessions. Since our REST API is stateless and authenticates using JWT tokens in headers rather than browser cookies, CSRF protection is disabled.

### Q68. How is password hashing handled?
**Answer:**  
Passwords are encrypted using BCrypt via Spring's `PasswordEncoder` bean. The registration service encodes passwords before saving them, ensuring passwords are never stored in plain text:
```java
passwordEncoder.encode(request.getPassword())
```

### Q69. Why BCrypt?
**Answer:**  
BCrypt is a slow, adaptive hashing algorithm that uses a configurable work factor (rounds). It includes a unique salt automatically for each hash, protecting database passwords against brute-force and rainbow table attacks.

### Q70. What does `AuthenticationManager` do in login?
**Answer:**  
It authenticates user credentials. It delegates verification to `DaoAuthenticationProvider`, which loads user details from the database and compares the password hash against the input using BCrypt.

### Q71. What does `UserDetailsService` do?
**Answer:**  
It is a core Spring Security interface used to load user accounts from the database using their username (email):
```java
UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
```

### Q72. Why is `UserDetailsService` in `ApplicationConfig`?
**Answer:**  
Moving the database-backed `UserDetailsService` bean definition to a separate `ApplicationConfig` class prevents circular dependencies in `SecurityConfig` during startup.

### Q73. How is RBAC implemented?
**Answer:**  
Role-Based Access Control is enforced by assigning roles (`ADMIN`, `WARDEN`) to users and annotating controller endpoints with method-level role checks:
```java
@PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
```

### Q74. Why use `@EnableMethodSecurity`?
**Answer:**  
It enables Spring Security's method-level security processing, allowing the use of annotations like `@PreAuthorize` and `@PostAuthorize` directly on controller methods.

### Q75. What endpoints are public?
**Answer:**  
* `/` and `/index.html`: Welcome page and static portal assets.
* `/api/auth/**`: Register and login routes.
* `/api/listings`: Public listing searches.
* `/swagger-ui/**`, `/v3/api-docs/**`: API documentation routes.

### Q76. What is a security weakness in the current project?
**Answer:**  
The application's signing secret (`jwt.secret`) and database password are committed in plain text inside `application.yml`, which is a security risk. In production, these should be loaded from environment variables or a secret vault.

### Q77. Another security weakness?
**Answer:**  
The student profile retrieval route (`GET /api/students/{id}`) allows any authenticated student to fetch other student profiles by passing different ID parameters (IDOR vulnerability). The service layer needs object-level validation checks.

### Q78. What is object-level authorization?
**Answer:**  
It checks if the authenticated user owns or has permission to access the specific resource record being requested, rather than only verifying their broad role permissions.

### Q79. Does JWT contain the role?
**Answer:**  
Yes. The token generation logic adds the user's role string as a custom claim named `role` in the JWT payload:
```java
claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
```

### Q80. Is the role claim used for authorization?
**Answer:**  
In Staylo's implementation, the JWT filter extracts the username (email) and re-loads the user's roles from the database using `UserDetailsService` for authorization, treating the database as the source of truth.

### Q81. How would you support token revocation?
**Answer:**  
* **Short Expirations:** Keep JWT validity short (e.g. 15 minutes) and issue refresh tokens.
* **Token Blacklisting:** Store revoked token hashes in a fast database like **Redis** until their expiration time, checking incoming tokens against this blacklist.
* **Database Checks:** Maintain a `tokenVersion` counter on user records. Revoking tokens updates this count, invalidating old tokens.

### Q82. How would you improve JWT secret handling?
**Answer:**  
I would read the secret value from environment variables (`${JWT_SECRET}`) on startup, ensuring the key is high-entropy (at least 256-bit) and never committed to source control.

---

## 8. Business Logic Questions

### Q83. How does student registration work?
**Answer:**  
The register service checks if the target `User` account exists, verifies the enrollment number is unique in the database, checks if the user already has a student profile, creates the profile, and saves it.

### Q84. Why check duplicate enrollment number?
**Answer:**  
Enrollment numbers uniquely identify students. Verifying uniqueness in the service layer prevents duplicate profile registrations. The database also enforces this using a unique constraint on the column.

### Q85. How does room creation work?
**Answer:**  
The room service verifies the room number is unique, sets capacity and type parameters, defaults occupancy count to 0, sets status to `AVAILABLE`, and saves the room.

### Q86. How is room availability decided?
**Answer:**  
A room is available if its active status is `AVAILABLE` and its current occupancy is less than its maximum capacity.

### Q87. How does allocation work?
**Answer:**  
1. Checks if the student and room exist in the database.
2. Verifies the student doesn't have an active allocation.
3. Verifies the room is available.
4. Records the logged-in administrator's email.
5. Increments room occupancy. If the room is now full, updates its status to `FULL`.
6. Saves the room and allocation records.

### Q88. Why is allocation transactional?
**Answer:**  
Allocation updates two separate database tables (creating an allocation record and updating room occupancy). Annotation with `@Transactional` ensures both operations commit successfully or roll back on error, protecting database consistency.

### Q89. How does vacating a room work?
**Answer:**  
It checks if the allocation is active, sets the status to `VACATED`, sets the checkout date to today, and decrements room occupancy. If the room status was `FULL`, it updates the status back to `AVAILABLE`.

### Q90. Why use `Math.max(0, room.getOccupied() - 1)`?
**Answer:**  
It prevents the database room occupancy count from dropping below zero due to manual database edits or out-of-order API calls.

### Q91. What concurrency issue can happen in room allocation?
**Answer:**  
If two admins attempt to allocate the last bed in a room simultaneously, both threads could see the room as available and allocate it, overbooking the room. This can be resolved using database locking or optimistic concurrency control.

### Q92. How are payments recorded?
**Answer:**  
The service verifies the student profile exists, maps the request to a `Payment` entity, defaults the status to `PAID` if not specified, and saves the transaction record.

### Q93. Why default payment status to `PAID`?
**Answer:**  
The endpoint records completed transactions. If the system was modified to issue invoices, the default status would be updated to `PENDING`.

### Q94. How does total paid calculation work?
**Answer:**  
The repository executes an aggregate `SUM` query over all paid payments for a student. The service maps a null result (indicating no payments) to `0.0`.

### Q95. How does listing creation work?
**Answer:**  
The service reads the authenticated user's email, verifies they exist in the database, sets them as the owner, maps the request fields, and saves the listing.

### Q96. How does listing ownership protection work?
**Answer:**  
Before updates or deletions, the service compares the owner's email on the listing record against the authenticated user's email. If they differ and the user is not an `ADMIN`, the action is blocked.

### Q97. Why enforce ownership in the service and not only the controller?
**Answer:**  
Ownership checks require database lookups to compare records. Enforcing these rules in the service layer keeps business logic isolated from presentation layers.

### Q98. What business validations could be added?
**Answer:**  
* Check that student users have the `STUDENT` role.
* Verify check-in dates are not in the past.
* Ensure check-out dates are after check-in dates.
* Prevent updating room capacity below its current occupancy.
* Validate payment dates are not after due dates.

---

## 9. Validation and Error Handling Questions

### Q99. How is request validation implemented?
**Answer:**  
Validation is implemented using Jakarta validation annotations (like `@NotBlank`, `@Size`, `@Min`) on DTO fields. Controller methods are annotated with `@Valid` to trigger these checks on incoming payloads.

### Q100. Give examples of validations.
**Answer:**  
* `AuthDTO.RegisterRequest.email`: Validates email formats using `@Email`.
* `AuthDTO.RegisterRequest.password`: Restricts minimum password length using `@Size(min = 6)`.
* `RoomDTO.Request.capacity`: Restricts capacity using `@Min(1)` and `@Max(6)`.

### Q101. How are validation errors returned?
**Answer:**  
When validation fails, Spring throws a `MethodArgumentNotValidException`. `GlobalExceptionHandler` intercepts this exception, extracts the field names and validation messages, and returns a `400 Bad Request` containing the error details.

### Q102. What custom exceptions exist?
**Answer:**  
* `ResourceNotFoundException`: Thrown when a requested database record is missing.
* `StayloException`: Thrown when business rules are violated.

### Q103. Why have a global exception handler?
**Answer:**  
It centralizes exception mapping, removing the need for manual try-catch blocks in controller methods and ensuring the API returns a consistent error response structure.

### Q104. What is one issue with the general exception handler?
**Answer:**  
The default handler catches all unexpected exceptions and returns the exception message in the JSON body:
```java
return ResponseEntity.status(500).body("An unexpected error occurred: " + ex.getMessage());
```
This can expose internal database or code details to clients. In production, these details should be logged internally, and the API should return a generic error message.

### Q105. How would you improve error responses?
**Answer:**  
I would include timestamps, error codes, request paths, and correlation IDs in the error body to make debugging and tracking client issues easier.

---

## 10. Testing Questions

### Q106. What testing tools are used?
**Answer:**  
JUnit 5, Mockito (for mocking dependencies), AssertJ (for assertions), MockMvc (for controller testing), and H2 (for in-memory database testing).

### Q107. What does `StudentServiceTest` test?
**Answer:**  
It unit-tests student registration and profile query logic. It mocks the database repositories to test service validation rules in isolation.

### Q108. What does `AuthControllerTest` test?
**Answer:**  
It runs integration tests against the authentication endpoints using `MockMvc` and an in-memory database, verifying registration, login checks, validation failures, and duplicate email errors.

### Q109. Why use H2 for tests?
**Answer:**  
H2 is an in-memory database that runs in system memory, removing the need for an external MySQL instance during testing. This keeps test execution fast, isolated, and easy to run in CI environments.

### Q110. What does `@ActiveProfiles("test")` do?
**Answer:**  
It tells Spring Boot to load configurations from `application-test.yml` during test execution. This overrides default production settings to configure H2 and disable seed data queries.

### Q111. Why use Mockito in service tests?
**Answer:**  
Mockito isolates the service layer from database operations by mocking repository interfaces. This allows unit tests to focus purely on verifying business logic rules.

### Q112. Why use MockMvc?
**Answer:**  
MockMvc allows testing controller routing, serialization, validation, and security rules without starting a full HTTP server, keeping integration tests fast.

### Q113. What tests are missing?
**Answer:**  
The test suite is missing coverage for room allocation conflicts, vacating logic, payment validation rules, listing ownership checks, JWT filter routing, and role-based security failures.

### Q114. What happened when this guide ran tests locally?
**Answer:**  
Test compilation failed with package resolution errors for DTO classes. Since the application compiled successfully, this indicates classpath or annotation processing issues in the build environment. In an interview, I would explain: "I would investigate classpath configurations, verify Lombok configuration flags, and ensure clean builds."

### Q115. How would you debug that test compile failure?
**Answer:**  
I would run `./mvnw clean test-compile -e` to inspect the compiler logs, verify Lombok's configuration in the Maven compiler plugin, and ensure the generated sources directory is included in the test classpath.

### Q116. How would you improve test coverage?
**Answer:**  
I would add unit tests for each service's business logic, integration tests for role-based security access, and concurrency tests to verify room allocations under load.

---

## 11. Docker and Deployment Questions

### Q117. Explain the Dockerfile.
**Answer:**  
The application uses a **multi-stage build**:
* **Build Stage:** Uses a JDK image to compile the application and build the jar file.
* **Runtime Stage:** Copies only the generated jar file to a smaller JRE runtime image.
This setup minimizes the final image size and reduces the container's security risk.

### Q118. Why multi-stage Docker build?
**Answer:**  
It separates the compile environment from the runtime environment. By excluding build tools (like Maven and compilation libraries) from the final container, the production image remains small and secure.

### Q119. Explain `docker-compose.yml`.
**Answer:**  
It orchestrates the application containers. It defines a MySQL 8 container and the Staylo application container, connects them to a custom bridge network, configures shared database volumes, and overrides application settings.

### Q120. Why use a MySQL health check?
**Answer:**  
It prevents the application container from starting before the MySQL database is healthy and ready to accept connections, avoiding connection errors on startup.

### Q121. How is configuration overridden in Docker Compose?
**Answer:**  
By defining environment variables in the `docker-compose.yml` file (e.g., `SPRING_DATASOURCE_URL`), which override the default values set in `application.yml`.

### Q122. How would you deploy this to production?
**Answer:**  
I would build the application Docker image, push it to a secure registry, deploy the container behind a reverse proxy (like NGINX), use a managed database service (like AWS RDS), externalize secrets, and configure database migrations.

### Q123. What production changes are needed?
**Answer:**  
* Load secrets from environment variables.
* Set `ddl-auto` to `validate` or `none`.
* Integrate database migration tools (Flyway).
* Disable debug logging and SQL query visibility.
* Restrict CORS configurations.

---

## 12. CI/CD Questions

### Q124. What does the GitHub Actions workflow do?
**Answer:**  
It automates continuous integration on pushes or pull requests to the main branches. It checks out the code, configures JDK 17, caches Maven dependencies, and runs `mvn clean test` to verify the build.

### Q125. Why run tests in CI?
**Answer:**  
It catches build errors and test failures automatically before code is merged, preventing broken builds from reaching main deployment branches.

### Q126. How would you improve the CI pipeline?
**Answer:**  
I would add steps for static code analysis (SonarQube), dependency security scanning, automated Docker image builds, and automated deployments to staging environments on successful builds.

### Q127. What is the difference between local tests and CI tests?
**Answer:**  
CI tests run in clean, isolated runner environments, ensuring test results are consistent. Local tests can be affected by cached dependencies, local configurations, and existing database states.

---

## 13. Swagger and API Documentation Questions

### Q128. How is Swagger configured?
**Answer:**  
Swagger is configured in `SwaggerConfig.java` using a custom `OpenAPI` bean. This bean defines project details and adds an HTTP Bearer JWT security scheme to enable token authentication inside the Swagger UI.

### Q129. How do you test protected endpoints in Swagger?
**Answer:**  
First, authenticate via `/api/auth/login` to retrieve a JWT token. Copy the token, click the "Authorize" button in Swagger UI, paste the token, and close. Swagger will now attach the token to all API calls.

### Q130. Why annotate controllers with `@Operation` and `@Tag`?
**Answer:**  
They add summaries, detailed descriptions, and functional groupings to endpoints inside the Swagger UI, making the API documentation easier to navigate for developers.

---

## 14. Code-Level Deep Dive Questions

### Q131. Why use `@RequiredArgsConstructor`?
**Answer:**  
It generates constructors for all final fields at compile time. This enables constructor-based dependency injection in Spring components without writing explicit constructor code.

### Q132. Why constructor injection?
**Answer:**  
* **Immutability:** Allows declaring dependency fields as `final`.
* **Testing:** Simplifies unit testing by allowing manual instantiation of classes without Spring context.
* **Safety:** Prevents instantiating beans with missing dependencies.

### Q133. Why use `@Transactional` only on some service methods?
**Answer:**  
`@Transactional` is applied to service methods that perform write, update, or multi-table operations to protect database integrity. Pure read methods generally do not require transactional boundaries.

### Q134. Would read methods benefit from `@Transactional(readOnly = true)`?
**Answer:**  
Yes. Marking transactions as `readOnly = true` optimization flags can improve Hibernate performance, prevent dirty checks, and clarify intent for developers.

### Q135. Why map entities manually instead of using ModelMapper or MapStruct?
**Answer:**  
Manual mapping is explicit and type-safe without adding external library overhead. For larger projects with many models, integrating MapStruct would reduce boilerplate code.

### Q136. What is a downside of manual mapping here?
**Answer:**  
It increases repetitive mapping code in services, which can make the codebase harder to maintain as the number of entities and DTOs grows.

### Q137. Why use nested static DTO classes?
**Answer:**  
It groups request and response models within their parent domain class (e.g., `RoomDTO.Request` and `RoomDTO.Response`), keeping payload definitions organized.

### Q138. Why is `roomNumber` not updated in `updateRoom`?
**Answer:**  
Room numbers act as unique, stable inventory identifiers. Allowing updates would require additional validation checks to prevent duplicate room numbers.

### Q139. Why does `AllocationService` use `SecurityContextHolder`?
**Answer:**  
It extracts the username of the logged-in administrator from the security context to record who performed the allocation for auditing purposes.

### Q140. Why does `PGListingService` use `SecurityContextHolder`?
**Answer:**  
It identifies the authenticated user to set them as the listing owner and verify ownership permissions during updates or deletions.

### Q141. What is the danger of directly using `SecurityContextHolder` in services?
**Answer:**  
It couples business services to the Spring Security framework and makes unit testing harder. A cleaner approach is to use a wrapper service class to resolve the current user.

### Q142. Why return `ApiResponse<Void>` for delete?
**Answer:**  
Delete operations do not return database payloads. Returning `ApiResponse<Void>` allows the API to return a consistent response envelope indicating success.

---

## 15. System Design Follow-Up Questions

### Q143. How would you scale this backend?
**Answer:**  
Since authentication is stateless, I would run multiple application containers behind an NGINX load balancer, implement database replication, optimize connection pools, and integrate Redis caching.

### Q144. How would you prevent room overbooking?
**Answer:**  
I would implement database optimistic locking using a `@Version` field on the room entity, or fetch room records using pessimistic locks (`SELECT FOR UPDATE`) during allocation.

### Q145. How would you add search for PG listings?
**Answer:**  
I would implement indexing on filter columns (city, rent) in MySQL. For advanced searches, I would integrate a search engine like **Elasticsearch**.

### Q146. How would you add image upload for PG listings?
**Answer:**  
I would upload files to an object storage service (like Amazon S3), store the generated file URLs in the database, and configure image compression pipelines.

### Q147. How would you add notifications?
**Answer:**  
I would deploy a background worker service and implement event-driven updates using a message broker (like RabbitMQ) to send emails or messages asynchronously.

### Q148. How would you add audit logging?
**Answer:**  
I would implement entity listeners (using Hibernate Envers) or database trigger scripts to record state changes and user details for sensitive transactions.

### Q149. How would you design a frontend for Staylo?
**Answer:**  
I would build a Single Page Application using React. It would include admin dashboards for room management, owner views for listings, and student search views.

### Q150. How would you expose analytics?
**Answer:**  
I would create database view tables or repository aggregate queries to expose metrics (occupancy rates, collected fees, pending balances) through dedicated reporting endpoints.

---

## 16. Common Java and Spring Questions Connected to Staylo

### Q151. What is dependency injection?
**Answer:**  
It is a design pattern where object dependencies are provided from the outside rather than created internally, promoting loose coupling and making testing easier.

### Q152. What is inversion of control?
**Answer:**  
It is the architectural principle of delegating control over object instantiation, configuration, and lifecycles to a framework container (the Spring IoC container).

### Q153. What is `@Service`?
**Answer:**  
It is a stereotype annotation that marks a class as a Spring bean containing business logic, making it discoverable during classpath scans.

### Q154. What is `@Repository`?
**Answer:**  
It marks a data access component as a Spring bean. It also enables automatic translation of database-specific exceptions into Spring's unified data access hierarchy.

### Q155. What is `@RestController`?
**Answer:**  
It is a convenience annotation combining `@Controller` and `@ResponseBody`. It indicates the controller will serialize response objects directly into JSON response bodies.

### Q156. What is `@RequestMapping`?
**Answer:**  
It configures route mapping URL prefixes for controller classes or configures specific HTTP request methods for endpoints.

### Q157. What is `@PathVariable`?
**Answer:**  
It binds dynamic variables extracted from the request URL path (e.g., `/api/rooms/{id}`) directly to controller method arguments.

### Q158. What is `@RequestBody`?
**Answer:**  
It tells Spring to deserialize the incoming JSON request body into the annotated Java controller method argument.

### Q159. What is `@RequestParam`?
**Answer:**  
It binds query parameters from the request URL string (e.g., `?type=SINGLE`) directly to controller method arguments.

### Q160. What is `Optional` used for?
**Answer:**  
It is a wrapper object introduced in Java 8 that may or may not contain a value, helping prevent runtime NullPointerExceptions.

---

## 17. Resume Defense Questions

### Q161. What was your contribution?
**Answer:**  
"I designed the relational database schema, configured Spring Security and JWT authentication, implemented room allocation business logic, built the global exception handling, and set up Docker containerization and GitHub Actions workflows."

### Q162. What was the hardest part?
**Answer:**  
"Implementing the room allocation transaction flow. It required updating multiple tables, verifying student allocation states, checking room availability, and ensuring these updates rollback cleanly on failures."

### Q163. What did you learn?
**Answer:**  
"I learned how to design secure REST APIs, implement state validation rules, handle transactions, write automated tests, and package applications using multi-stage Docker builds."

### Q164. What would you improve if you had more time?
**Answer:**  
"I would move configuration secrets out of codebase files, implement database migration scripts, expand test suite coverage, and add Redis caching."

### Q165. What is one honest limitation?
**Answer:**  
"The application does not use optimistic locking to protect room allocations against concurrent requests, which could lead to overbooking under heavy load."

### Q166. How would you answer if asked why credentials are committed?
**Answer:**  
"For this demo repository, I included default MySQL configurations directly in the configuration file to simplify local developer setup. For production, I would load these settings from environment variables."

### Q167. How would you answer if asked about test failure?
**Answer:**  
"The test execution failed in this local build environment due to classpath configuration details. I would debug this by reviewing build plugin configurations and Lombok compiler settings."

### Q168. How do you show ownership in an interview?
**Answer:**  
By explaining transaction flows (e.g. allocation) clearly, explaining why specific architecture choices were made, and demonstrating awareness of security tradeoffs and scalability limits.

---

## 18. Scenario-Based Questions

### Q169. A student says they can see another student's payments. What do you check?
**Answer:**  
I would inspect the payment controller's endpoint authorization rules to verify if the service compares the authenticated user's ID against the requested payment profile's student ID.

### Q170. A room shows available but allocation fails. Why?
**Answer:**  
Possible causes include: the student already has an active allocation, the room status is set to maintenance, or another transaction modified the room status.

### Q171. A room gets overbooked during high traffic. What happened?
**Answer:**  
A race condition occurred. Two concurrent threads read the same room occupancy level before either committed updates, allocating the same bed. I would implement pessimistic locking to fix this.

### Q172. Login fails even for correct credentials. What would you inspect?
**Answer:**  
I would check database user status columns, verify the password encoder bean configurations, and check database encryption inputs.

### Q173. JWT validation fails. What would you inspect?
**Answer:**  
I would verify request authorization headers, validate expiration times, check signing key configurations, and verify filter execution order.

### Q174. Swagger protected endpoints return 403. What should the user do?
**Answer:**  
Verify they paste the JWT token correctly into the authorization field in Swagger, and check if their authenticated user role has permission to access the endpoint.

### Q175. Data is duplicated on app startup. What do you check?
**Answer:**  
I would check database schema initialization modes and verify if the data seed scripts use unique primary keys or idempotent SQL commands (`INSERT IGNORE`).

### Q176. App cannot connect to MySQL in Docker. What do you check?
**Answer:**  
I would verify the network settings in the docker-compose configuration, check the database URL hostname, and verify the MySQL container startup state.

---

## 19. Advanced Improvement Answers

### Q177. How would you add pagination?
**Answer:**  
I would update repository methods to accept `Pageable` parameters, returning `Page<T>` wrapper objects to return paginated lists to clients.

### Q178. How would you add sorting?
**Answer:**  
I would pass Spring Sort parameters containing target columns and directions (ascending/descending) to the repository methods.

### Q179. How would you handle API versioning?
**Answer:**  
I would use URL path versioning (e.g. `/api/v1/rooms`) to keep different API versions isolated and clear for clients.

### Q180. How would you handle database migrations?
**Answer:**  
I would integrate **Flyway**, manage database modifications using versioned SQL migration scripts, and disable Hibernate's schema update configurations.

### Q181. How would you add refresh tokens?
**Answer:**  
I would issue short-lived JWT access tokens and long-lived refresh tokens stored securely in the database, invalidating refresh tokens on logout.

### Q182. How would you protect against brute-force login?
**Answer:**  
I would implement rate-limiting filters (using Bucket4j) and configure automatic account lockout rules after consecutive login failures.

### Q183. How would you add role hierarchy?
**Answer:**  
I would configure Spring Security's `RoleHierarchy` bean to automatically inherit permissions from lower roles (e.g., `ADMIN` inheriting `WARDEN` permissions).

### Q184. How would you add caching?
**Answer:**  
I would configure Spring Cache with Redis to cache available room and public housing search responses, clearing the cache when listings are updated.

### Q185. How would you add observability?
**Answer:**  
I would integrate Spring Boot Actuator, configure Micrometer to collect performance metrics, export logs to a central stack, and configure correlation IDs.

### Q186. How would you secure Swagger in production?
**Answer:**  
I would disable Swagger in production profiles or restrict access to the documentation pages behind basic authentication.

### Q187. How would you handle file uploads?
**Answer:**  
I would upload images to an object storage service, validate file size limits and image formats, and store the file URLs in the database.

### Q188. How would you handle soft deletes?
**Answer:**  
I would add an `isDeleted` boolean column to entities and configure Hibernate filter annotations (`@SQLDelete` and `@Where`) to filter out deleted records.

### Q189. How would you model amenities better?
**Answer:**  
Instead of comma-separated strings, I would define an `Amenity` entity and map it to housing listings using a many-to-many relationship.

### Q190. How would you handle monetary values better?
**Answer:**  
I would use Java's `BigDecimal` class instead of `Double` to store fees and payments, avoiding floating-point rounding errors in calculations.

### Q191. How would you handle time zones?
**Answer:**  
I would store all database timestamps in UTC using Java's `Instant` class, leaving date formatting to be handled by the client application.

### Q192. How would you add email verification?
**Answer:**  
I would generate unique verification tokens on user registration, email activation links, and update user active status flags upon verification.

### Q193. How would you add payment gateway integration?
**Answer:**  
I would integrate a payment processor API (like Stripe), verify webhook signatures, and process payment transactions asynchronously.

### Q194. How would you make webhook handling idempotent?
**Answer:**  
I would save the payment processor's transaction event IDs in the database with unique constraints, ignoring duplicate webhook events.

### Q195. How would you protect update/delete listing endpoints better?
**Answer:**  
I would implement custom Spring Security expression annotations to check listing owner permissions before routing requests to controllers.

---

## 20. Questions You Should Ask the Interviewer

### Q196. If this were your production system, would you prefer optimistic or pessimistic locking for room allocation?
**Answer:**  
This question shows you understand database concurrency tradeoffs. Optimistic locking is better for low-contention scenarios, while pessimistic locking is preferred for high-contention systems.

### Q197. Would your team prefer DTO mapping manually or using MapStruct?
**Answer:**  
This question shows you value code maintainability and team standards. MapStruct reduces boilerplate code, while manual mapping is explicit.

### Q198. How strict are your production standards around API versioning and backward compatibility?
**Answer:**  
This shows you think about API lifecycles and downstream client integrations.

### Q199. What observability stack does your team use for Spring Boot services?
**Answer:**  
This demonstrates you care about monitoring application performance, container states, and logging in production.

### Q200. How do you usually separate authentication, authorization, and object ownership checks?
**Answer:**  
This shows you think about security design patterns and how to handle role and resource permissions cleanly.

---

## 21. Most Important Answers to Memorize

1. **Project Pitch:** Staylo is a Spring Boot REST backend for hostel accommodation workflows with JWT auth, RBAC, JPA, DTOs, validation, Swagger, Docker, and CI.
2. **Architecture:** Thin controllers handle routing, transactional services run business logic, repositories handle database access, and DTOs isolate models.
3. **Security:** Logins authenticate via `AuthenticationManager`, passwords encrypt using BCrypt, and `JwtAuthFilter` validates request headers statelessly.
4. **Allocation Logic:** Checks student, checks active allocations, checks room availability, increments occupancy, and saves allocation under a transaction.
5. **Production Upgrades:** Use database migrations (Flyway), load secrets from environment variables, use `BigDecimal` for currency, and implement Redis caching.
6. **Code Limitation:** The current version does not prevent concurrency overbooking in allocations, requiring database locking implementation.
