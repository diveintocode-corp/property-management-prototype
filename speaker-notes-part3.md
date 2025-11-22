# Part 3: Security, Full Demo & Integration (10 Minutes)

## Security Infrastructure (3 minutes)

> "Welcome to the final session. We've built the structure (Part 1) and the business logic (Part 2). Now we add the critical layer that makes this secure: **Security**.
>
> We use **Spring Security**, the industry-standard framework for Java applications.
>
> ### **SecurityConfig.java**
> This is where we configure authentication and authorization:
>
> **1. Public vs Authenticated Endpoints**
> ```java
> .authorizeHttpRequests(auth -> auth
>     .requestMatchers(\"/login\", \"/register\", \"/css/**\").permitAll()
>     .anyRequest().authenticated()
> )
> ```
> *   `/login`, `/register`, and static CSS files are public.
> *   Everything else requires authentication.
>
> **2. Form-Based Login**
> ```java
> .formLogin(form -> form
>     .loginPage(\"/login\")
>     .defaultSuccessUrl(\"/properties\", true)
>     .failureUrl(\"/login?error=true\")
> )
> ```
> *   Custom login page at `/login`.
> *   After successful login, users land on the properties dashboard.
>
> **3. CSRF Protection**
> ```java
> .csrf(csrf -> csrf
>     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
> )
> ```
> *   Protects against Cross-Site Request Forgery attacks.
> *   All forms must include a CSRF token.
>
> ### **Password Security**
> Look at `UserServiceImpl`:
> ```java
> user.setPassword(passwordEncoder.encode(user.getPassword()));
> userMapper.insert(user);
> ```
> *   Passwords are **never** stored in plain text.
> *   We use **BCrypt** hashing—a one-way cryptographic function.
> *   Even if the database is compromised, passwords remain secure.
>
> ### **CustomUserDetailsService**
> This class loads users from our database for authentication:
> ```java
> User user = userMapper.findByUsername(username);
> return new org.springframework.security.core.userdetails.User(
>     user.getUsername(),
>     user.getPassword(),
>     authorities
> );
> ```
> Spring Security calls this service during login to verify credentials.
>
> ### **Auto-Login Flow**
> After registration, we provide a seamless experience:
> *   **Standard Flow:** Register → Redirect to login → Type credentials again → Login. (Friction!)
> *   **Our Flow:** Register → Backend creates user → **Auto-login** → Redirect to dashboard. (Smooth!)
>
> This is implemented in `AuthController.register()` by programmatically creating and storing the `SecurityContext` in the session."

---

## Exception Handling & Validation (2 minutes)

