package com.example.app.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Lease {
    private Long id;
    
    @NotNull(message = "Property is required")
    private Long propertyId;
    
    @NotNull(message = "Tenant is required")
    private Long tenantId;
    
    @NotNull(message = "Rent amount is required")
    @Positive(message = "Rent must be greater than 0")
    private Integer rent;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Status is required")
    private String status;
    
    @Positive(message = "Deposit must be greater than 0")
    private Integer deposit;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Navigation properties
    private Property property;
    private Tenant tenant;
}