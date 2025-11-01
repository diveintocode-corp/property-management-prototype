package com.example.app.service;

import com.example.app.model.Tenant;
import java.util.List;

public interface TenantService {
    List<Tenant> getAllTenants();
    Tenant getTenantById(Long id);
    void createTenant(Tenant tenant);
    void updateTenant(Tenant tenant);
    void deleteTenant(Long id);
}