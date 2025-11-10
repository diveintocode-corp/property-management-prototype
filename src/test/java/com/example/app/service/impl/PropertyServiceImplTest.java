package com.example.app.service.impl;

import com.example.app.mapper.PropertyMapper;
import com.example.app.model.Property;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyMapper propertyMapper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

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
    void getAllProperties_ShouldReturnAllProperties() {
        // Given
        Property property2 = new Property();
        property2.setId(2L);
        property2.setName("テスト物件2");
        List<Property> expectedProperties = Arrays.asList(testProperty, property2);
        when(propertyMapper.findAll()).thenReturn(expectedProperties);

        // When
        List<Property> result = propertyService.getAllProperties();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedProperties, result);
        verify(propertyMapper, times(1)).findAll();
    }

    @Test
    void getPropertyById_WhenPropertyExists_ShouldReturnProperty() {
        // Given
        when(propertyMapper.findById(1L)).thenReturn(testProperty);

        // When
        Property result = propertyService.getPropertyById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProperty.getId(), result.getId());
        assertEquals(testProperty.getName(), result.getName());
        verify(propertyMapper, times(1)).findById(1L);
    }

    @Test
    void getPropertyById_WhenPropertyDoesNotExist_ShouldReturnNull() {
        // Given
        when(propertyMapper.findById(999L)).thenReturn(null);

        // When
        Property result = propertyService.getPropertyById(999L);

        // Then
        assertNull(result);
        verify(propertyMapper, times(1)).findById(999L);
    }

    @Test
    void createProperty_ShouldCallMapperInsert() {
        // Given
        Property newProperty = new Property();
        newProperty.setName("新規物件");
        newProperty.setAddress("東京都新宿区1-1-1");
        newProperty.setArea("30.0");
        newProperty.setRooms("1LDK");
        doNothing().when(propertyMapper).insert(any(Property.class));

        // When
        propertyService.createProperty(newProperty);

        // Then
        verify(propertyMapper, times(1)).insert(newProperty);
    }

    @Test
    void updateProperty_ShouldCallMapperUpdate() {
        // Given
        testProperty.setName("更新された物件名");
        doNothing().when(propertyMapper).update(any(Property.class));

        // When
        propertyService.updateProperty(testProperty);

        // Then
        verify(propertyMapper, times(1)).update(testProperty);
    }

    @Test
    void deleteProperty_ShouldCallMapperDelete() {
        // Given
        Long propertyId = 1L;
        doNothing().when(propertyMapper).delete(anyLong());

        // When
        propertyService.deleteProperty(propertyId);

        // Then
        verify(propertyMapper, times(1)).delete(propertyId);
    }
}

