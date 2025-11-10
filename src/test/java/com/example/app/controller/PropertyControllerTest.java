package com.example.app.controller;

import com.example.app.model.Property;
import com.example.app.service.LeaseService;
import com.example.app.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private LeaseService leaseService;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        testProperty = new Property();
        testProperty.setId(1L);
        testProperty.setName("テスト物件");
        testProperty.setAddress("東京都渋谷区1-2-3");
        testProperty.setArea("25.5");
        testProperty.setRooms("1K");
        testProperty.setCreatedAt(LocalDateTime.now());
        testProperty.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void list_ShouldReturnPropertyListPage() throws Exception {
        // Given
        List<Property> properties = Arrays.asList(testProperty);
        when(propertyService.getAllProperties()).thenReturn(properties);

        // When & Then
        mockMvc.perform(get("/properties"))
                .andExpect(status().isOk())
                .andExpect(view().name("properties/list"))
                .andExpect(model().attribute("properties", properties));
        
        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void detail_WhenPropertyExists_ShouldReturnDetailPage() throws Exception {
        // Given
        when(propertyService.getPropertyById(1L)).thenReturn(testProperty);
        when(leaseService.getLeasesByPropertyId(1L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/properties/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("properties/detail"))
                .andExpect(model().attribute("property", testProperty));
        
        verify(propertyService, times(1)).getPropertyById(1L);
        verify(leaseService, times(1)).getLeasesByPropertyId(1L);
    }

    @Test
    void detail_WhenPropertyDoesNotExist_ShouldRedirectToList() throws Exception {
        // Given
        when(propertyService.getPropertyById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/properties/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"));
        
        verify(propertyService, times(1)).getPropertyById(999L);
    }

    @Test
    void newForm_ShouldReturnFormPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/properties/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("properties/form"))
                .andExpect(model().attributeExists("property"));
    }

    @Test
    void editForm_WhenPropertyExists_ShouldReturnFormPage() throws Exception {
        // Given
        when(propertyService.getPropertyById(1L)).thenReturn(testProperty);

        // When & Then
        mockMvc.perform(get("/properties/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("properties/form"))
                .andExpect(model().attribute("property", testProperty));
        
        verify(propertyService, times(1)).getPropertyById(1L);
    }

    @Test
    void editForm_WhenPropertyDoesNotExist_ShouldRedirectToList() throws Exception {
        // Given
        when(propertyService.getPropertyById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/properties/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"));
    }

    @Test
    void save_WhenNewProperty_ShouldCreateAndRedirect() throws Exception {
        // Given
        Property newProperty = new Property();
        newProperty.setName("新規物件");
        newProperty.setAddress("東京都新宿区1-1-1");
        newProperty.setArea("30.0");
        newProperty.setRooms("1LDK");
        doNothing().when(propertyService).createProperty(any(Property.class));

        // When & Then
        mockMvc.perform(post("/properties")
                .param("name", "新規物件")
                .param("address", "東京都新宿区1-1-1")
                .param("area", "30.0")
                .param("rooms", "1LDK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"))
                .andExpect(flash().attributeExists("message"));
        
        verify(propertyService, times(1)).createProperty(any(Property.class));
    }

    @Test
    void save_WhenUpdateProperty_ShouldUpdateAndRedirect() throws Exception {
        // Given
        testProperty.setName("更新された物件");
        doNothing().when(propertyService).updateProperty(any(Property.class));

        // When & Then
        mockMvc.perform(post("/properties")
                .param("id", "1")
                .param("name", "更新された物件")
                .param("address", "東京都渋谷区1-2-3")
                .param("area", "25.5")
                .param("rooms", "1K"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"))
                .andExpect(flash().attributeExists("message"));
        
        verify(propertyService, times(1)).updateProperty(any(Property.class));
    }

    @Test
    void save_WhenValidationFails_ShouldReturnForm() throws Exception {
        // When & Then
        mockMvc.perform(post("/properties")
                .param("name", "") // Invalid: empty name
                .param("address", "東京都渋谷区1-2-3")
                .param("area", "25.5"))
                .andExpect(status().isOk())
                .andExpect(view().name("properties/form"));
        
        verify(propertyService, never()).createProperty(any(Property.class));
        verify(propertyService, never()).updateProperty(any(Property.class));
    }

    @Test
    void delete_WhenNoActiveLeases_ShouldDeleteAndRedirect() throws Exception {
        // Given
        when(leaseService.hasActiveLeases(1L)).thenReturn(false);
        doNothing().when(propertyService).deleteProperty(1L);

        // When & Then
        mockMvc.perform(post("/properties/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties"))
                .andExpect(flash().attributeExists("message"));
        
        verify(leaseService, times(1)).hasActiveLeases(1L);
        verify(propertyService, times(1)).deleteProperty(1L);
    }

    @Test
    void delete_WhenActiveLeasesExist_ShouldNotDeleteAndShowError() throws Exception {
        // Given
        when(leaseService.hasActiveLeases(1L)).thenReturn(true);
        when(propertyService.getPropertyById(1L)).thenReturn(testProperty);

        // When & Then
        mockMvc.perform(post("/properties/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/1"))
                .andExpect(flash().attributeExists("error"));
        
        verify(leaseService, times(1)).hasActiveLeases(1L);
        verify(propertyService, never()).deleteProperty(anyLong());
    }
}

