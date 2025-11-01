package com.example.app.service;

import com.example.app.model.Lease;
import java.util.List;

public interface LeaseService {
    List<Lease> getAllLeases();
    Lease getLeaseById(Long id);
    List<Lease> getLeasesByPropertyId(Long propertyId);
    List<Lease> getLeasesByTenantId(Long tenantId);
    void createLease(Lease lease);
    void updateLease(Lease lease);
    void deleteLease(Long id);
    boolean hasActiveLeases(Long propertyId);
}