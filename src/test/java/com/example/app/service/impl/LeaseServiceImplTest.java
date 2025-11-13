package com.example.app.service.impl;

import com.example.app.mapper.LeaseMapper;
import com.example.app.model.Lease;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaseServiceImplTest {

    @Mock
    private LeaseMapper leaseMapper;

    @InjectMocks
    private LeaseServiceImpl leaseService;

    private Lease testLease;

    @BeforeEach
    void setUp() {
        testLease = new Lease();
        testLease.setId(1L);
        testLease.setPropertyId(1L);
        testLease.setTenantId(1L);
        testLease.setRent(100000);
        testLease.setStartDate(LocalDate.of(2023, 1, 1));
        testLease.setStatus("ACTIVE");
        testLease.setDeposit(200000);
        testLease.setKeymoney(100000);
        testLease.setCreatedAt(LocalDateTime.now());
        testLease.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getLeaseById_WhenLeaseExists_ShouldReturnLease() {
        // Given
        when(leaseMapper.findById(1L)).thenReturn(testLease);

        // When
        Lease result = leaseService.getLeaseById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testLease.getId(), result.getId());
        verify(leaseMapper, times(1)).findById(1L);
    }

    @Test
    void getLeaseById_WhenLeaseDoesNotExist_ShouldReturnNull() {
        // Given
        when(leaseMapper.findById(999L)).thenReturn(null);

        // When
        Lease result = leaseService.getLeaseById(999L);

        // Then
        assertNull(result);
        verify(leaseMapper, times(1)).findById(999L);
    }

    @Test
    void getLeasesByPropertyId_ShouldReturnLeasesForProperty() {
        // Given
        List<Lease> expectedLeases = Arrays.asList(testLease);
        when(leaseMapper.findByPropertyId(1L)).thenReturn(expectedLeases);

        // When
        List<Lease> result = leaseService.getLeasesByPropertyId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedLeases, result);
        verify(leaseMapper, times(1)).findByPropertyId(1L);
    }

    @Test
    void getLeasesByTenantId_ShouldReturnLeasesForTenant() {
        // Given
        List<Lease> expectedLeases = Arrays.asList(testLease);
        when(leaseMapper.findByTenantId(1L)).thenReturn(expectedLeases);

        // When
        List<Lease> result = leaseService.getLeasesByTenantId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedLeases, result);
        verify(leaseMapper, times(1)).findByTenantId(1L);
    }

    @Test
    void createLease_WhenNoActiveLeaseExists_ShouldCreateLease() {
        // Given
        testLease.setId(null); // New lease
        when(leaseMapper.findActiveLeasesByPropertyId(1L)).thenReturn(new ArrayList<>());
        doNothing().when(leaseMapper).insert(any(Lease.class));

        // When
        leaseService.createLease(testLease);

        // Then
        verify(leaseMapper, times(1)).findActiveLeasesByPropertyId(1L);
        verify(leaseMapper, times(1)).insert(testLease);
    }

    @Test
    void createLease_WhenActiveLeaseExists_ShouldThrowException() {
        // Given
        testLease.setId(null); // New lease
        Lease existingLease = new Lease();
        existingLease.setId(2L);
        when(leaseMapper.findActiveLeasesByPropertyId(1L)).thenReturn(Arrays.asList(existingLease));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            leaseService.createLease(testLease);
        });
        assertEquals("Cannot create lease: Property already has an active lease", exception.getMessage());
        verify(leaseMapper, times(1)).findActiveLeasesByPropertyId(1L);
        verify(leaseMapper, never()).insert(any(Lease.class));
    }

    @Test
    void createLease_WhenStatusIsNotActive_ShouldCreateLease() {
        // Given
        testLease.setId(null); // New lease
        testLease.setStatus("ENDED");
        doNothing().when(leaseMapper).insert(any(Lease.class));

        // When
        leaseService.createLease(testLease);

        // Then
        verify(leaseMapper, never()).findActiveLeasesByPropertyId(anyLong());
        verify(leaseMapper, times(1)).insert(testLease);
    }

    @Test
    void updateLease_WhenLeaseExists_ShouldUpdateLease() {
        // Given
        when(leaseMapper.findById(1L)).thenReturn(testLease);
        doNothing().when(leaseMapper).update(any(Lease.class));

        // When
        leaseService.updateLease(testLease);

        // Then
        verify(leaseMapper, times(1)).findById(1L);
        verify(leaseMapper, times(1)).update(testLease);
    }

    @Test
    void updateLease_WhenLeaseDoesNotExist_ShouldThrowException() {
        // Given
        when(leaseMapper.findById(1L)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            leaseService.updateLease(testLease);
        });
        assertEquals("Lease not found", exception.getMessage());
        verify(leaseMapper, times(1)).findById(1L);
        verify(leaseMapper, never()).update(any(Lease.class));
    }

    @Test
    void updateLease_WhenPropertyChangedAndActiveLeaseExists_ShouldThrowException() {
        // Given
        Lease existingLease = new Lease();
        existingLease.setId(1L);
        existingLease.setPropertyId(1L);
        existingLease.setStatus("ACTIVE");
        when(leaseMapper.findById(1L)).thenReturn(existingLease);
        
        testLease.setPropertyId(2L); // Changed property
        testLease.setStatus("ACTIVE");
        Lease conflictingLease = new Lease();
        conflictingLease.setId(3L);
        when(leaseMapper.findActiveLeasesByPropertyId(2L)).thenReturn(Arrays.asList(conflictingLease));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            leaseService.updateLease(testLease);
        });
        assertEquals("Cannot update lease: Property already has an active lease", exception.getMessage());
        verify(leaseMapper, never()).update(any(Lease.class));
    }

    @Test
    void deleteLease_ShouldCallMapperDelete() {
        // Given
        Long leaseId = 1L;
        doNothing().when(leaseMapper).delete(anyLong());

        // When
        leaseService.deleteLease(leaseId);

        // Then
        verify(leaseMapper, times(1)).delete(leaseId);
    }

    @Test
    void hasActiveLeases_WhenActiveLeasesExist_ShouldReturnTrue() {
        // Given
        when(leaseMapper.findActiveLeasesByPropertyId(1L)).thenReturn(Arrays.asList(testLease));

        // When
        boolean result = leaseService.hasActiveLeases(1L);

        // Then
        assertTrue(result);
        verify(leaseMapper, times(1)).findActiveLeasesByPropertyId(1L);
    }

    @Test
    void hasActiveLeases_WhenNoActiveLeasesExist_ShouldReturnFalse() {
        // Given
        when(leaseMapper.findActiveLeasesByPropertyId(1L)).thenReturn(new ArrayList<>());

        // When
        boolean result = leaseService.hasActiveLeases(1L);

        // Then
        assertFalse(result);
        verify(leaseMapper, times(1)).findActiveLeasesByPropertyId(1L);
    }
}

