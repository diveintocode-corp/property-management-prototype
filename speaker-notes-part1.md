# Part 1: Architecture & Complete CRUD Foundation (10 Minutes)

## Introduction (1.5 minutes)

> "Hello everyone. Welcome to Part 1 of our Property Management System presentation. This is a Spring Boot application that manages properties, tenants, and lease contracts with business rules.
>
> Today we'll explore the **architectural foundation** that makes this system maintainable and scalable. We follow a strict **Layered Architecture**:
>
> 1. **Controller Layer:** Handles HTTP requests and renders views (Web logic).
> 2. **Service Layer:** Enforces business rules and orchestrates operations (Business logic).
> 3. **Mapper Layer:** Manages database interactions using MyBatis (Data logic).
>
> Our tech stack includes **Spring Boot** for the framework, **MyBatis** for database access, and **Thymeleaf** for server-side rendering. By the end of this session, you'll understand how we manage all three core entities: Properties, Tenants, and Leases."

---

## Domain Models - All Three Entities (3 minutes)

> "Let's start with our **Domain Model**—the classes in `src/main/java/com/example/app/model` that represent real-world business objects.
>
> ### **1. Property Entity**
> Represents a physical rental property (apartment, house, etc.).
> *   **Key Fields:** `name`, `address`, `area`, `rooms`
> *   **Validation:** `@NotBlank` on name and address ensures we never save incomplete property data.
> *   **Purpose:** This is what landlords manage and rent out.
>
> ### **2. Tenant Entity**
> Represents a person who rents a property.
> *   **Key Fields:** `fullName`, `phone`, `email`
> *   **Validation:** `@NotBlank` on fullName ensures we always know who the tenant is.
> *   **Purpose:** Contact information for the people renting properties.
>
> ### **3. Lease Entity**
> Represents the contract between a Property and a Tenant.
> *   **Key Fields:** 
>     - `propertyId`, `tenantId` (foreign keys linking the relationship)
>     - `status` (ACTIVE, NOTICE, ENDED)
>     - `rent`, `deposit`, `keymoney` (financial terms)
>     - `startDate`, `endDate` (contract duration)
> *   **Validation:** `@NotNull` on all required fields.
> *   **Purpose:** The legal contract that ties everything together.
>
> **Key Takeaway:** By using **Bean Validation** annotations directly on our models, we catch invalid data at the entry point—before it ever reaches our business logic or database."

---

## Data Access Layer (2.5 minutes)

> "For database persistence, we use **MyBatis**. Unlike heavyweight ORMs like Hibernate, MyBatis gives us full control over our SQL while still providing clean Java interfaces.
>
> Look at `src/main/java/com/example/app/mapper`. We have three mapper interfaces:
>
> ### **PropertyMapper**
> *   `List<Property> findAll()` - Get all properties
> *   `Property findById(Long id)` - Get one property
> *   `void insert(Property property)` - Create new property
> *   `void update(Property property)` - Update existing property
> *   `void delete(Long id)` - Remove property
>
> ### **TenantMapper**
> *   Same CRUD operations for tenants
> *   `findAll()`, `findById()`, `insert()`, `update()`, `delete()`
>
> ### **LeaseMapper**
> *   Standard CRUD operations PLUS:
> *   `List<Lease> findByPropertyId(Long propertyId)` - Find all leases for a property
> *   `List<Lease> findByTenantId(Long tenantId)` - Find all leases for a tenant
> *   `List<Lease> findActiveLeasesByPropertyId(Long propertyId)` - **Critical query** used to enforce business rules
>
> **The Strategy:** SQL lives in XML files (`src/main/resources/mappers`), keeping our Java code clean and our queries visible. Services call these mappers to persist changes."

---

## Controller Layer - Basic CRUD (3 minutes)

> "Now let's see how we expose this functionality to users through **Controllers**.
>
> ### **PropertyController** (`/properties`)
> Handles all property-related web requests:
> *   `GET /properties` → List all properties
> *   `GET /properties/{id}` → Show property details (including its leases)
> *   `GET /properties/new` → Display create form
> *   `GET /properties/{id}/edit` → Display edit form
> *   `POST /properties` → Save (create or update) property
> *   `POST /properties/{id}/delete` → Delete property (with guards)
>
> ### **TenantController** (`/tenants`)
> Handles tenant management:
> *   `GET /tenants` → List all tenants
> *   `GET /tenants/new` → Display create form
> *   `GET /tenants/{id}/edit` → Display edit form (shows tenant's leases)
> *   `POST /tenants` → Save tenant
> *   `POST /tenants/{id}/delete` → Delete tenant (with guards)
>
> ### **Form Handling Pattern**
> All controllers follow the same pattern:
> 1.  Receive form data with `@Valid @ModelAttribute`
> 2.  Check `BindingResult` for validation errors
> 3.  If errors exist, redisplay the form with error messages
> 4.  If valid, call the service layer
> 5.  Redirect with success/error flash messages
>
> **Key Principle:** Controllers are 'thin'—they don't make business decisions. They just coordinate between the web layer and the service layer, then return Thymeleaf template names like `properties/list` or `tenants/form`."

---

## Summary & Next Steps

> "Let's recap what we've covered:
>
> *   **Three Core Entities:** Property, Tenant, and Lease—each with validation.
> *   **MyBatis Mappers:** Clean interfaces for CRUD operations on all entities.
> *   **Controllers:** Web endpoints that handle forms and delegate to services.
>
> This gives us a solid foundation for basic operations. But what about business rules? What prevents someone from creating two active leases for the same property? Or deleting a property that has active contracts?
>
> That's where the **Service Layer** comes in—and that's exactly what we'll cover in Part 2."
