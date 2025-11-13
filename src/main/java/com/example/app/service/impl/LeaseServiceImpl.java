package com.example.app.service.impl;

import com.example.app.mapper.LeaseMapper;
import com.example.app.model.Lease;
import com.example.app.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * LeaseServiceインターフェースの実装クラス。
 * 賃貸契約の管理に関するビジネスロジックを実装します。
 * 契約の重複チェックや状態管理の検証ロジックを提供します。
 */
@Service
@RequiredArgsConstructor
public class LeaseServiceImpl implements LeaseService {
    
    private final LeaseMapper leaseMapper;
    
    @Override
    public Lease getLeaseById(Long id) {
        return leaseMapper.findById(id);
    }
    
    @Override
    public List<Lease> getLeasesByPropertyId(Long propertyId) {
        return leaseMapper.findByPropertyId(propertyId);
    }
    
    @Override
    public List<Lease> getLeasesByTenantId(Long tenantId) {
        return leaseMapper.findByTenantId(tenantId);
    }
    
    @Override
    @Transactional
    public void createLease(Lease lease) {
        if (!validateLease(lease)) {
            throw new IllegalStateException("Cannot create lease: Property already has an active lease");
        }
        leaseMapper.insert(lease);
    }
    
    @Override
    @Transactional
    public void updateLease(Lease lease) {
        Lease existing = leaseMapper.findById(lease.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Lease not found");
        }
        
        // If property changed or status changed to ACTIVE, validate
        if (!existing.getPropertyId().equals(lease.getPropertyId()) ||
            ("ACTIVE".equals(lease.getStatus()) && !"ACTIVE".equals(existing.getStatus()))) {
            if (!validateLease(lease)) {
                throw new IllegalStateException("Cannot update lease: Property already has an active lease");
            }
        }
        
        leaseMapper.update(lease);
    }
    
    @Override
    @Transactional
    public void deleteLease(Long id) {
        leaseMapper.delete(id);
    }
    
    @Override
    public boolean hasActiveLeases(Long propertyId) {
        return !leaseMapper.findActiveLeasesByPropertyId(propertyId).isEmpty();
    }
    
    /**
     * 賃貸契約の有効性を検証します。
     * 同一物件に対して複数のアクティブな契約が存在しないことを確認します。
     * 
     * @param lease 検証する契約情報
     * @return 契約が有効な場合はtrue、無効な場合はfalse
     */
    private boolean validateLease(Lease lease) {
        if (!"ACTIVE".equals(lease.getStatus())) {
            return true;
        }
        
        List<Lease> activeLeases = leaseMapper.findActiveLeasesByPropertyId(lease.getPropertyId());
        return activeLeases.isEmpty() || 
               (activeLeases.size() == 1 && activeLeases.get(0).getId().equals(lease.getId()));
    }
}