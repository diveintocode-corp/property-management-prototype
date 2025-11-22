# Part 2: Business Logic & Cross-Entity Rules (10 Minutes)

## Service Layer Pattern (2 minutes)

> "Welcome back. In Part 1, we built the foundation—models, mappers, and controllers. Now we tackle the intelligence of the system: the **Service Layer**.
>
> Services are where business rules live. They sit between controllers and mappers, enforcing constraints that keep our data consistent.
>
> We follow an **interface-based pattern** for all services:
> *   `PropertyService` / `PropertyServiceImpl`
> *   `TenantService` / `TenantServiceImpl`
> *   `LeaseService` / `LeaseServiceImpl`
>
> **Why interfaces?** This enables dependency injection and makes testing easier. We can mock services in unit tests.
>
> **Transaction Management:** All create/update/delete methods are marked with `@Transactional`. This ensures that if something fails mid-operation, the entire transaction rolls back—keeping our database in a consistent state."

---

## Property Management Rules (2.5 minutes)

> "Let's start with **PropertyService**. At first glance, it looks simple—just CRUD operations:
>
> *   `getAllProperties()` - Fetch all properties
> *   `getPropertyById(Long id)` - Fetch one property
> *   `createProperty(Property property)` - Insert new property
> *   `updateProperty(Property property)` - Update existing property
> *   `deleteProperty(Long id)` - Delete property
>
> But here's where it gets interesting: **deletion isn't always allowed**.
>
> ### **Deletion Guard Rule**
> **Business Rule:** You cannot delete a property if it has any active leases.
>
> **Why?** Imagine deleting a property while someone is actively renting it. You'd orphan the lease data and lose critical contract information.
>
> **How it's enforced:**
> Look at `PropertyController.delete()`:
> ```java
> if (leaseService.hasActiveLeases(id)) {
>     redirectAttributes.addFlashAttribute(\"error\", 
>         \"Cannot delete property with active leases\");
>     return \"redirect:/properties/\" + id;
> }
> propertyService.deleteProperty(id);
> ```
>
> The controller checks with `LeaseService` before allowing deletion. If active leases exist, the operation is blocked and the user sees a friendly error message.
>
> **Key Insight:** The service layer provides the `hasActiveLeases()` method, but the controller enforces the rule. This separation keeps concerns clean."

---

## Tenant Management Rules (2 minutes)

> "**TenantService** follows a similar pattern:
>
> *   Standard CRUD operations: `getAllTenants()`, `getTenantById()`, `createTenant()`, `updateTenant()`, `deleteTenant()`
>
> ### **Deletion Guard Rule**
> **Business Rule:** You cannot delete a tenant if they have any existing leases (active or historical).
>
> **Why?** Leases are legal contracts. Even after a lease ends, we need to maintain the historical record of who rented what and when.
>
> **How it's enforced:**
> Look at `TenantController.delete()`:
> ```java
> if (!leaseService.getLeasesByTenantId(id).isEmpty()) {
>     redirectAttributes.addFlashAttribute(\"error\", 
>         \"Cannot delete tenant with existing leases\");
>     return \"redirect:/tenants\";
> }
> tenantService.deleteTenant(id);
> ```
>
> Again, the controller consults `LeaseService` to check for any leases (not just active ones) before permitting deletion.
>
> **Pattern Recognition:** Both Property and Tenant deletions follow the same guard pattern—check dependencies first, then act."

---

## Lease Management Rules (3.5 minutes)

> "Now for the most complex part: **LeaseService**. This is where the critical business logic lives.
>
> ### **The Golden Rule: One Active Lease Per Property**
> **Business Rule:** A property can only have ONE lease with status 'ACTIVE' at any given time.
>
> **Why?** A property can't be rented to two different tenants simultaneously. This is a fundamental constraint in property management.
>
> ### **How It's Enforced**
> Look at `LeaseServiceImpl.createLease()`:
> ```java
> public void createLease(Lease lease) {
>     if (!validateLease(lease)) {
>         throw new IllegalStateException(
>             \"Cannot create lease: Property already has an active lease\");
>     }
>     leaseMapper.insert(lease);
> }
> ```
>
> The magic happens in `validateLease()`:
> ```java
> private boolean validateLease(Lease lease) {
>     if (!\"ACTIVE\".equals(lease.getStatus())) {
>         return true;  // Non-active leases don't conflict
>     }
>     
>     List<Lease> activeLeases = 
>         leaseMapper.findActiveLeasesByPropertyId(lease.getPropertyId());
>     
>     return activeLeases.isEmpty() || 
>            (activeLeases.size() == 1 && 
>             activeLeases.get(0).getId().equals(lease.getId()));
> }
> ```
>
> **What's happening:**
> 1.  If the lease status isn't 'ACTIVE', validation passes (you can have multiple ENDED or NOTICE leases).
> 2.  If it IS 'ACTIVE', we query the database for other active leases on the same property.
> 3.  Validation passes only if:
>     *   No active leases exist, OR
>     *   The only active lease is the one we're currently editing (for updates).
>
> ### **Update Logic**
> `updateLease()` is even smarter:
> ```java
> if (!existing.getPropertyId().equals(lease.getPropertyId()) ||
>     (\"ACTIVE\".equals(lease.getStatus()) && 
>      !\"ACTIVE\".equals(existing.getStatus()))) {
>     if (!validateLease(lease)) {
>         throw new IllegalStateException(
>             \"Cannot update lease: Property already has an active lease\");
>     }
> }
> ```
>
> It only re-validates if:
> *   The property changed (moving the lease to a different property), OR
> *   The status changed TO 'ACTIVE' (activating a previously inactive lease).
>
> ### **Status Transitions**
> The typical lifecycle: `ACTIVE` → `NOTICE` → `ENDED`
> *   **ACTIVE:** Tenant is currently living there.
> *   **NOTICE:** Tenant has given notice to leave.
> *   **ENDED:** Contract is complete.
>
> ### **Exception Handling**
> When validation fails, we throw `IllegalStateException`. The controller catches this and converts it to a user-friendly error message on the form."

---

## Summary & Next Steps

> "Let's recap the business rules we've implemented:
>
> 1.  **Property Deletion Guard:** Cannot delete if active leases exist.
> 2.  **Tenant Deletion Guard:** Cannot delete if any leases exist.
> 3.  **Lease Uniqueness Rule:** Only one ACTIVE lease per property.
>
> These rules are enforced in the **Service Layer**, making them impossible to bypass—even if someone tries to circumvent the UI.
>
> We've now covered the structure (Part 1) and the intelligence (Part 2). In Part 3, we'll add the final layer: **Security**, and demonstrate the entire system working together in a live demo."
