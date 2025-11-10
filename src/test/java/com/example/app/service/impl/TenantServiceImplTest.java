package com.example.app.service.impl;

import com.example.app.mapper.TenantMapper;
import com.example.app.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantMapper tenantMapper;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setFullName("山田 太郎");
        testTenant.setPhone("090-1234-5678");
        testTenant.setEmail("yamada@example.com");
        testTenant.setCreatedAt(LocalDateTime.now());
        testTenant.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllTenants_ShouldReturnAllTenants() {
        // Given
        Tenant tenant2 = new Tenant();
        tenant2.setId(2L);
        tenant2.setFullName("佐藤 花子");
        List<Tenant> expectedTenants = Arrays.asList(testTenant, tenant2);
        when(tenantMapper.findAll()).thenReturn(expectedTenants);

        // When
        List<Tenant> result = tenantService.getAllTenants();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedTenants, result);
        verify(tenantMapper, times(1)).findAll();
    }

    @Test
    void getTenantById_WhenTenantExists_ShouldReturnTenant() {
        // Given
        when(tenantMapper.findById(1L)).thenReturn(testTenant);

        // When
        Tenant result = tenantService.getTenantById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testTenant.getId(), result.getId());
        assertEquals(testTenant.getFullName(), result.getFullName());
        verify(tenantMapper, times(1)).findById(1L);
    }

    @Test
    void getTenantById_WhenTenantDoesNotExist_ShouldReturnNull() {
        // Given
        when(tenantMapper.findById(999L)).thenReturn(null);

        // When
        Tenant result = tenantService.getTenantById(999L);

        // Then
        assertNull(result);
        verify(tenantMapper, times(1)).findById(999L);
    }

    @Test
    void createTenant_ShouldCallMapperInsert() {
        // Given
        Tenant newTenant = new Tenant();
        newTenant.setFullName("新規入居者");
        newTenant.setPhone("080-1111-2222");
        newTenant.setEmail("new@example.com");
        doNothing().when(tenantMapper).insert(any(Tenant.class));

        // When
        tenantService.createTenant(newTenant);

        // Then
        verify(tenantMapper, times(1)).insert(newTenant);
    }

    @Test
    void updateTenant_ShouldCallMapperUpdate() {
        // Given
        testTenant.setFullName("更新された名前");
        doNothing().when(tenantMapper).update(any(Tenant.class));

        // When
        tenantService.updateTenant(testTenant);

        // Then
        verify(tenantMapper, times(1)).update(testTenant);
    }

    @Test
    void deleteTenant_ShouldCallMapperDelete() {
        // Given
        Long tenantId = 1L;
        doNothing().when(tenantMapper).delete(anyLong());

        // When
        tenantService.deleteTenant(tenantId);

        // Then
        verify(tenantMapper, times(1)).delete(tenantId);
    }
}

