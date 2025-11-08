package com.example.app.service.impl;

import com.example.app.mapper.TenantMapper;
import com.example.app.model.Tenant;
import com.example.app.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TenantServiceインターフェースの実装クラス。
 * 入居者情報の管理に関するビジネスロジックを実装します。
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    
    private final TenantMapper tenantMapper;
    
    @Override
    public List<Tenant> getAllTenants() {
        return tenantMapper.findAll();
    }
    
    @Override
    public Tenant getTenantById(Long id) {
        return tenantMapper.findById(id);
    }
    
    @Override
    @Transactional
    public void createTenant(Tenant tenant) {
        tenantMapper.insert(tenant);
    }
    
    @Override
    @Transactional
    public void updateTenant(Tenant tenant) {
        tenantMapper.update(tenant);
    }
    
    @Override
    @Transactional
    public void deleteTenant(Long id) {
        tenantMapper.delete(id);
    }
}