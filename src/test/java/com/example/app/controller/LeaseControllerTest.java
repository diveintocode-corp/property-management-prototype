package com.example.app.controller;

import com.example.app.model.Lease;
import com.example.app.model.Property;
import com.example.app.model.Tenant;
import com.example.app.service.LeaseService;
import com.example.app.service.PropertyService;
import com.example.app.service.TenantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaseController.class)
class LeaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaseService leaseService;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private TenantService tenantService;

    private Lease testLease;
    private Property testProperty;
    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setName("テスト物件");

        testTenant = new Tenant();
        testTenant.setId(1L);
        testTenant.setFullName("山田 太郎");

        testLease = new Lease();
        testLease.setId(1L);
        testLease.setPropertyId(1L);
        testLease.setTenantId(1L);
        testLease.setRent(100000);
        testLease.setStartDate(LocalDate.of(2023, 1, 1));
        testLease.setStatus("ACTIVE");
        testLease.setDeposit(200000);
        testLease.setKeymoney(100000);
        testLease.setProperty(testProperty);
        testLease.setTenant(testTenant);
    }

    @Test
    void newForm_ShouldReturnFormPage() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        List<Tenant> tenants = Arrays.asList(testTenant);
        when(propertyService.getAllProperties()).thenReturn(properties);
        when(tenantService.getAllTenants()).thenReturn(tenants);

        // When & Then
        mockMvc.perform(get("/leases/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("leases/form"))
                .andExpect(model().attributeExists("lease"))
                .andExpect(model().attributeExists("properties"))
                .andExpect(model().attributeExists("tenants"))
                .andExpect(model().attributeExists("statusOptions"));
        
        verify(propertyService, times(1)).getAllProperties();
        verify(tenantService, times(1)).getAllTenants();
    }

    @Test
    void newForm_WithPropertyId_ShouldPreSelectProperty() throws Exception {
        // Given
        when(propertyService.getPropertyById(1L)).thenReturn(testProperty);
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(testTenant));

        // When & Then
        mockMvc.perform(get("/leases/new")
                .param("propertyId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("leases/form"));
        
        verify(propertyService, times(1)).getPropertyById(1L);
    }

    @Test
    void editForm_WhenLeaseExists_ShouldReturnFormPage() throws Exception {
        // Given
        when(leaseService.getLeaseById(1L)).thenReturn(testLease);
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(testTenant));

        // When & Then
        mockMvc.perform(get("/leases/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("leases/form"))
                .andExpect(model().attribute("lease", testLease));
        
        verify(leaseService, times(1)).getLeaseById(1L);
        verify(propertyService, times(1)).getAllProperties();
        verify(tenantService, times(1)).getAllTenants();
    }

    @Test
    void editForm_WhenLeaseDoesNotExist_ShouldRedirect() throws Exception {
        // Given
        when(leaseService.getLeaseById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/leases/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"));
    }

    @Test
    void save_WhenNewLease_ShouldCreateAndRedirect() throws Exception {
        // Given
        doNothing().when(leaseService).createLease(any(Lease.class));
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(testTenant));

        // When & Then
        mockMvc.perform(post("/leases")
                .param("propertyId", "1")
                .param("tenantId", "1")
                .param("rent", "100000")
                .param("startDate", "2023-01-01")
                .param("status", "ACTIVE")
                .param("deposit", "200000")
                .param("keymoney", "100000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/1"))
                .andExpect(flash().attributeExists("message"));
        
        verify(leaseService, times(1)).createLease(any(Lease.class));
    }

    @Test
    void save_WhenUpdateLease_ShouldUpdateAndRedirect() throws Exception {
        // Given
        doNothing().when(leaseService).updateLease(any(Lease.class));
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(testTenant));

        // When & Then
        mockMvc.perform(post("/leases")
                .param("id", "1")
                .param("propertyId", "1")
                .param("tenantId", "1")
                .param("rent", "120000")
                .param("startDate", "2023-01-01")
                .param("status", "ACTIVE")
                .param("deposit", "240000")
                .param("keymoney", "120000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/1"))
                .andExpect(flash().attributeExists("message"));
        
        verify(leaseService, times(1)).updateLease(any(Lease.class));
    }

    @Test
    void save_WhenValidationFails_ShouldReturnForm() throws Exception {
        // Given
        when(propertyService.getAllProperties()).thenReturn(Arrays.asList(testProperty));
        when(tenantService.getAllTenants()).thenReturn(Arrays.asList(testTenant));

        // When & Then
        mockMvc.perform(post("/leases")
                .param("propertyId", "") // Invalid: empty propertyId
                .param("rent", "-1000")) // Invalid: negative rent
                .andExpect(status().isOk())
                .andExpect(view().name("leases/form"));
        
        verify(leaseService, never()).createLease(any(Lease.class));
        verify(leaseService, never()).updateLease(any(Lease.class));
    }

    @Test
    void delete_ShouldDeleteAndRedirect() throws Exception {
        // Given
        when(leaseService.getLeaseById(1L)).thenReturn(testLease);
        doNothing().when(leaseService).deleteLease(1L);

        // When & Then
        mockMvc.perform(post("/leases/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/1"))
                .andExpect(flash().attributeExists("message"));
        
        verify(leaseService, times(1)).getLeaseById(1L);
        verify(leaseService, times(1)).deleteLease(1L);
    }

    @Test
    void delete_WhenLeaseDoesNotExist_ShouldRedirect() throws Exception {
        // Given
        when(leaseService.getLeaseById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/leases/999/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"));
        
        verify(leaseService, times(1)).getLeaseById(999L);
        verify(leaseService, never()).deleteLease(anyLong());
    }
}

