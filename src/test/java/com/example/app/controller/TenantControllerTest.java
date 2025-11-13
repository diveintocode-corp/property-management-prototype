package com.example.app.controller;

import com.example.app.model.Tenant;
import com.example.app.service.LeaseService;
import com.example.app.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantService tenantService;

    @MockBean
    private LeaseService leaseService;

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
    @WithMockUser
    void list_ShouldReturnTenantListPage() throws Exception {
        // Given
        List<Tenant> tenants = Arrays.asList(testTenant);
        when(tenantService.getAllTenants()).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/tenants"))
                .andExpect(status().isOk())
                .andExpect(view().name("tenants/list"))
                .andExpect(model().attribute("tenants", tenants));
        
        verify(tenantService, times(1)).getAllTenants();
    }

    @Test
    @WithMockUser
    void newForm_ShouldReturnFormPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/tenants/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("tenants/form"))
                .andExpect(model().attributeExists("tenant"));
    }

    @Test
    @WithMockUser
    void editForm_WhenTenantExists_ShouldReturnFormPage() throws Exception {
        // Given
        when(tenantService.getTenantById(1L)).thenReturn(testTenant);
        when(leaseService.getLeasesByTenantId(1L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/tenants/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("tenants/form"))
                .andExpect(model().attribute("tenant", testTenant));
        
        verify(tenantService, times(1)).getTenantById(1L);
        verify(leaseService, times(1)).getLeasesByTenantId(1L);
    }

    @Test
    @WithMockUser
    void editForm_WhenTenantDoesNotExist_ShouldRedirectToList() throws Exception {
        // Given
        when(tenantService.getTenantById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/tenants/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tenants"));
    }

    @Test
    @WithMockUser
    void save_WhenNewTenant_ShouldCreateAndRedirect() throws Exception {
        // Given
        doNothing().when(tenantService).createTenant(any(Tenant.class));

        // When & Then
        mockMvc.perform(post("/tenants")
                .with(csrf())
                .param("fullName", "新規入居者")
                .param("phone", "080-1111-2222")
                .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tenants"))
                .andExpect(flash().attributeExists("message"));
        
        verify(tenantService, times(1)).createTenant(any(Tenant.class));
    }

    @Test
    @WithMockUser
    void save_WhenUpdateTenant_ShouldUpdateAndRedirect() throws Exception {
        // Given
        doNothing().when(tenantService).updateTenant(any(Tenant.class));

        // When & Then
        mockMvc.perform(post("/tenants")
                .with(csrf())
                .param("id", "1")
                .param("fullName", "更新された名前")
                .param("phone", "090-1234-5678")
                .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tenants"))
                .andExpect(flash().attributeExists("message"));
        
        verify(tenantService, times(1)).updateTenant(any(Tenant.class));
    }

    @Test
    @WithMockUser
    void save_WhenValidationFails_ShouldReturnForm() throws Exception {
        // When & Then
        mockMvc.perform(post("/tenants")
                .with(csrf())
                .param("fullName", "") // Invalid: empty name
                .param("email", "invalid-email")) // Invalid email format
                .andExpect(status().isOk())
                .andExpect(view().name("tenants/form"));
        
        verify(tenantService, never()).createTenant(any(Tenant.class));
        verify(tenantService, never()).updateTenant(any(Tenant.class));
    }

    @Test
    @WithMockUser
    void delete_WhenNoLeases_ShouldDeleteAndRedirect() throws Exception {
        // Given
        when(leaseService.getLeasesByTenantId(1L)).thenReturn(Arrays.asList());
        doNothing().when(tenantService).deleteTenant(1L);

        // When & Then
        mockMvc.perform(post("/tenants/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tenants"))
                .andExpect(flash().attributeExists("message"));
        
        verify(leaseService, times(1)).getLeasesByTenantId(1L);
        verify(tenantService, times(1)).deleteTenant(1L);
    }

    @Test
    @WithMockUser
    void delete_WhenLeasesExist_ShouldNotDeleteAndShowError() throws Exception {
        // Given
        com.example.app.model.Lease lease = new com.example.app.model.Lease();
        lease.setId(1L);
        when(leaseService.getLeasesByTenantId(1L)).thenReturn(Arrays.asList(lease));

        // When & Then
        mockMvc.perform(post("/tenants/1/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tenants"))
                .andExpect(flash().attributeExists("error"));
        
        verify(leaseService, times(1)).getLeasesByTenantId(1L);
        verify(tenantService, never()).deleteTenant(anyLong());
    }
}

