## Introduction (30 seconds)

> “Hello — I’ll walk you through our Property Management prototype. This is a small but realistic Spring Boot app that demonstrates layered architecture, secure authentication, and important domain rules for managing properties, tenants and leases. I’ll show how the UI maps to services and validations that keep the data consistent.”

---

## Project structure (1 minute)

> “Take a look at the code in `src/main/java/com/example/app`. The project follows a familiar pattern:
>
> * **model** (domain classes),
> * **controller** (web endpoints and views),
> * **service** (business logic and validation),
> * **mapper** (MyBatis interfaces for DB access), and
> * **security/config** (authentication and security wiring).
>
> This separation keeps controllers thin, concentrates business logic in services for easier testing, and keeps database code isolated in mappers. For example, see the controllers in `src/main/java/com/example/app/controller` and services in `src/main/java/com/example/app/service` (`LeaseController`, `LeaseService`, etc.).”

---

## Entity layer — the domain models (1 minute)

> “Open the model classes: `User`, `Tenant`, `Property`, and `Lease`. They map directly to the business domain:
>
> * `User` holds authentication info and uses validation annotations.
> * `Tenant` and `Property` define contact and address info respectively.
> * `Lease` captures the contract between a property and a tenant and includes `status`, `startDate`, `rent`, and navigation properties. Note the documented domain values like `ACTIVE`, `NOTICE`, and `ENDED`.
>
> These classes carry validation annotations (e.g., `@NotBlank`, `@NotNull`) so we catch invalid input early.”

---

## Data access — MyBatis mappers (45 seconds)

> “The app uses MyBatis mappers for persistence. You’ll find `PropertyMapper`, `TenantMapper`, `LeaseMapper`, and `UserMapper` in `src/main/java/com/example/app/mapper`. They declare DB operations like `findById`, `findAll`, `insert`, `update`, and `delete`. Services call these mappers to persist changes. One important mapper method is `findActiveLeasesByPropertyId`, which the service uses to enforce the one-active-lease rule.”

---

## Service layer — where business rules live (1 minute 15 seconds)

> “Our core business rules live in the service layer. Services follow an interface-based pattern (ideal for DI and testing): `LeaseService` / `LeaseServiceImpl`, `PropertyServiceImpl`, `TenantServiceImpl`.
>
> A key rule: **only one ACTIVE lease per property**. Before creating or updating a lease, the service checks whether the property already has an active lease; if it does, the operation is rejected. This logic is centralized in `validateLease`, which calls `findActiveLeasesByPropertyId`. Centralizing rules here prevents client-side circumvention and makes testing easier.
>
> Other responsibilities:
>
> * `PropertyService` handles property create/update/delete; controllers consult `LeaseService.hasActiveLeases` before permitting deletion.
> * `TenantService` handles tenant CRUD; controllers block deletion if the tenant has leases.”

---

## Controller layer — UI endpoints & templates (1 minute)

> “Controllers map HTTP requests to templates and service calls. This project uses server-rendered templates: controllers return template names such as `properties/list`, `properties/form`, `leases/form`, and `tenants/form`.
>
> Examples:
>
> * `LeaseController.newForm` and `LeaseController.save` set up model data and return the lease form or redirect after success.
> * `PropertyController` handles list/detail/new/edit/save/delete and blocks delete when active leases exist.
> * `TenantController` lists tenants and blocks tenant deletion if leases exist.
>
> Keeping controllers thin while delegating logic to services helps maintain a clean codebase.”

---

## Exception handling, DTOs & validation (30 seconds)

> “The app uses Bean Validation on models and DTOs. Controllers check `BindingResult` and, on validation errors, redisplay forms with messages. Service-layer exceptions such as `IllegalStateException` or `IllegalArgumentException` are used to indicate domain issues, which controllers catch and convert into friendly messages or form errors.”

---

## Security & configuration (45 seconds)

> “Security is implemented using Spring Security:
>
> * Passwords are hashed (BCrypt) before saving in `UserServiceImpl`.
> * `CustomUserDetailsService` loads users for authentication.
> * `SecurityConfig` wires the `DaoAuthenticationProvider`, configures the `AuthenticationManager`, enables form login and logout, and sets CSRF protection. Public endpoints include `/login` and `/register`.
>
> Also, when a user registers successfully we perform an **auto-login** that creates and stores the `SecurityContext` in the session, which gives a smooth onboarding experience.”

---

## Running the app & quick demo plan (1 minute)

> “To run the app: Right click on `PropertyManagementApplication.java` and select 'Run As Spring Boot App. The root `/` redirects to `/properties`, so the app opens on the properties list if you are logged in.
>
> Demo sequence (~60s):
>
> 1. Register a new user and show the auto-login redirect to `/properties`.
> 2. Create a property.
> 3. Create a tenant.
> 4. Create an **ACTIVE** lease for that property.
> 5. Attempt to create a second **ACTIVE** lease for the same property — demonstrate the service-level rejection.
> 6. Attempt to delete the property while a lease exists — show the deletion is blocked.
>
> These steps demonstrate the main business invariants: single active lease per property and guarded deletion.”

---

## Key features & takeaways (45 seconds)

> “A quick summary:
>
> * **Layered architecture** (controller → service → mapper) for maintainability and testability.
> * **Service-level validations**: the single ACTIVE lease rule and delete guards are enforced in services so they cannot be bypassed.
> * **Secure authentication**: BCrypt hashing and Spring Security integration, with a smooth auto-login upon registration.
> * **Server-rendered templates** and concise controllers that delegate logic to services.”

---

## Conclusion & next steps (30 seconds)

> “This property management prototype demonstrates sound Spring Boot practices: clear separation of concerns, centralized rules, and secure authentication.