> "A production system must handle errors gracefully. We have two types of validation:
>
> ### **1. Bean Validation Errors**
> These are caught at the controller level:
> ```java
> public String save(@Valid @ModelAttribute Property property,
>                    BindingResult bindingResult) {
>     if (bindingResult.hasErrors()) {
>         return \"properties/form\";  // Redisplay form with errors
>     }
>     // ... save logic
> }
> ```
> *   If a user submits a form with blank required fields, `@NotBlank` validation fails.
> *   The form is redisplayed with specific error messages next to each field.
> *   Example: \"Name is required\" appears next to the name input.
>
> ### **2. Business Logic Errors**
> These are thrown by the service layer:
> ```java
> try {
>     propertyService.createProperty(property);
> } catch (Exception e) {
>     bindingResult.reject(\"error\", e.getMessage());
>     return \"properties/form\";
> }
> ```
> *   If `LeaseService` throws `IllegalStateException` (e.g., \"Property already has an active lease\"), the controller catches it.
> *   The error message is added to the form and displayed to the user.
> *   This converts technical exceptions into user-friendly feedback.
>
> **Key Principle:** Users never see stack traces or cryptic error codes. They see actionable messages that help them fix the problem."

---

## Complete End-to-End Demo (5 minutes)

> "Now let's see everything working together in a live demonstration. I'll walk through the entire system from registration to enforcing business rules.
>
> ### **Step 1: User Registration & Auto-Login**
> *   Navigate to `/register`.
> *   Fill in username: `manager@example.com`, password: `secure123`.
> *   Click 'Register'.
> *   **Watch:** Immediately redirected to `/properties` (the dashboard) without needing to log in again.
> *   **Result:** Auto-login worked! The user is authenticated and ready to work.
>
> ---
>
> ### **Step 2: Property Management**
> **Create a Property:**
> *   Click 'New Property'.
> *   Fill in:
>     - Name: `Sunset Apartments`
>     - Address: `123 Ocean Drive`
>     - Area: `Downtown`
>     - Rooms: `2BR/1BA`
> *   Click 'Save'.
> *   **Result:** Property appears in the list.
>
> **View Property Details:**
> *   Click on 'Sunset Apartments'.
> *   **Result:** Detail page shows property info and an empty lease list (no leases yet).
>
> ---
>
> ### **Step 3: Tenant Management**
> **Create a Tenant:**
> *   Navigate to `/tenants`.
> *   Click 'New Tenant'.
> *   Fill in:
>     - Full Name: `John Doe`
>     - Phone: `555-1234`
>     - Email: `john@example.com`
> *   Click 'Save'.
> *   **Result:** Tenant appears in the list.
>
> **Create Another Tenant:**
> *   Repeat for `Jane Smith` with phone `555-5678` and email `jane@example.com`.
> *   **Result:** Now we have two tenants.
>
> ---
>
> ### **Step 4: Lease Management - The Happy Path**
> **Create First Lease:**
> *   Navigate to 'New Lease' (or from Property detail page).
> *   Select:
>     - Property: `Sunset Apartments`
>     - Tenant: `John Doe`
>     - Status: `ACTIVE`
>     - Rent: `2000`
>     - Start Date: Today's date
> *   Click 'Save'.
> *   **Result:** Lease created successfully. John Doe is now actively renting Sunset Apartments.
>
> ---
>
> ### **Step 5: Lease Management - The Business Rule Violation**
> **Attempt Second Active Lease:**
> *   Click 'New Lease' again.
> *   Select:
>     - Property: `Sunset Apartments` (same property!)
>     - Tenant: `Jane Smith` (different tenant)
>     - Status: `ACTIVE`
>     - Rent: `2000`
> *   Click 'Save'.
> *   **Result:** ❌ **Error message appears:** \"Cannot create lease: Property already has an active lease\"
> *   **Explanation:** The service layer detected the conflict and rejected the operation. The 'One Active Lease' rule is enforced.
>
> ---
>
> ### **Step 6: Deletion Guards - Property**
> **Attempt to Delete Property with Active Lease:**
> *   Go back to 'Sunset Apartments' detail page.
> *   Click 'Delete Property'.
> *   **Result:** ❌ **Error message:** \"Cannot delete property with active leases\"
> *   **Explanation:** The controller checked with `LeaseService` and blocked the deletion to protect data integrity.
>
> ---
>
> ### **Step 7: Deletion Guards - Tenant**
> **Attempt to Delete Tenant with Leases:**
> *   Navigate to `/tenants`.
> *   Try to delete 'John Doe'.
> *   **Result:** ❌ **Error message:** \"Cannot delete tenant with existing leases\"
> *   **Explanation:** Even though we might want to remove a tenant, we can't erase historical contract data.
>
> ---
>
> ### **Step 8: Validation Errors**
> **Test Form Validation:**
> *   Click 'New Property'.
> *   Leave 'Name' and 'Address' blank.
> *   Click 'Save'.
> *   **Result:** Form redisplays with error messages: \"Name is required\", \"Address is required\".
> *   **Explanation:** Bean Validation caught the errors before they reached the service layer.
>
> ---
>
> ### **Final Recap**
> We've demonstrated:
> 1.  ✅ **Seamless Authentication:** Auto-login after registration.
> 2.  ✅ **Complete CRUD:** Properties, Tenants, and Leases all manageable.
> 3.  ✅ **Business Rule Enforcement:** One active lease per property.
> 4.  ✅ **Data Integrity Protection:** Deletion guards prevent orphaned data.
> 5.  ✅ **User-Friendly Validation:** Clear error messages guide users.
>
> This is a system with:
> *   **Layered Architecture** for maintainability.
> *   **Service-Level Rules** that cannot be bypassed.
> *   **Secure Authentication** with BCrypt and Spring Security.
> *   **Polished UX** with validation feedback and auto-login.
>
> Thank you for your time.
